package org.rnorth.testcontainers.containers;

import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerInfo;

/**
 * @author richardnorth
 */
public class MySQLContainer extends AbstractContainer implements DatabaseContainer  {

    private static final String IMAGE = "mysql";
    private final String tag;
    private String mySqlPort;

    public MySQLContainer() {
        this(null);
    }

    public MySQLContainer(String tag) {
        if (tag == null) {
            this.tag = "latest";
        } else {
            this.tag = tag;
        }
    }

    @Override
    protected void containerIsStarting(ContainerInfo containerInfo) {
        mySqlPort = containerInfo.networkSettings().ports().get("3306/tcp").get(0).hostPort();
    }

    @Override
    protected String getLivenessCheckPort() {
        return mySqlPort;
    }

    @Override
    protected ContainerConfig getContainerConfig() {
        return ContainerConfig.builder()
                    .image(getDockerImageName())
                    .exposedPorts("3306")
                    .env("MYSQL_DATABASE=test", "MYSQL_USER=test", "MYSQL_PASSWORD=test", "MYSQL_ROOT_PASSWORD=test")
                    .cmd("mysqld")
                    .build();
    }

    @Override
    protected String getDockerImageName() {
        return IMAGE + ":" + tag;
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:mysql://" + dockerHostIpAddress + ":" + mySqlPort + "/test";
    }
}
