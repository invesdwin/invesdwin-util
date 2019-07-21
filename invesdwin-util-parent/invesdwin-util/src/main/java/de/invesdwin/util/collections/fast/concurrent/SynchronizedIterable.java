package de.invesdwin.util.collections.fast.concurrent;

import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedIterable<E> implements Iterable<E> {

    private final Iterable<E> delegate;

    public SynchronizedIterable(final Iterable<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Iterator<E> iterator() {
        return new SynchronizedIterator<E>(delegate.iterator());
    }

}
