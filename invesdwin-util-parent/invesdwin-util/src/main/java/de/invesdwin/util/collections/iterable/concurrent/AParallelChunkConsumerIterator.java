package de.invesdwin.util.collections.iterable.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableFuture;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.lang.finalizer.AFinalizer;

@ThreadSafe
public abstract class AParallelChunkConsumerIterator<R, E> extends ACloseableIterator<E> {

    private static final int DEFAULT_CONSUMER_COUNT = Executors.getCpuThreadPoolCount();
    @GuardedBy("this")
    private final ParallelChunkConsumerIteratorFinalizer<R> finalizer;
    @GuardedBy("this")
    private final List<Future<E>> futures;

    public AParallelChunkConsumerIterator(final String name, final ICloseableIterator<R> requests) {
        this(name, requests, DEFAULT_CONSUMER_COUNT);
    }

    public AParallelChunkConsumerIterator(final String name, final ICloseableIterator<R> requests,
            final int chunkSize) {
        this(name, requests, chunkSize, null);
    }

    public AParallelChunkConsumerIterator(final String name, final ICloseableIterator<R> requests, final int chunkSize,
            final WrappedExecutorService limitExecutor) {
        super(new TextDescription(name));
        this.finalizer = new ParallelChunkConsumerIteratorFinalizer<>(name, requests, chunkSize, limitExecutor);
        this.finalizer.register(this);
        this.futures = new LinkedList<Future<E>>();
    }

    @Override
    protected synchronized boolean innerHasNext() {
        return finalizer.requests.hasNext() || !futures.isEmpty();
    }

    @Override
    protected synchronized E innerNext() {
        while (finalizer.requests.hasNext()
                && (futures.isEmpty() || finalizer.consumerExecutor
                        .getPendingCount() < finalizer.consumerExecutor.getFullPendingCountCondition().getLimit())
                && futures.size() < finalizer.chunkSize) {
            final R request = finalizer.requests.next();
            final Future<E> submit = finalizer.consumerExecutor.submit(() -> {
                final ListenableFuture<E> future = finalizer.limitExecutor.submit(() -> doWork(request));
                return Futures.get(future);
            });
            futures.add(submit);
        }
        if (futures.isEmpty()) {
            throw FastNoSuchElementException.getInstance("AParallelChunkConsumerIterator: futures is empty");
        }
        final Future<E> future = futures.remove(0);
        try {
            final E result = Futures.get(future);
            return result;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw FastNoSuchElementException
                    .getInstance("AParallelChunkConsumerIterator: InterrupedException received");
        }
    }

    protected abstract E doWork(R request);

    private static final class ParallelChunkConsumerIteratorFinalizer<_R> extends AFinalizer {

        private final int chunkSize;
        private ICloseableIterator<_R> requests;
        private WrappedExecutorService limitExecutor;
        private WrappedExecutorService consumerExecutor;

        private ParallelChunkConsumerIteratorFinalizer(final String name, final ICloseableIterator<_R> requests,
                final int chunkSize, final WrappedExecutorService limitExecutor) {
            this.chunkSize = chunkSize;
            this.requests = requests;
            this.consumerExecutor = Executors.newFixedThreadPool(name, chunkSize).setDynamicThreadName(false);
            if (limitExecutor != null) {
                this.limitExecutor = limitExecutor;
            } else {
                this.limitExecutor = Executors.newDisabledExecutor(name);
            }
        }

        @Override
        protected void clean() {
            if (requests != null) {
                requests.close();
                requests = null;
            }
            if (consumerExecutor != null) {
                consumerExecutor.shutdown();
                consumerExecutor = null;
            }
        }

        @Override
        protected boolean isCleaned() {
            return consumerExecutor == null;
        }

        @Override
        public boolean isThreadLocal() {
            return true;
        }

    }

    @Override
    protected void innerClose() {
        finalizer.close();
    }

}
