package de.invesdwin.util.collections.iterable.concurrent;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;

@NotThreadSafe
public class ProducerQueueIterator<E> extends ACloseableIterator<E> {

    private final class ProducerRunnable implements Runnable {

        @Override
        public void run() {
            try {
                while (!innerClosed && producer.hasNext()) {
                    final E next = producer.next();
                    onElement(next);
                }
            } catch (final NoSuchElementException e) {
                innerClose();
            } finally {
                //closing does not prevent queue from getting drained completely
                innerClose();
            }
        }

        private void onElement(final E element) {
            try {
                Assertions.assertThat(element).isNotNull();
                while (!innerClosed) {
                    final boolean added = queue.offer(element);
                    if (!added && queue.remainingCapacity() == 0) {
                        if (utilizationDebugEnabled) {
                            LOGGER.info(String.format("%s: queue is full", name));
                        }
                        drainedLock.lock();
                        try {
                            //wait till queue is drained again, start work immediately when a bit of space is free again
                            while (!innerClosed && queue.size() >= queueSize) {
                                drainedCondition.await(1, TimeUnit.SECONDS);
                            }
                        } finally {
                            drainedLock.unlock();
                        }
                    }
                    if (added) {
                        return;
                    }
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                innerClose();
            }
        }
    }

    public static final int DEFAULT_QUEUE_SIZE = 10000;
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProducerQueueIterator.class);

    private final BlockingQueue<E> queue;
    private volatile boolean innerClosed;
    @GuardedBy("this")
    private E nextElement;
    private final Lock drainedLock = new ReentrantLock();
    @GuardedBy("drainedLock")
    private final Condition drainedCondition = drainedLock.newCondition();
    private final WrappedExecutorService executor;
    private ACloseableIterator<E> producer;

    private final String name;
    private final int queueSize;

    private boolean utilizationDebugEnabled;

    public ProducerQueueIterator(final String name, final ACloseableIterator<E> producer) {
        this(name, producer, DEFAULT_QUEUE_SIZE);
    }

    public ProducerQueueIterator(final String name, final ACloseableIterator<E> producer, final int queueSize) {
        this.producer = producer;
        this.queue = new LinkedBlockingDeque<E>(queueSize);
        this.name = name;
        this.queueSize = queueSize;
        this.executor = Executors.newFixedThreadPool(name, 1);
        this.executor.execute(new ProducerRunnable());
        //read first element
        this.nextElement = readNext();
    }

    public ProducerQueueIterator<E> withUtilizationDebugEnabled() {
        this.utilizationDebugEnabled = true;
        return this;
    }

    public boolean isUtilizationDebugEnabled() {
        return utilizationDebugEnabled;
    }

    @Override
    protected synchronized boolean innerHasNext() {
        final boolean hasNext = !innerClosed || !queue.isEmpty() || nextElement != null;
        if (!hasNext) {
            innerClose();
        }
        return hasNext;
    }

    /*
     * always peek next and return current to prevent reaching end while being in next and thus having to return null or
     * throw NoSuchElementException without the caller expecting this
     */
    @Override
    protected synchronized E innerNext() {
        if (hasNext()) {
            final E curElement = nextElement;
            nextElement = null;
            Assertions.assertThat(curElement).isNotNull();
            nextElement = readNext();
            return curElement;
        } else {
            throw new NoSuchElementException();
        }
    }

    private E readNext() {
        try {
            boolean firstPoll = true;
            while (hasNext()) {
                if (!firstPoll && utilizationDebugEnabled) {
                    LOGGER.info(String.format("%s: queue is empty", name));
                }
                firstPoll = false;
                final E element = queue.poll(1, TimeUnit.SECONDS);
                if (element != null) {
                    drainedLock.lock();
                    try {
                        drainedCondition.signalAll();
                    } finally {
                        drainedLock.unlock();
                    }
                    return element;
                }
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        return null;
    }

    @Override
    protected void innerClose() {
        if (!innerClosed) {
            innerClosed = true;
            executor.shutdown();
            //cannot wait here for executor to close completely since the thread could trigger it himself
            producer.close();
        }
    }

}