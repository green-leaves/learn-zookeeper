package org.example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorLeaderLatchExample {
    private static final String ZOOKEEPER_CONNECTION_STRING = "localhost:2181";
    private static final String LEADER_PATH = "/curator/leader";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory
                .newClient(ZOOKEEPER_CONNECTION_STRING, new ExponentialBackoffRetry(1000, Integer.MAX_VALUE));

        client.start();

        LeaderLatch leaderLatch = new LeaderLatch(client, LEADER_PATH);
        leaderLatch.start();

        leaderLatch.await(); // Block until the instance becomes leader

        if (leaderLatch.hasLeadership()) {
            System.out.println("I am the leader!");
            // Perform leader-specific tasks here
        } else {
            System.out.println("I am not the leader.");
        }

        // Keep the program running until interrupted
        Thread.sleep(Long.MAX_VALUE);

        leaderLatch.close();
        client.close();
    }
}
