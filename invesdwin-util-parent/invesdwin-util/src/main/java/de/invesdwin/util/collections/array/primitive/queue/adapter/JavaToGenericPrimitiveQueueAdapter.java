package de.invesdwin.util.collections.array.primitive.queue.adapter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.primitive.queue.IGenericPrimitiveQueue;

@Immutable
public class JavaToGenericPrimitiveQueueAdapter<E> implements Queue<E>, IGenericPrimitiveQueue<E> {

    private final Queue<E> delegate;

    public JavaToGenericPrimitiveQueueAdapter(final Queue<E> delegate) {
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
        return delegate.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean remove(final Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        return delegate.addAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
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
