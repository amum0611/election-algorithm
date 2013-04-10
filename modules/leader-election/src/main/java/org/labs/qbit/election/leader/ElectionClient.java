package org.labs.qbit.election.leader;

import org.apache.log4j.NDC;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (c) 2013, QBit-Labs Inc. (http://qbit-labs.org) All Rights Reserved.
 * <p/>
 * QBit-Labs Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
public class ElectionClient {

    private static final Logger logger = LoggerFactory.getLogger(ElectionClient.class);

    private ExecutorService executorService;
    private ZooKeeper zooKeeper;

    public ElectionClient() throws IOException {
        int numberOfThreads = 20;
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        logger.debug("Executor Service is being initialized with FixedThreadPool [NumberOfThread: {}]", numberOfThreads);
        zooKeeper = new ZooKeeper("localhost:2181", 2000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                logger.debug("An event occurred on root path");
            }
        });
    }

    public void execute() throws KeeperException, InterruptedException {
        String electionPath = zooKeeper.create("/election", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        logger.info("Root Resource Lock Path: [{}]", electionPath);

        List<Callable<Object>> tasksList = new ArrayList<Callable<Object>>();
        //1st Client
        tasksList.add(Executors.callable(new Runnable() {
            @Override
            public void run() {
                NDC.push("1st Client");
                LeaderProcedure leaderProcedure = new LeaderProcedure(zooKeeper, "/election", "z-");
                leaderProcedure.createZNode();
                logger.info("DONE 1");
                NDC.pop();
            }
        }));
        //2nd Client
        tasksList.add(Executors.callable(new Runnable() {
            @Override
            public void run() {
                NDC.push("2nd Client");
                LeaderProcedure leaderProcedure = new LeaderProcedure(zooKeeper, "/election", "z-");
                leaderProcedure.createZNode();
                logger.info("DONE 2");
                NDC.pop();
            }
        }));
        //3rd Client
        tasksList.add(Executors.callable(new Runnable() {
            @Override
            public void run() {
                NDC.push("3rd Client");
                LeaderProcedure leaderProcedure = new LeaderProcedure(zooKeeper, "/election", "z-");
                leaderProcedure.createZNode();
                logger.info("DONE 3");
                NDC.pop();
            }
        }));
        //4th Client
        tasksList.add(Executors.callable(new Runnable() {
            @Override
            public void run() {
                NDC.push("4th Client");
                LeaderProcedure leaderProcedure = new LeaderProcedure(zooKeeper, "/election", "z-");
                leaderProcedure.createZNode();
                logger.info("DONE 4");
                NDC.pop();
            }
        }));
        //5th Client
        tasksList.add(Executors.callable(new Runnable() {
            @Override
            public void run() {
                NDC.push("5th Client");
                LeaderProcedure leaderProcedure = new LeaderProcedure(zooKeeper, "/election", "z-");
                leaderProcedure.createZNode();
                logger.info("DONE 5");
                NDC.pop();
            }
        }));
        executorService.invokeAll(tasksList);
        executorService.shutdown();
        delete();
    }

    public void delete() {
        try {
            List<String> children = zooKeeper.getChildren("/election", false);
            for (String child : children) {
                String chilePath = "/election/" + child;
                zooKeeper.delete(chilePath, -1);
            }
            zooKeeper.delete("/election", -1);
        } catch (KeeperException e) {
            //logger.error("An exception occurred", e);
        } catch (InterruptedException e) {
            //logger.error("An exception occurred", e);
        }

    }

    public static void main(String[] args) {
        try {
            ElectionClient electionClient = new ElectionClient();
            electionClient.delete();
            electionClient.execute();
        } catch (IOException e) {
            logger.error("An exception occurred", e);
        } catch (InterruptedException e) {
            logger.error("An exception occurred", e);
        } catch (KeeperException e) {
            logger.error("An exception occurred", e);
        }
    }
}
