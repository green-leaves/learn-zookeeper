package org.example;


import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.admin.AdminServer;

import java.io.File;
import java.io.IOException;

public class ZooKeeperServerApplication {
    public static void main(String[] args) throws IOException, AdminServer.AdminServerException {
        final File logDir;
        logDir = java.nio.file.Files.createTempDirectory("zookeeper-logs").toFile();
        ServerConfig serverConfig = new ServerConfig();
        String[] configs = {"2181", logDir.getAbsolutePath()};
        serverConfig.parse(configs);
        ZooKeeperServerMain zooKeeperServerMain = new ZooKeeperServerMain();
        zooKeeperServerMain.runFromConfig(serverConfig);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileUtils.deleteDirectory(logDir);
            }
            catch(IOException e) {
                // We tried!
            }
        }));
    }
}