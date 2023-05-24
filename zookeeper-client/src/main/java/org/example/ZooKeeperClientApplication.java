package org.example;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ZooKeeperClientApplication {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 500, (Watcher) watchedEvent -> {

        });
        String instancesPath = "/clients";
        if (zooKeeper.exists(instancesPath, false) == null) {
            zooKeeper.create(instancesPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        String instancePath = zooKeeper
                .create(instancesPath + "/instance-",
                        null,
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Registered instance: " + instancePath);

        TimeUnit.MINUTES.sleep(10);
    }
}