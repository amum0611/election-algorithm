package org.labs.qbit.election.leader;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;

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
public class LeaderProcedure implements Watcher {

    private static final Logger logger = LoggerFactory.getLogger(LeaderProcedure.class);

    private ZooKeeper zooKeeper;
    private final String zNodeBasePath;
    private final String nodeName;

    private String zNode;
    private String processID;

    public LeaderProcedure(ZooKeeper zooKeeper, String zNodeBasePath, String nodeName) {
        this.zooKeeper = zooKeeper;
        this.zNodeBasePath = zNodeBasePath;
        this.nodeName = nodeName;
        processID = Utility.getUniqueId();
    }

    public void createZNode() {
        try {
            String path = MessageFormat.format("{0}/{1}", zNodeBasePath, nodeName);
            zNode = zooKeeper.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("Created ZNode [{}]", zNode);
            List<String> nodes = zooKeeper.getChildren(zNodeBasePath, this);
            logger.info("List of Child Nodes: {}", nodes);
            int currentPathSequence = parseSequence(zNode);
            boolean alreadyHasALeader = false;
            String leaderNode = zNode;
            for (String node : nodes) {
                int sequence = parseSequence(zNode);
                if (sequence < currentPathSequence) {
                    alreadyHasALeader = true;
                    leaderNode = node;
                }
            }
            if (alreadyHasALeader) {
                logger.info("A Leader node already exist [{}]", leaderNode);
            } else {
                logger.info("The new node is now elected as the leader [{}]", leaderNode);
            }
        } catch (KeeperException e) {
            logger.error("An error occurred", e);
        } catch (InterruptedException e) {
            logger.error("An error occurred", e);
        }
    }

    public void deleteZNode() {
        try {
            logger.info("Read Unlock Request for path [{}]", zNode);
            zooKeeper.delete(zNode, -1);
        } catch (KeeperException e) {
            logger.error("An exception occurred", e);
        } catch (InterruptedException e) {
            logger.error("An exception occurred", e);
        }
    }

    private int parseSequence(String node) {
        String[] split = node.split("-");
        return Integer.parseInt(split[1]);
    }

    @Override
    public void process(WatchedEvent event) {
        logger.info("An event occurred for [{}] for process ID [{}]", event.getPath(), processID);
        if (event.getType() == Watcher.Event.EventType.None) {
            createZNode();
        } else {
            String path = event.getPath();
            if (path != null && path.equals(zNodeBasePath)) {
                try {
                    zooKeeper.getChildren(zNodeBasePath, this);
                } catch (KeeperException e) {
                    logger.error("An error occurred", e);
                } catch (InterruptedException e) {
                    logger.error("An error occurred", e);
                }
            }
        }
    }
}
