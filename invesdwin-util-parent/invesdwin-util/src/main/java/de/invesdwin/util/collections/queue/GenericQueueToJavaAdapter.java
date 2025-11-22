package de.invesdwin.util.collections.queue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.lang.Objects;

@Immutable
public class GenericQueueToJavaAdapter<E> implements Queue<E>, IGenericQueue<E> {

    private final IGenericQueue<E> delegate;

    public GenericQueueToJavaAdapter(final IGenericQueue<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public Object[] toArray() {
        return Objects.EMPTY_ARRAY;
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return (T[]) Objects.EMPTY_ARRAY;
    }

    @Override
    public boolean remove(final Object o) {
        return false;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean offer(final E e) {
        return delegate.offer(e);
    }

    @Override
    public boolean add(final E e) {
        return delegate.add(e);
    }

    @Override
    public E remove() {
        return delegate.remove();
    }

    @Override
    public E poll() {
        return delegate.poll();
    }

    @Override
    public E element() {
        return delegate.element();
    }

    @Override
    public E peek() {
        return delegate.peek();
    }

}
