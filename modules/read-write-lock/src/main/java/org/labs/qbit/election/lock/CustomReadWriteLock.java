package org.labs.qbit.election.lock;

import org.apache.zookeeper.ZooKeeper;

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
@Deprecated
public class CustomReadWriteLock {

    private ReadLock readLock;

    private WriteLock writeLock;

    public CustomReadWriteLock(ZooKeeper zooKeeper, String lockBasePath) {
        readLock = new ReadLock(zooKeeper, lockBasePath, LockType.READ);
        writeLock = new WriteLock(zooKeeper, lockBasePath, LockType.WRITE);
    }

    public ReadLock readLock() {
        return readLock;
    }

    public WriteLock writeLock() {
        return writeLock;
    }
}
