package de.invesdwin.util.collections.iterable.buffer;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

@Immutable
public final class EmptyBufferingIterator<E> implements IBufferingIterator<E> {

    private static final EmptyBufferingIterator<?> INSTANCE = new EmptyBufferingIterator<>();

    private EmptyBufferingIterator() {}

    @SuppressWarnings("unchecked")
    public static <T> EmptyBufferingIterator<T> getInstance() {
        return (EmptyBufferingIterator<T>) INSTANCE;
    }

    @Override
    public void close() {}

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E next() {
        throw new FastNoSuchElementException("EmptyBufferingIterator is always empty");
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public E getHead() {
        return null;
    }

    @Override
    public E getTail() {
        return null;
    }

    @Override
    public boolean prepend(final E element) {
        return false;
    }

    @Override
    public boolean add(final E element) {
        return false;
    }

    @Override
    public boolean addAll(final Iterator<? extends E> iterator) {
        return false;
    }

    @Override
    public boolean addAll(final Iterable<? extends E> iterable) {
        return false;
    }

    @Override
    public boolean addAll(final BufferingIterator<E> iterable) {
        return false;
    }

    @Override
    public void clear() {}

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean consume(final Iterable<? extends E> iterable) {
        return false;
    }

    @Override
    public boolean consume(final Iterator<? extends E> iterator) {
        return false;
    }

    @Override
    public boolean consume(final BufferingIterator<E> iterator) {
        return false;
    }

}
