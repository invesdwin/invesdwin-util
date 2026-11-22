package de.invesdwin.util.collections.iterable.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.graph.WaitForGraph;
import de.invesdwin.util.error.FastNoSuchElementException;

/**
 * The queue's wait edges come from the real iterator, not from a test calling the graph API by hand.
 * <p>
 * Shape under test (the precalculated / pts fork-dump topology): a consumer thread holds a store key's write lock and
 * polls its producer queue for the next element, while the producer thread - the task that would deliver that element -
 * needs the very lock the consumer holds. The cycle checks in the store layer can only refuse what the graph can SEE,
 * and before both queue edges existed the consumer's wait on an EMPTY queue was invisible: the walk reached the queue,
 * found nobody who owed it progress, and answered "no cycle" while both sides waited forever.
 * <p>
 * The assertion is made on the producer thread, through the producer's own eyes: it polls until the graph reports the
 * cycle, bounded. Deleting either registration in {@code AGenericProducerQueueIterator} (the consumer's wait edge in
 * {@code readNext}, the producer identity in its runnable) leaves that poll unanswered - which is how this cell is
 * mutation-checked.
 */
@NotThreadSafe
public class ProducerQueueIteratorWaitEdgesTest {

    private static final String STORE_KEY = WaitForGraph.newKey("ProducerQueueIteratorWaitEdgesTest_table", "K");

    /**
     * Held on the test INSTANCE on purpose: {@code AProducerQueueIterator}'s constructor calls {@code start()}, which
     * reads the first element, which calls {@code newProducer()} - so the producer iterator is built BEFORE any field
     * of a subclass constructor has been assigned. Local variables captured into such a fixture are still null at that
     * moment; the NPE this cell originally failed with was exactly that, not a defect in the queue edges.
     */
    private final CountDownLatch producerEntered = new CountDownLatch(1);
    private final CountDownLatch producerChecked = new CountDownLatch(1);
    private final CountDownLatch releaseProducer = new CountDownLatch(1);
    private final AtomicBoolean producerSawCycle = new AtomicBoolean();
    private final AtomicReference<Throwable> checkFailure = new AtomicReference<>();
    private final AtomicReference<Throwable> consumerFailure = new AtomicReference<>();

    @Test
    public void testTheQueueRegistersBothEdgesSoItsProducerSeesTheCycle() throws Exception {

        final Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                //the consumer holds the key the producer's work needs, exactly as a store update attempt does while it
                //drains its source queue
                WaitForGraph.lockAcquired(STORE_KEY);
                try {
                    final BlockingProducerQueueIterator iterator = new BlockingProducerQueueIterator();
                    iterator.close();
                } catch (final Throwable t) {
                    consumerFailure.set(t);
                } finally {
                    WaitForGraph.lockReleased(STORE_KEY);
                }
            }
        }, "QueueEdgesTest-consumer");
        consumer.start();

        Assertions.checkTrue(producerEntered.await(10, TimeUnit.SECONDS),
                "precondition: the producer runnable must reach its own iterator; consumerFailure="
                        + consumerFailure.get());
        Assertions.checkTrue(producerChecked.await(15, TimeUnit.SECONDS),
                "precondition: the producer must finish its poll of the graph; checkFailure=" + checkFailure.get()
                        + "; consumerFailure=" + consumerFailure.get());
        Assertions.checkTrue(producerSawCycle.get(),
                "the producer demanding the key held by its own consumer must see the cycle through the queue edges;"
                        + " checkFailure=" + checkFailure.get() + "; graph=" + WaitForGraph.describeCycle(STORE_KEY));

        releaseProducer.countDown();
        consumer.join(15000L);
        Assertions.checkTrue(!consumer.isAlive(),
                "the consumer must finish once the producer is released; consumerFailure=" + consumerFailure.get());
    }

    /** A producer that delivers nothing until released, checking the graph from its own thread. */
    @NotThreadSafe
    private final class BlockingProducerQueueIterator extends AProducerQueueIterator<String> {

        private BlockingProducerQueueIterator() {
            //no fields of this class may be read from newProducer(): the base constructor already drains the queue
            super("QueueEdgesTest");
        }

        @Override
        protected ICloseableIterator<String> newProducer() {
            return new ICloseableIterator<String>() {

                @Override
                public boolean hasNext() {
                    producerEntered.countDown();
                    //the consumer's wait edge exists only while readNext is actually waiting on an empty queue, so
                    //poll with a bound rather than trusting a sleep
                    for (int attempt = 0; attempt < 200 && !producerSawCycle.get(); attempt++) {
                        try {
                            Thread.sleep(25L);
                            producerSawCycle.set(WaitForGraph.wouldCloseCycle(STORE_KEY));
                        } catch (final InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        } catch (final Throwable t) {
                            //a throw here is relayed by the queue to the consumer as a producerException, which would
                            //make this cell fail as a bare timeout with no explanation - capture it instead
                            checkFailure.set(t);
                            break;
                        }
                    }
                    producerChecked.countDown();
                    try {
                        releaseProducer.await(30, TimeUnit.SECONDS);
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return false;
                }

                @Override
                public String next() {
                    throw FastNoSuchElementException.getInstance("QueueEdgesTest producer is exhausted");
                }

                @Override
                public void close() {
                    //nothing to release
                }

            };
        }

    }

}
