package de.invesdwin.util.collections.fast.concurrent.locked.pre;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.lang.string.description.TextDescription;

@ThreadSafe
public abstract class APreLockedCollection<E> implements Collection<E> {

    protected final TextDescription iteratorName;
    /** The object to lock on, needed for List/SortedSet views */
    protected final ILock lock;

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param delegate
     *            the collection to decorate, must not be null
     * @throws NullPointerException
     *             if the collection is null
     */
    public APreLockedCollection(final TextDescription iteratorName) {
        this.iteratorName = iteratorName;
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
    public APreLockedCollection(final TextDescription iteratorName, final ILock lock) {
        this.iteratorName = iteratorName;
        Assertions.checkNotNull(lock);
        this.lock = lock;
    }

    /**
     * Gets the collection being decorated.
     *
     * @return the decorated collection
     */
    protected abstract Collection<E> getPreLockedDelegate();

    //-----------------------------------------------------------------------

    @Override
    public final boolean add(final E object) {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.add(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean addAll(final Collection<? extends E> coll) {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.addAll(coll);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final void clear() {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            delegate.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean contains(final Object object) {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.contains(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean containsAll(final Collection<?> coll) {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.containsAll(coll);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean isEmpty() {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final ICloseableIterator<E> iterator() {
        final Collection<E> delegate = getPreLockedDelegate();
        final Iterator<E> iterator = delegate.iterator();
        return new PreLockedIterator<E>(iteratorName, iterator, lock);
    }

    @Override
    public final Object[] toArray() {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.toArray();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final <T> T[] toArray(final T[] object) {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.toArray(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean remove(final Object object) {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.remove(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean removeAll(final Collection<?> coll) {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.removeAll(coll);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean retainAll(final Collection<?> coll) {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.retainAll(coll);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final int size() {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.equals(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final int hashCode() {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.hashCode();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final String toString() {
        final Collection<E> delegate = getPreLockedDelegate();
        try {
            return delegate.toString();
        } finally {
            lock.unlock();
        }
    }

}
