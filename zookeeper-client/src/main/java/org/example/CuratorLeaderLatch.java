package org.example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;

public class CuratorLeaderLatch implements Closeable, LeaderLatchListener {

    private String name;
    private LeaderLatch leaderLatch;

    public CuratorLeaderLatch(CuratorFramework client, String name, String leaderPath) {
        this.leaderLatch = new LeaderLatch(client, leaderPath);
        this.name = name;
    }

    public void runForLeader()
            throws Exception {
        leaderLatch.addListener(this);
        leaderLatch.start();
    }

    public void awaitLeaderShip() throws EOFException, InterruptedException {
        this.leaderLatch.await();
    }

    @Override
    public void isLeader() {
        System.out.println(name + " I am leader");
    }

    @Override
    public void notLeader() {
        System.out.println(name + " I am NOT leader");
    }

    @Override
    public void close() throws IOException {
        leaderLatch.close();
    }
}
