package org.labs.qbit.election.lock;

import org.apache.log4j.NDC;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Copyright (c) 2013, QBit-Labs Inc. (http://qbit-labs.org) All Rights Reserved.
 *
 * QBit-Labs Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
public class ReadWriteLockClient {

    private static final Logger logger = LoggerFactory.getLogger(ReadWriteLockClient.class);
    private static final int NUMBER_OF_CLIENTS = 5;

    private ExecutorService executorService;
    private ZooKeeper zooKeeper;

    public ReadWriteLockClient() throws IOException {
        int numberOfThreads = 20;
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        logger.debug("Executor Service is being initialized with FixedThreadPool [NumberOfThread: {}]", numberOfThreads);
        zooKeeper = new ZooKeeper("localhost:2181", 2000, new ReadWriteWatcher(new Object()));
    }

    private void execute() throws IOException, KeeperException, InterruptedException {

        String lockPath = zooKeeper.create("/_locknode_", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        logger.info("Root Resource Lock Path: [{}]", lockPath);
        final SharedResource sharedResource = new SharedResource(zooKeeper, lockPath);

        List<Callable<Object>> tasksList = new ArrayList<Callable<Object>>();

        //1st Client
        tasksList.add(Executors.callable(new Runnable() {
            @Override
            public void run() {
                NDC.push("1st Client");
                sharedResource.get(1);
                logger.info("DONE 1");
                NDC.pop();
            }
        }));
        //2nd Client
        tasksList.add(Executors.callable(new Runnable() {
            @Override
            public void run() {
                NDC.push("2nd Client");
                sharedResource.get(2);
                sharedResource.put(10, 100);
                sharedResource.put(12, 120);
                logger.info("DONE 2");
                NDC.pop();
            }
        }));
        //3rd Client
        tasksList.add(Executors.callable(new Runnable() {
            @Override
            public void run() {
                NDC.push("3rd Client");
                sharedResource.put(11, 110);
                sharedResource.get(1);
                sharedResource.put(13, 130);
                logger.info("DONE 3");
                NDC.pop();
            }
        }));
        //4th Client
        tasksList.add(Executors.callable(new Runnable() {
            @Override
            public void run() {
                NDC.push("4th Client");
                sharedResource.get(1);
                sharedResource.put(14, 140);
                sharedResource.get(14);
                sharedResource.put(15, 150);
                sharedResource.get(15);
                logger.info("DONE 4");
                NDC.pop();
            }
        }));
        //5th Client
        tasksList.add(Executors.callable(new Runnable() {
            @Override
            public void run() {
                NDC.push("5th Client");
                sharedResource.get(1);
                sharedResource.put(16, 160);
                sharedResource.put(17, 170);
                sharedResource.get(16);
                sharedResource.get(17);
                logger.info("DONE 5");
                NDC.pop();
            }
        }));
        logger.info("Initial State of Shared Resource: {}", sharedResource);
        executorService.invokeAll(tasksList);
        executorService.shutdown();
        logger.info("Final State of Shared Resource: {}", sharedResource);
        delete();
    }

    public void delete() {
        try {
            List<String> children = zooKeeper.getChildren("/_locknode_", false);
            for (String child : children) {
                String chilePath = "/_locknode_/" + child;
                zooKeeper.delete(chilePath, -1);
            }
            zooKeeper.delete("/_locknode_", -1);
        } catch (KeeperException e) {
//            logger.error("An exception occurred", e);
        } catch (InterruptedException e) {
//            logger.error("An exception occurred", e);
        }
        
    }

    public static void main(String[] args) {
        try {
            ReadWriteLockClient readWriteLockClient = new ReadWriteLockClient();
            readWriteLockClient.delete();
            readWriteLockClient.execute();
        } catch (IOException e) {
            logger.error("An exception occurred", e);
        } catch (InterruptedException e) {
            logger.error("An exception occurred", e);
        } catch (KeeperException e) {
            logger.error("An exception occurred", e);
        }
    }
}
