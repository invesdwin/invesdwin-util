package de.invesdwin.util.concurrent.graph;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    /**
     * The queue's two edges, taken together. Topology (the precalculated / pts fork-dump shape): the consumer thread C
     * holds a store key's write lock and polls its queue for the next element, while the producer thread P - the task
     * that would deliver that element - is blocked demanding the same store key. C's wait edge on the queue and the
     * queue's producer identity are BOTH needed for the cycle to close: with only one of them, each side waits and the
     * cycle checks at ALazyDataUpdater/ATimeSeriesUpdater answer "no cycle" forever.
     */
    @Test
    void queueEdgesCloseTheCycleBetweenAConsumerAndItsProducer() throws InterruptedException {
        final String storeKey = WaitForGraph.newKey("TimeSeriesDB_pre", "E");
        final String queueKey = "queue:pre@" + Integer.toHexString(System.identityHashCode(storeKey));
        final WrappedExecutorService pool = Executors.newFixedThreadPool("WaitForGraphTest", 1);
        try {
            //consumer side: this thread holds the store key and waits on the queue
            WaitForGraph.lockAcquired(storeKey);
            WaitForGraph.registerQueueConsumer(queueKey);
            WaitForGraph.beginWait(queueKey);

            final CountDownLatch checked = new CountDownLatch(1);
            final boolean[] producerSawCycle = new boolean[1];
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    WaitForGraph.registerQueueProducer(queueKey);
                    producerSawCycle[0] = WaitForGraph.wouldCloseCycle(storeKey);
                    checked.countDown();
                }
            });
            Assertions.checkTrue(checked.await(5, TimeUnit.SECONDS), "the producer check must complete");
            Assertions.checkTrue(producerSawCycle[0],
                    "the producer demanding the key held by its own consumer must see the cycle; graph: "
                            + WaitForGraph.describeCycle(storeKey));
            Assertions.checkTrue(WaitForGraph.wouldCloseCycle(queueKey),
                    "and the same cycle must be visible from the consumer's side of the queue");
        } finally {
            WaitForGraph.endWait(queueKey);
            WaitForGraph.lockReleased(storeKey);
            pool.shutdownNow();
        }
    }

    /**
     * The consumer edge on its own must NOT report a cycle - the producer identity is what closes it. Pinned so a
     * half-applied fix (one edge registered, the other forgotten) cannot pass the cell above by accident.
     */
    @Test
    void aConsumerQueueEdgeWithoutAProducerIdentityIsNotACycle() throws InterruptedException {
        final String storeKey = WaitForGraph.newKey("TimeSeriesDB_half", "H");
        final String queueKey = "queue:half@" + Integer.toHexString(System.identityHashCode(storeKey));
        final WrappedExecutorService pool = Executors.newFixedThreadPool("WaitForGraphTest", 1);
        try {
            WaitForGraph.lockAcquired(storeKey);
            WaitForGraph.registerQueueConsumer(queueKey);
            WaitForGraph.beginWait(queueKey);

            final CountDownLatch checked = new CountDownLatch(1);
            final boolean[] sawCycle = new boolean[1];
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    //no registerQueueProducer here on purpose
                    sawCycle[0] = WaitForGraph.wouldCloseCycle(storeKey);
                    checked.countDown();
                }
            });
            Assertions.checkTrue(checked.await(5, TimeUnit.SECONDS), "the check must complete");
            Assertions.checkFalse(sawCycle[0],
                    "without the queue's producer edge the walk cannot return to the requesting thread");
        } finally {
            WaitForGraph.endWait(queueKey);
            WaitForGraph.lockReleased(storeKey);
            pool.shutdownNow();
        }
    }

    /**
     * No false positive: a queue whose producer waits on a key held by an unrelated, non-waiting thread is not a cycle,
     * so the new edges must not start refusing healthy waits.
     */
    @Test
    void aProducerWaitingOnAnUnrelatedHolderIsNotReportedAsACycle() throws InterruptedException {
        final String storeKey = WaitForGraph.newKey("TimeSeriesDB_unrelated", "U");
        final String queueKey = "queue:unrelated@" + Integer.toHexString(System.identityHashCode(storeKey));
        final CountDownLatch holderInside = new CountDownLatch(1);
        final CountDownLatch releaseHolder = new CountDownLatch(1);
        final Thread holder = new Thread(new Runnable() {
            @Override
            public void run() {
                WaitForGraph.lockAcquired(storeKey);
                holderInside.countDown();
                try {
                    releaseHolder.await(5, TimeUnit.SECONDS);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                WaitForGraph.lockReleased(storeKey);
            }
        }, "unrelated-lock-holder");
        holder.start();
        Assertions.checkTrue(holderInside.await(5, TimeUnit.SECONDS), "precondition: an unrelated thread holds the key");
        try {
            //a consumer waiting on its queue, and a producer that wants a key held by someone with no waits of its own
            WaitForGraph.registerQueueConsumer(queueKey);
            WaitForGraph.beginWait(queueKey);
            WaitForGraph.registerQueueProducer(queueKey);
            Assertions.checkFalse(WaitForGraph.wouldCloseCycle(storeKey),
                    "the queue's edges must not turn a healthy wait for an unrelated holder into a cycle");
        } finally {
            WaitForGraph.endWait(queueKey);
            releaseHolder.countDown();
            holder.join(5000L);
        }
    }

}
