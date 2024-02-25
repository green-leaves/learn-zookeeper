package org.example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;

public class LearnCurator {

    private static final String ZOOKEEPER_CONNECTION_STRING = "localhost:2181";
    private static final String LEADER_PATH = "/curator/leader";
    public static void main(String[] args) throws Exception {
//        Thread thread1 = new Thread(new LeaderCandidate("Candidate 1"));
//        Thread thread2 = new Thread(new LeaderCandidate("Candidate 2"));
//        thread1.start();
//        Thread.sleep(3000);
//
//        thread2.start();
//
//        Thread.sleep(3000);

        CuratorFramework client = CuratorFrameworkFactory
                .newClient(ZOOKEEPER_CONNECTION_STRING, new RetryForever(1000));
        client.start();

        CuratorLeaderLatch curatorLeaderLatch = new CuratorLeaderLatch(client, "", LEADER_PATH);
        try {
            curatorLeaderLatch.runForLeader();
            curatorLeaderLatch.awaitLeaderShip();
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            System.out.println("Failed to run for leader");
        } finally {
            System.out.println("Closing ...");
            curatorLeaderLatch.close();
        }



    }

    static class LeaderCandidate implements Runnable {

        private String name;

        public LeaderCandidate(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            CuratorFramework client = CuratorFrameworkFactory
                    .newClient(ZOOKEEPER_CONNECTION_STRING, new RetryForever(1000));
            client.start();

            CuratorLeaderLatch curatorLeaderLatch = new CuratorLeaderLatch(client, name, LEADER_PATH);
            try {
                curatorLeaderLatch.runForLeader();
                curatorLeaderLatch.awaitLeaderShip();
            } catch (Exception e) {
                System.out.println("Failed to run for leader");
            } finally {
                try {
                    Thread.sleep(3000);
                    curatorLeaderLatch.close();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

}
