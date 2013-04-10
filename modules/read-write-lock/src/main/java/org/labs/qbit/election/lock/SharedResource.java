package org.labs.qbit.election.lock;

import org.apache.zookeeper.ZooKeeper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

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
public class SharedResource {

    private static Map<Integer, Integer> map;

    private final ZooKeeper zooKeeper;
    private final String lockBasePath;

    public SharedResource(ZooKeeper zooKeeper, String lockBasePath) {
        this.zooKeeper = zooKeeper;
        this.lockBasePath = lockBasePath;
    }

    static {
        SharedResource.map = new HashMap<Integer, Integer>();
        map.put(1, 11);
        map.put(2, 22);
        map.put(3, 33);
        map.put(4, 44);
        map.put(5, 55);
        map.put(6, 66);
        map.put(7, 77);
    }

    public Integer get(int key) {
        Lock readLock = new ReadLock(zooKeeper, lockBasePath, LockType.READ);
        readLock.lock();
        try {
            return SharedResource.map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public void put(int key, int value) {
        Lock writeLock = new WriteLock(zooKeeper, lockBasePath, LockType.WRITE);
        writeLock.lock();
        try {
            SharedResource.map.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String toString() {
        return SharedResource.map.toString();
    }
}
