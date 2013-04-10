# Introduction

Implementation of Read Write Lock and Election Algorithm with Apache Zookeeper

# License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

    Developer: Azeem Mumtaz - 138218R

# Prerequisites

* Maven

* Java 1.6

# Build the server

    mvn clean install

# Simulation

* Read Write Lock

    mvn clean install

    find the read-write-lock-1.0.0.jar at modules/read-write-lock/target

    Then execute,

        java -cp read-write-lock-1.0.0.jar org.labs.qbit.election.lock.ReadWriteLockClient

* Leader Election

    mvn clean install

    find leader-election-1.0.0.jar at modules/leader-election/target

    Then execute,

        java -cp leader-election-1.0.0.jar org.labs.qbit.election.leader.ElectionClient

## Pre conditions

    Zookeeper should be up and running on localhost:2181
    Necessary JARs are included within the respective builds.