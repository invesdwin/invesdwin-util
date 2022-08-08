package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.PeekingIterator;

import de.invesdwin.util.collections.Iterators;

@NotThreadSafe
public class PeekingCloseableIterator<E> implements ICloseableIterator<E>, PeekingIterator<E> {

    private final ICloseableIterator<? extends E> delegate;
    private final PeekingIterator<E> peekingDelegate;

    public PeekingCloseableIterator(final ICloseableIterator<? extends E> delegate) {
        this.delegate = delegate;
        this.peekingDelegate = Iterators.peekingIterator(delegate);
    }

    @Override
    public boolean hasNext() {
        return peekingDelegate.hasNext();
    }

    @Override
    public E peek() {
        return peekingDelegate.peek();
    }

    @Override
    public E next() {
        return peekingDelegate.next();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void remove() {
        peekingDelegate.remove();
    }

}
