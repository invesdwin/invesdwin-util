package de.invesdwin.util.collections.iterable.concurrent;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.concurrent.WrappedExecutorService;

@Immutable
public abstract class AParallelChunkConsumerIterable<R, E> implements ICloseableIterable<E> {

    public static final int DEFAULT_CHUNK_SIZE = 10000;

    private final String name;
    private final ICloseableIterable<R> requests;
    private final int chunkSize;
    private final WrappedExecutorService executor;

    public AParallelChunkConsumerIterable(final String name, final ICloseableIterable<R> requests) {
        this(name, requests, DEFAULT_CHUNK_SIZE);
    }

    public AParallelChunkConsumerIterable(final String name, final ICloseableIterable<R> requests,
            final int chunkSize) {
        this(name, requests, chunkSize, null);
    }

    public AParallelChunkConsumerIterable(final String name, final ICloseableIterable<R> requests, final int chunkSize,
            final WrappedExecutorService executor) {
        this.name = name;
        this.requests = requests;
        this.chunkSize = chunkSize;
        this.executor = executor;
    }

    @Override
    public ACloseableIterator<E> iterator() {
        return new AParallelChunkConsumerIterator<R, E>(name, requests.iterator(), chunkSize, executor) {
            @Override
            protected E doWork(final R request) {
                return AParallelChunkConsumerIterable.this.doWork(request);
            }
        };
    }

    protected abstract E doWork(R request);

}
