package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedCollection<E> implements Collection<E> {

    /** The object to lock on, needed for List/SortedSet views */
    private final IReadWriteLock lock;

    /** The collection to decorate */
    private final Collection<E> delegate;

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param delegate
     *            the collection to decorate, must not be null
     * @throws NullPointerException
     *             if the collection is null
     */
    public ReadWriteLockedCollection(final Collection<E> delegate) {
        this.delegate = delegate;
        this.lock = Locks.newReentrantReadWriteLock(getClass().getSimpleName());
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param collection
     *            the collection to decorate, must not be null
     * @param lock
     *            the lock object to use, must not be null
     * @throws NullPointerException
     *             if the collection or lock is null
     */
    public ReadWriteLockedCollection(final Collection<E> collection, final IReadWriteLock lock) {
        this.delegate = collection;
        Assertions.checkNotNull(lock);
        this.lock = lock;
    }

    /**
     * Gets the collection being decorated.
     *
     * @return the decorated collection
     */
    protected Collection<E> getDelegate() {
        return delegate;
    }

    protected IReadWriteLock getLock() {
        return lock;
    }

    //-----------------------------------------------------------------------

    @Override
    public boolean add(final E object) {
        lock.writeLock().lock();
        try {
            return getDelegate().add(object);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        lock.writeLock().lock();
        try {
            return getDelegate().addAll(coll);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            getDelegate().clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean contains(final Object object) {
        lock.readLock().lock();
        try {
            return getDelegate().contains(object);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsAll(final Collection<?> coll) {
        lock.readLock().lock();
        try {
            return getDelegate().containsAll(coll);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return getDelegate().isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        lock.readLock().lock();
        try {
            final Iterator<E> iterator = getDelegate().iterator();
            return new ReadWriteLockedIterator<E>(iterator, lock);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Object[] toArray() {
        lock.readLock().lock();
        try {
            return getDelegate().toArray();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T> T[] toArray(final T[] object) {
        lock.readLock().lock();
        try {
            return getDelegate().toArray(object);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean remove(final Object object) {
        lock.writeLock().lock();
        try {
            return getDelegate().remove(object);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeAll(final Collection<?> coll) {
        lock.writeLock().lock();
        try {
            return getDelegate().removeAll(coll);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean retainAll(final Collection<?> coll) {
        lock.writeLock().lock();
        try {
            return getDelegate().retainAll(coll);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return getDelegate().size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean equals(final Object object) {
        lock.readLock().lock();
        try {
            if (object == this) {
                return true;
            }
            return object == this || getDelegate().equals(object);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int hashCode() {
        lock.readLock().lock();
        try {
            return getDelegate().hashCode();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return getDelegate().toString();
        } finally {
            lock.readLock().unlock();
        }
    }

}
