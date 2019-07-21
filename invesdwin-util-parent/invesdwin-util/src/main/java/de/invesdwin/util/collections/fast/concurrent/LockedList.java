package de.invesdwin.util.collections.fast.concurrent;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedList<E> extends LockedCollection<E> implements List<E> {

    public LockedList(final List<E> delegate) {
        super(delegate);
    }

    public LockedList(final List<E> delegate, final ILock lock) {
        super(delegate, lock);
    }

    @Override
    protected List<E> getDelegate() {
        return (List<E>) super.getDelegate();
    }

    @Override
    public void add(final int index, final E element) {
        getLock().lock();
        try {
            getDelegate().add(index, element);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        getLock().lock();
        try {
            return getDelegate().addAll(index, c);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public E get(final int index) {
        getLock().lock();
        try {
            return getDelegate().get(index);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public int indexOf(final Object o) {
        getLock().lock();
        try {
            return getDelegate().indexOf(o);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public int lastIndexOf(final Object o) {
        getLock().lock();
        try {
            return getDelegate().lastIndexOf(o);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        getLock().lock();
        try {
            final ListIterator<E> iterator = getDelegate().listIterator();
            return new LockedListIterator<E>(iterator, getLock());
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        getLock().lock();
        try {
            final ListIterator<E> iterator = getDelegate().listIterator(index);
            return new LockedListIterator<E>(iterator, getLock());
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public E remove(final int index) {
        getLock().lock();
        try {
            return getDelegate().remove(index);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public E set(final int index, final E element) {
        getLock().lock();
        try {
            return getDelegate().set(index, element);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public void replaceAll(final UnaryOperator<E> operator) {
        getLock().lock();
        try {
            getDelegate().replaceAll(operator);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public void sort(final Comparator<? super E> c) {
        getLock().lock();
        try {
            getDelegate().sort(c);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        getLock().lock();
        try {
            return new LockedList<E>(getDelegate().subList(fromIndex, toIndex), getLock());
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        getLock().lock();
        try {
            return getDelegate().equals(o);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public int hashCode() {
        getLock().lock();
        try {
            return getDelegate().hashCode();
        } finally {
            getLock().unlock();
        }
    }

}
