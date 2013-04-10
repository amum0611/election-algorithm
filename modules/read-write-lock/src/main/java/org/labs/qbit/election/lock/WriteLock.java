package org.labs.qbit.election.lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

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
public class WriteLock implements Lock, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(WriteLock.class);

    private final ZooKeeper zooKeeper;
    private final String lockBasePath;
    private final String lockName;
    private final Object mutex;
    private String lockPath;
    private ReadWriteWatcher watcher;

    public WriteLock(ZooKeeper zooKeeper, String lockBasePath, LockType lockType) {
        this.zooKeeper = zooKeeper;
        this.lockBasePath = lockBasePath;
        this.lockName = lockType.getValue();
        this.mutex = new Object();
        watcher = new ReadWriteWatcher(mutex);
    }

    @Override
    public void lock() {
        try {
            String path = MessageFormat.format("{0}/{1}", lockBasePath, lockName);
            lockPath = zooKeeper.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("Write LockPath: [{}]", lockPath);
            synchronized(mutex) {
                while(true) {
                    List<String> nodes = zooKeeper.getChildren(lockBasePath, watcher);
                    logger.info("List of Child Nodes: {}", nodes);
                    boolean alreadyLocked = false;
                    int currentSequence = Integer.parseInt(lockPath.split("-")[1]);
                    for (String node : nodes) {
                        String[] split = node.split("-");
                        if (LockType.READ.getValue().equals(split[0] + "-")) {
                            continue;
                        }
                        int sequence = Integer.parseInt(split[1]);
                        if (sequence < currentSequence) {
                            alreadyLocked = true;
                        }
                    }
                    if (!alreadyLocked) {
                        logger.info("Requested client [{}], holds the write lock, since no client holds it", lockPath);
                        return;
                    }
                    logger.info("Requested client [{}], done NOT holds the write lock. Please wait!!!", lockPath);
                    mutex.wait();
                }
            }
        } catch (KeeperException e) {
            logger.error("An exception occurred", e);
        } catch (InterruptedException e) {
            logger.error("An exception occurred", e);
        }

    }

    @Override
    public void unlock() {
        try {
            logger.info("Write Unlock Request for path [{}]", lockPath);
            zooKeeper.delete(lockPath, -1);
        } catch (KeeperException e) {
            logger.error("An exception occurred", e);
        } catch (InterruptedException e) {
            logger.error("An exception occurred", e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
