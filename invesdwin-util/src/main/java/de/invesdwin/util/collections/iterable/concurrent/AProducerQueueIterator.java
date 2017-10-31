package de.invesdwin.util.collections.iterable.concurrent;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public abstract class AProducerQueueIterator<E> extends ACloseableIterator<E> {

    private final class ProducerRunnable implements Runnable {

        @Override
        public void run() {
            try {
                final Consumer<E> consumer = new Consumer<E>() {
                    @Override
                    public void accept(final E t) {
                        onElement(t);
                    }
                };
                AProducerQueueIterator.this.internalProduce(consumer);
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
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AProducerQueueIterator.class);

    private final BlockingQueue<E> queue;
    private volatile boolean innerClosed;
    @GuardedBy("this")
    private E nextElement;
    private final Lock drainedLock = new ReentrantLock();
    @GuardedBy("drainedLock")
    private final Condition drainedCondition = drainedLock.newCondition();
    private final WrappedExecutorService executor;

    private final String name;
    private final int queueSize;

    private boolean utilizationDebugEnabled;

    public AProducerQueueIterator(final String name) {
        this(name, DEFAULT_QUEUE_SIZE);
    }

    public AProducerQueueIterator(final String name, final int queueSize) {
        this.queue = new LinkedBlockingDeque<E>(queueSize);
        this.name = name;
        this.queueSize = queueSize;
        this.executor = Executors.newFixedThreadPool(name, 1);
    }

    protected void start() {
        this.executor.execute(new ProducerRunnable());
        //read first element
        this.nextElement = readNext();
    }

    protected abstract void internalProduce(Consumer<E> consumer);

    protected abstract void internalClose();

    public AProducerQueueIterator<E> withUtilizationDebugEnabled() {
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
            if (curElement == null) {
                throw new NullPointerException("should not happen, since hasNext was called!");
            }
            nextElement = readNext();
            return curElement;
        } else {
            throw new FastNoSuchElementException("ProducerQueueIterator: hasNext is false");
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

    protected boolean isInnerClosed() {
        return innerClosed;
    }

    @Override
    protected void innerClose() {
        if (!innerClosed) {
            innerClosed = true;
            executor.shutdown();
            //cannot wait here for executor to close completely since the thread could trigger it himself
            internalClose();
        }
    }

}