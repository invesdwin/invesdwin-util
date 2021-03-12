package de.invesdwin.util.collections.iterable.buffer;

import java.util.Iterator;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@NotThreadSafe
public abstract class ADelegateBufferingIterator<E> implements IBufferingIterator<E> {

    private IBufferingIterator<E> delegate;

    @Override
    public void close() {
        delegate = EmptyBufferingIterator.getInstance();
    }

    @SuppressWarnings("unchecked")
    private IBufferingIterator<E> getDelegate() {
        if (delegate == null) {
            delegate = (IBufferingIterator<E>) newDelegate();
        }
        return delegate;
    }

    protected abstract IBufferingIterator<? extends E> newDelegate();

    @Override
    public boolean hasNext() {
        return getDelegate().hasNext();
    }

    @Override
    public E next() {
        return getDelegate().next();
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return getDelegate().iterator();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public E getHead() {
        return getDelegate().getHead();
    }

    @Override
    public E getTail() {
        return getDelegate().getTail();
    }

    @Override
    public boolean prepend(final E element) {
        return getDelegate().prepend(element);
    }

    @Override
    public boolean add(final E element) {
        return getDelegate().add(element);
    }

    @Override
    public boolean addAll(final Iterator<? extends E> iterator) {
        return getDelegate().addAll(iterator);
    }

    @Override
    public boolean addAll(final Iterable<? extends E> iterable) {
        return getDelegate().addAll(iterable);
    }

    @Override
    public boolean addAll(final BufferingIterator<E> iterable) {
        return getDelegate().addAll(iterable);
    }

    @Override
    public boolean addAll(final ICloseableIterable<? extends E> iterable) {
        return getDelegate().addAll(iterable);
    }

    @Override
    public boolean addAll(final ICloseableIterator<? extends E> iterator) {
        return getDelegate().addAll(iterator);
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public boolean consume(final Iterable<? extends E> iterable) {
        return getDelegate().consume(iterable);
    }

    @Override
    public boolean consume(final Iterator<? extends E> iterator) {
        return getDelegate().consume(iterator);
    }

    @Override
    public boolean consume(final BufferingIterator<E> iterator) {
        return getDelegate().consume(iterator);
    }

    @Override
    public ICloseableIterable<E> snapshot() {
        return getDelegate().snapshot();
    }

}
