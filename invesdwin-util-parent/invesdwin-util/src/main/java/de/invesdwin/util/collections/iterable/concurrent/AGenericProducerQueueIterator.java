package de.invesdwin.util.collections.iterable.concurrent;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.lang.finalizer.AFinalizer;

@NotThreadSafe
public abstract class AGenericProducerQueueIterator<E> extends ACloseableIterator<E> {

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
                AGenericProducerQueueIterator.this.internalProduce(consumer);
            } catch (final NoSuchElementException e) {
                finalizer.close();
                internalCloseProducer();
            } finally {
                //closing does not prevent queue from getting drained completely
                finalizer.close();
                internalCloseProducer();
            }
        }

        private void onElement(final E element) {
            try {
                Assertions.assertThat(element).isNotNull();
                while (!isInnerClosed()) {
                    final boolean added = queue.offer(element);
                    if (!added && queue.remainingCapacity() == 0) {
                        if (utilizationDebugEnabled) {
                            LOGGER.info(String.format("%s: queue is full", finalizer.name));
                        }
                        drainedLock.lock();
                        try {
                            //wait till queue is drained again, start work immediately when a bit of space is free again
                            while (!isInnerClosed() && queue.size() >= queueSize) {
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
                finalizer.close();
                internalCloseProducer();
            }
        }
    }

    public static final int DEFAULT_QUEUE_SIZE = 10000;
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(AGenericProducerQueueIterator.class);

    private final BlockingQueue<E> queue;
    private final GenericProducerQueueIteratorFinalizer finalizer;

    @GuardedBy("this")
    private E nextElement;
    private final Lock drainedLock;
    @GuardedBy("drainedLock")
    private final Condition drainedCondition;

    private final int queueSize;

    private boolean utilizationDebugEnabled;

    public AGenericProducerQueueIterator(final String name) {
        this(name, DEFAULT_QUEUE_SIZE);
    }

    public AGenericProducerQueueIterator(final String name, final int queueSize) {
        this.finalizer = new GenericProducerQueueIteratorFinalizer(name);
        registerFinalizer(finalizer);
        this.queue = new LinkedBlockingDeque<E>(queueSize);
        this.queueSize = queueSize;
        this.drainedLock = Locks
                .newReentrantLock(AGenericProducerQueueIterator.class.getSimpleName() + "_" + name + "_drainedLock");
        this.drainedCondition = drainedLock.newCondition();
    }

    protected void start() {
        finalizer.started = true;
        finalizer.executor.execute(new ProducerRunnable());
        //read first element
        this.nextElement = readNext();
    }

    protected abstract void internalProduce(Consumer<E> consumer);

    /**
     * Only the opening thread is supposed to close the producer.
     */
    protected abstract void internalCloseProducer();

    public AGenericProducerQueueIterator<E> withUtilizationDebugEnabled() {
        this.utilizationDebugEnabled = true;
        return this;
    }

    public boolean isUtilizationDebugEnabled() {
        return utilizationDebugEnabled;
    }

    @Override
    protected synchronized boolean innerHasNext() {
        final boolean hasNext = !isInnerClosed() || !queue.isEmpty() || nextElement != null;
        if (!hasNext) {
            finalizer.close();
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
                    LOGGER.info(String.format("%s: queue is empty", finalizer.name));
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
        return finalizer.isClosed();
    }

    private static final class GenericProducerQueueIteratorFinalizer extends AFinalizer {

        private final String name;
        private WrappedExecutorService executor;
        private boolean started;
        private volatile boolean closed;

        private GenericProducerQueueIteratorFinalizer(final String name) {
            this.name = name;
            this.executor = Executors.newFixedThreadPool(name, 1);
        }

        @Override
        protected void onClose() {
            if (!started) {
                throw new IllegalStateException("start() was forgotten to be called right after the constructor");
            }
        }

        @Override
        protected void clean() {
            //cannot wait here for executor to close completely since the thread could trigger it himself
            executor.shutdown();
            executor = null;
            closed = true;
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

    }

}