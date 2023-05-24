package org.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class InstanceRegistration implements Watcher {

    protected ZooKeeper zooKeeper;
    private String instancesPath;
    private String instanceId;

    public InstanceRegistration(String connectString, String instancesPath) throws IOException {
        this.zooKeeper = new ZooKeeper(connectString, 5000, this);
        this.instancesPath = instancesPath;
        this.instanceId = null;
    }

    public void registerInstance() throws KeeperException, InterruptedException {
        // Create an ephemeral sequential znode for self-registration
        String instancePath = zooKeeper.create(instancesPath + "/instance-", null,
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        this.instanceId = instancePath.substring(instancePath.lastIndexOf('/') + 1);
        System.out.println("Registered instance: " + instanceId);
    }

    void claimSubscriptions(List<String> subscriptions) throws KeeperException, InterruptedException {
        String instanceNodePath = instancesPath + "/" + instanceId;

        // Check if instance already claimed subscriptions
        Stat stat = zooKeeper.exists(instanceNodePath, false);
        if (stat != null) {
            System.out.println("Instance " + instanceId + " has already claimed subscriptions.");
            return;
        }

        // Shuffle the list of subscriptions to randomize claim
        List<String> shuffledSubscriptions = new ArrayList<>(subscriptions);
        java.util.Collections.shuffle(shuffledSubscriptions, ThreadLocalRandom.current());

        // Create the instance znode and set its data to claim the subscriptions
        String instanceData = String.join(",", shuffledSubscriptions);
        zooKeeper.create(instanceNodePath, instanceData.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("Instance " + instanceId + " has claimed subscriptions: " + shuffledSubscriptions);
    }

    @Override
    public void process(WatchedEvent event) {
        // Handle ZooKeeper events if needed
    }

}