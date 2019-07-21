package de.invesdwin.util.collections.fast.concurrent;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedCollection<E> implements Collection<E> {

    /** The object to lock on, needed for List/SortedSet views */
    private final Object lock;

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
    public SynchronizedCollection(final Collection<E> delegate) {
        this.delegate = delegate;
        this.lock = this;
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
    public SynchronizedCollection(final Collection<E> collection, final Object lock) {
        this.delegate = collection;
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

    protected Object getLock() {
        return lock;
    }

    //-----------------------------------------------------------------------

    @Override
    public boolean add(final E object) {
        synchronized (lock) {
            return getDelegate().add(object);
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        synchronized (lock) {
            return getDelegate().addAll(coll);
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            getDelegate().clear();
        }
    }

    @Override
    public boolean contains(final Object object) {
        synchronized (lock) {
            return getDelegate().contains(object);
        }
    }

    @Override
    public boolean containsAll(final Collection<?> coll) {
        synchronized (lock) {
            return getDelegate().containsAll(coll);
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return getDelegate().isEmpty();
        }
    }

    @Override
    public Iterator<E> iterator() {
        synchronized (lock) {
            final Iterator<E> iterator = getDelegate().iterator();
            return new SynchronizedIterator<E>(iterator, lock);
        }
    }

    @Override
    public Object[] toArray() {
        synchronized (lock) {
            return getDelegate().toArray();
        }
    }

    @Override
    public <T> T[] toArray(final T[] object) {
        synchronized (lock) {
            return getDelegate().toArray(object);
        }
    }

    @Override
    public boolean remove(final Object object) {
        synchronized (lock) {
            return getDelegate().remove(object);
        }
    }

    @Override
    public boolean removeAll(final Collection<?> coll) {
        synchronized (lock) {
            return getDelegate().removeAll(coll);
        }
    }

    @Override
    public boolean retainAll(final Collection<?> coll) {
        synchronized (lock) {
            return getDelegate().retainAll(coll);
        }
    }

    @Override
    public int size() {
        synchronized (lock) {
            return getDelegate().size();
        }
    }

    @Override
    public boolean equals(final Object object) {
        synchronized (lock) {
            if (object == this) {
                return true;
            }
            return object == this || getDelegate().equals(object);
        }
    }

    @Override
    public int hashCode() {
        synchronized (lock) {
            return getDelegate().hashCode();
        }
    }

    @Override
    public String toString() {
        synchronized (lock) {
            return getDelegate().toString();
        }
    }

}
