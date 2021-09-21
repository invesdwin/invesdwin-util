package de.invesdwin.util.collections.fast.concurrent.locked;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;

@ThreadSafe
public class LockedCollection<E> implements Collection<E> {

    /** The object to lock on, needed for List/SortedSet views */
    private final ILock lock;

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
    public LockedCollection(final Collection<E> delegate) {
        this.delegate = delegate;
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
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
    public LockedCollection(final Collection<E> collection, final ILock lock) {
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

    protected ILock getLock() {
        return lock;
    }

    //-----------------------------------------------------------------------

    @Override
    public boolean add(final E object) {
        lock.lock();
        try {
            return getDelegate().add(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        lock.lock();
        try {
            return getDelegate().addAll(coll);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        lock.lock();
        try {
            getDelegate().clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(final Object object) {
        lock.lock();
        try {
            return getDelegate().contains(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsAll(final Collection<?> coll) {
        lock.lock();
        try {
            return getDelegate().containsAll(coll);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            return getDelegate().isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        lock.lock();
        try {
            final Iterator<E> iterator = getDelegate().iterator();
            return new LockedIterator<E>(iterator, lock);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object[] toArray() {
        lock.lock();
        try {
            return getDelegate().toArray();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T[] toArray(final T[] object) {
        lock.lock();
        try {
            return getDelegate().toArray(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(final Object object) {
        lock.lock();
        try {
            return getDelegate().remove(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeAll(final Collection<?> coll) {
        lock.lock();
        try {
            return getDelegate().removeAll(coll);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean retainAll(final Collection<?> coll) {
        lock.lock();
        try {
            return getDelegate().retainAll(coll);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return getDelegate().size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(final Object object) {
        lock.lock();
        try {
            if (object == this) {
                return true;
            }
            return object == this || getDelegate().equals(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int hashCode() {
        lock.lock();
        try {
            return getDelegate().hashCode();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return getDelegate().toString();
        } finally {
            lock.unlock();
        }
    }

}
