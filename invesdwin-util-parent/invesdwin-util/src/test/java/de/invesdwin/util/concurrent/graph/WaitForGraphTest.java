package de.invesdwin.util.concurrent.graph;

import java.util.concurrent.CountDownLatch;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;

/**
 * Reproduces the precalculated livelock topologies from the 2026-10-30 fleet forensics as thread choreography.
 */
@ThreadSafe
class WaitForGraphTest {

    @Test
    void selfRecursionCycleIsOwnedByOwnDemandTree() throws InterruptedException {
        // topology: demand thread D acquires the key lock, submits the task and joins; the task pins the same key
        // and its nested backtest demands the same key again -> self-recursion cycle
        final String key = WaitForGraph.newKey("PrecalculatedValuesTable_precalculatedValues", "E");
        final WrappedExecutorService pool = Executors.newFixedThreadPool("WaitForGraphTest", 2);
        try {
            final Thread demandThread = Thread.currentThread();
            final CountDownLatch taskPinned = new CountDownLatch(1);
            final CountDownLatch nestedCheckDone = new CountDownLatch(1);
            final boolean[] nestedSawCycle = new boolean[1];
            final boolean[] nestedInTree = new boolean[1];
            // task thread: pins the key, registers itself as running task (submitter unknown here), then performs the
            // nested demand check for the same key
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    WaitForGraph.taskStarted(key, demandThread);
                    WaitForGraph.pinAcquired(key);
                    //mirror trackRemoteUpdate: the wait edge is registered before the cycle check
                    WaitForGraph.beginWait(key);
                    taskPinned.countDown();
                    nestedSawCycle[0] = WaitForGraph.wouldCloseCycle(key);
                    nestedInTree[0] = WaitForGraph.isCycleOwnedByOwnDemandTree(key);
                    WaitForGraph.endWait(key);
                    nestedCheckDone.countDown();
                }
            });
            // demand thread: lock holder + join waiter of the same key
            WaitForGraph.lockAcquired(key);
            WaitForGraph.beginJoinWait(key);
            Assertions.checkTrue(taskPinned.await(5, java.util.concurrent.TimeUnit.SECONDS));
            // the demand thread sees the cycle (its own join edge passes through the task)
            Assertions.checkTrue(WaitForGraph.wouldCloseCycle(key));
            nestedCheckDone.await(5, java.util.concurrent.TimeUnit.SECONDS);
            Assertions.checkTrue(nestedSawCycle[0]);
            Assertions.checkTrue(nestedInTree[0]);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void foreignPinCycleIsNotOwnDemandTree() throws InterruptedException {
        // topology: task for key B is blocked on a pin of key A held by an unrelated foreign thread F with no waits
        final String keyA = "tableX|A";
        final String keyB = "tableX|B";
        final WrappedExecutorService pool = Executors.newFixedThreadPool("WaitForGraphTest", 2);
        final CountDownLatch started = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);
        final boolean[] cycle = new boolean[1];
        final boolean[] inTree = new boolean[1];
        final Thread foreign = new Thread(new Runnable() {
            @Override
            public void run() {
                WaitForGraph.pinAcquired(keyA);
                // holds the pin indefinitely (no waits of its own): foreign leaf
                started.countDown();
                try {
                    Thread.sleep(4000);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                WaitForGraph.pinReleased(keyA, Thread.currentThread());
            }
        }, "foreign-pin-holder");
        foreign.start();
        Assertions.checkTrue(started.await(5, java.util.concurrent.TimeUnit.SECONDS));
        pool.execute(new Runnable() {
            @Override
            public void run() {
                WaitForGraph.taskStarted(keyB, Thread.currentThread());
                WaitForGraph.pinAcquired(keyB);
                // this task's nested demand walks A (foreign pin) then back to B? no back-edge exists -> no cycle;
                // now create one: make the foreign holder wait for keyB instead
                cycle[0] = WaitForGraph.wouldCloseCycle(keyA);
                inTree[0] = WaitForGraph.isCycleOwnedByOwnDemandTree(keyA);
                done.countDown();
            }
        });
        done.await(5, java.util.concurrent.TimeUnit.SECONDS);
        Assertions.checkFalse(cycle[0], "a foreign pin without a path back to the task is not a cycle");
        pool.shutdownNow();
    }

}
