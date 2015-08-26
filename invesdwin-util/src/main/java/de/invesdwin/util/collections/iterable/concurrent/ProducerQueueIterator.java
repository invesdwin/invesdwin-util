package de.invesdwin.util.collections.iterable.concurrent;

import java.io.IOException;
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
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;

@NotThreadSafe
public class ProducerQueueIterator<E> implements ICloseableIterator<E> {

    private final class ProducerRunnable implements Runnable {

        @Override
        public void run() {
            try {
                while (!closed && producer.hasNext()) {
                    final E next = producer.next();
                    onElement(next);
                }
            } catch (final NoSuchElementException e) {
                close();
            } finally {
                //closing does not prevent queue from getting drained completely
                close();
            }
        }

        private void onElement(final E element) {
            try {
                Assertions.assertThat(element).isNotNull();
                while (!closed) {
                    final boolean added = queue.offer(element);
                    if (!added && queue.remainingCapacity() == 0) {
                        if (debugEnabled) {
                            System.out.println(String.format("%s: queue is full", name)); //SUPPRESS CHECKSTYLE single line
                        }
                        drainedLock.lock();
                        try {
                            //wait till queue is drained again, start work immediately when a bit of space is free again
                            while (!closed && queue.size() >= queueSize) {
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
                close();
            }
        }
    }

    public static final int DEFAULT_QUEUE_SIZE = 10000;

    private final BlockingQueue<E> queue;
    private volatile boolean closed;
    @GuardedBy("this")
    private E nextElement;
    private final Lock drainedLock = new ReentrantLock();
    @GuardedBy("drainedLock")
    private final Condition drainedCondition = drainedLock.newCondition();
    private final WrappedExecutorService executor;
    private ICloseableIterator<E> producer;

    private final String name;
    private final int queueSize;

    private boolean debugEnabled;

    public ProducerQueueIterator(final String name, final ICloseableIterator<E> producer) {
        this(name, producer, DEFAULT_QUEUE_SIZE);
    }

    public ProducerQueueIterator(final String name, final ICloseableIterator<E> producer, final int queueSize) {
        this.producer = producer;
        this.queue = new LinkedBlockingDeque<E>(queueSize);
        this.name = name;
        this.queueSize = queueSize;
        this.executor = Executors.newFixedThreadPool(name, 1);
        this.executor.execute(new ProducerRunnable());
        //read first element
        this.nextElement = readNext();
    }

    public ProducerQueueIterator<E> withDebugEnabled() {
        this.debugEnabled = true;
        return this;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    @Override
    public synchronized boolean hasNext() {
        final boolean hasNext = !closed || !queue.isEmpty() || nextElement != null;
        if (!hasNext) {
            close();
        }
        return hasNext;
    }

    /*
     * always peek next and return current to prevent reaching end while being in next and thus having to return null or
     * throw NoSuchElementException without the caller expecting this
     */
    @Override
    public synchronized E next() {
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
                if (!firstPoll && debugEnabled) {
                    System.out.println(String.format("%s: queue is empty", name)); //SUPPRESS CHECKSTYLE single line
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
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            executor.shutdown();
            //cannot wait here for executor to close completely since the thread could trigger it himself
            try {
                producer.close();
            } catch (final IOException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

}