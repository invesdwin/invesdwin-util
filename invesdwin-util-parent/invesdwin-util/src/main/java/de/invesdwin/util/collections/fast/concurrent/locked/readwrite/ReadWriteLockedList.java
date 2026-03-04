package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedList<E> extends ReadWriteLockedCollection<E> implements List<E> {

    public ReadWriteLockedList(final List<E> delegate) {
        super(delegate);
    }

    public ReadWriteLockedList(final List<E> delegate, final IReadWriteLock lock) {
        super(delegate, lock);
    }

    @Override
    protected List<E> getDelegate() {
        return (List<E>) super.getDelegate();
    }

    @Override
    public void add(final int index, final E element) {
        getLock().writeLock().lock();
        try {
            getDelegate().add(index, element);
        } finally {
            getLock().writeLock().unlock();
        }
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        getLock().writeLock().lock();
        try {
            return getDelegate().addAll(index, c);
        } finally {
            getLock().writeLock().unlock();
        }
    }

    @Override
    public E get(final int index) {
        getLock().readLock().lock();
        try {
            return getDelegate().get(index);
        } finally {
            getLock().readLock().unlock();
        }
    }

    @Override
    public int indexOf(final Object o) {
        getLock().readLock().lock();
        try {
            return getDelegate().indexOf(o);
        } finally {
            getLock().readLock().unlock();
        }
    }

    @Override
    public int lastIndexOf(final Object o) {
        getLock().readLock().lock();
        try {
            return getDelegate().lastIndexOf(o);
        } finally {
            getLock().readLock().unlock();
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        getLock().readLock().lock();
        try {
            final ListIterator<E> iterator = getDelegate().listIterator();
            return new ReadWriteLockedListIterator<E>(iterator, getLock());
        } finally {
            getLock().readLock().unlock();
        }
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        getLock().readLock().lock();
        try {
            final ListIterator<E> iterator = getDelegate().listIterator(index);
            return new ReadWriteLockedListIterator<E>(iterator, getLock());
        } finally {
            getLock().readLock().unlock();
        }
    }

    @Override
    public E remove(final int index) {
        getLock().writeLock().lock();
        try {
            return getDelegate().remove(index);
        } finally {
            getLock().writeLock().unlock();
        }
    }

    @Override
    public E set(final int index, final E element) {
        getLock().writeLock().lock();
        try {
            return getDelegate().set(index, element);
        } finally {
            getLock().writeLock().unlock();
        }
    }

    @Override
    public void replaceAll(final UnaryOperator<E> operator) {
        getLock().writeLock().lock();
        try {
            getDelegate().replaceAll(operator);
        } finally {
            getLock().writeLock().unlock();
        }
    }

    @Override
    public void sort(final Comparator<? super E> c) {
        getLock().writeLock().lock();
        try {
            getDelegate().sort(c);
        } finally {
            getLock().writeLock().unlock();
        }
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        getLock().readLock().lock();
        try {
            return new ReadWriteLockedList<E>(getDelegate().subList(fromIndex, toIndex), getLock());
        } finally {
            getLock().readLock().unlock();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        getLock().readLock().lock();
        try {
            return getDelegate().equals(o);
        } finally {
            getLock().readLock().unlock();
        }
    }

    @Override
    public int hashCode() {
        getLock().readLock().lock();
        try {
            return getDelegate().hashCode();
        } finally {
            getLock().readLock().unlock();
        }
    }

}
