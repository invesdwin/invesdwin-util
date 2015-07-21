package de.invesdwin.util.collections.iterable.concurrent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.Futures;
import de.invesdwin.util.concurrent.WrappedExecutorService;

@ThreadSafe
public abstract class AParallelChunkConsumerIterator<R, E> implements ICloseableIterator<E> {

    private static final int DEFAULT_CONSUMER_COUNT = Executors.getCpuThreadPoolCount();
    private final ICloseableIterator<R> requests;
    private final WrappedExecutorService consumerExecutor;
    @GuardedBy("this")
    private final List<Future<E>> futures;
    private final int chunkSize;
    private boolean closed;

    public AParallelChunkConsumerIterator(final String name, final ICloseableIterator<R> requests) {
        this(name, requests, DEFAULT_CONSUMER_COUNT);
    }

    public AParallelChunkConsumerIterator(final String name, final ICloseableIterator<R> requests, final int chunkSize) {
        this.chunkSize = chunkSize;
        this.requests = requests;
        this.consumerExecutor = Executors.newFixedThreadPool(name, chunkSize);
        this.futures = new ArrayList<Future<E>>(chunkSize);
    }

    @Override
    public boolean hasNext() {
        return requests.hasNext() || !futures.isEmpty();
    }

    @Override
    public synchronized E next() {
        while (requests.hasNext() && consumerExecutor.getPendingCount() < consumerExecutor.getFullPendingCount()
                && futures.size() < chunkSize) {
            final R request = requests.next();
            final Future<E> submit = consumerExecutor.submit(new Callable<E>() {
                @Override
                public E call() throws Exception {
                    return doWork(request);
                }
            });
            futures.add(submit);
        }
        if (futures.isEmpty()) {
            throw new NoSuchElementException();
        }
        final Future<E> future = futures.remove(0);
        try {
            return Futures.get(future);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    protected abstract E doWork(R request);

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            requests.close();
            consumerExecutor.shutdown();
        }
    }

}
