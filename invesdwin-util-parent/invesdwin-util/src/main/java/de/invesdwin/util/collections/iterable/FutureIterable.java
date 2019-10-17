package de.invesdwin.util.collections.iterable;

import java.util.concurrent.Future;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.future.Futures;

@ThreadSafe
public class FutureIterable<E> implements ICloseableIterable<E> {

    private final Future<? extends ICloseableIterable<? extends E>> future;

    public FutureIterable(final Future<? extends ICloseableIterable<? extends E>> future) {
        this.future = future;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ICloseableIterator<E> iterator() {
        try {
            return (ICloseableIterator<E>) Futures.get(future).iterator();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
