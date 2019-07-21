package de.invesdwin.util.collections.fast.concurrent;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedList<E> extends SynchronizedCollection<E> implements List<E> {

    public SynchronizedList(final List<E> delegate) {
        super(delegate);
    }

    public SynchronizedList(final List<E> delegate, final Object lock) {
        super(delegate, lock);
    }

    @Override
    protected List<E> getDelegate() {
        return (List<E>) super.getDelegate();
    }

    @Override
    public void add(final int index, final E element) {
        synchronized (getLock()) {
            getDelegate().add(index, element);
        }
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        synchronized (getLock()) {
            return getDelegate().addAll(index, c);
        }
    }

    @Override
    public E get(final int index) {
        synchronized (getLock()) {
            return getDelegate().get(index);
        }
    }

    @Override
    public int indexOf(final Object o) {
        synchronized (getLock()) {
            return getDelegate().indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(final Object o) {
        synchronized (getLock()) {
            return getDelegate().lastIndexOf(o);
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        synchronized (getLock()) {
            final ListIterator<E> iterator = getDelegate().listIterator();
            return new SynchronizedListIterator<E>(iterator, getLock());
        }
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        synchronized (getLock()) {
            final ListIterator<E> iterator = getDelegate().listIterator(index);
            return new SynchronizedListIterator<E>(iterator, getLock());
        }
    }

    @Override
    public E remove(final int index) {
        synchronized (getLock()) {
            return getDelegate().remove(index);
        }
    }

    @Override
    public E set(final int index, final E element) {
        synchronized (getLock()) {
            return getDelegate().set(index, element);
        }
    }

    @Override
    public void replaceAll(final UnaryOperator<E> operator) {
        synchronized (getLock()) {
            getDelegate().replaceAll(operator);
        }
    }

    @Override
    public void sort(final Comparator<? super E> c) {
        synchronized (getLock()) {
            getDelegate().sort(c);
        }
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        synchronized (getLock()) {
            return new SynchronizedList<E>(getDelegate().subList(fromIndex, toIndex), getLock());
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        synchronized (getLock()) {
            return getDelegate().equals(o);
        }
    }

    @Override
    public int hashCode() {
        synchronized (getLock()) {
            return getDelegate().hashCode();
        }
    }

}
