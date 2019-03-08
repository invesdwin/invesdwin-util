package de.invesdwin.util.collections.iterable.concurrent;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterable;

@Immutable
public abstract class AParallelChunkConsumerIterable<R, E> implements ICloseableIterable<E> {

    public static final int DEFAULT_CHUNK_SIZE = 10000;

    private final String name;
    private final ICloseableIterable<R> requests;
    private final int chunkSize;

    public AParallelChunkConsumerIterable(final String name, final ICloseableIterable<R> requests) {
        this(name, requests, DEFAULT_CHUNK_SIZE);
    }

    public AParallelChunkConsumerIterable(final String name, final ICloseableIterable<R> requests, final int chunkSize) {
        this.name = name;
        this.requests = requests;
        this.chunkSize = chunkSize;
    }

    @Override
    public ACloseableIterator<E> iterator() {
        return new AParallelChunkConsumerIterator<R, E>(name, requests.iterator(), chunkSize) {
            @Override
            protected E doWork(final R request) {
                return AParallelChunkConsumerIterable.this.doWork(request);
            }
        };
    }

    protected abstract E doWork(R request);

}
