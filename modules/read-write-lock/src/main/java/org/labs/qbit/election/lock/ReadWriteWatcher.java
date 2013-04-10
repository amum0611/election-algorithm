package org.labs.qbit.election.lock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.proto.WatcherEvent;

import java.text.MessageFormat;

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
public class ReadWriteWatcher implements Watcher {

    private final Object mutex;

    public ReadWriteWatcher(Object mutex) {
        this.mutex = mutex;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        synchronized (mutex) {
            mutex.notifyAll();
        }
    }
}
