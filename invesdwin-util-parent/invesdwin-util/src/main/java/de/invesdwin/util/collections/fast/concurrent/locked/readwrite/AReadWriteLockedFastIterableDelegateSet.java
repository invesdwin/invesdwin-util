package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import java.util.Collection;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.iterable.collection.ArrayCloseableIterator;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

/**
 * Boosts the iteration speed over the values by keeping a fast iterator instance that only gets modified when changes
 * to the map occur.
 * 
 * The iterator returned from this set is also suitable for concurrent modification during iteration.
 * 
 * http://stackoverflow.com/questions/1006395/fastest-way-to-iterate-an-array-in-java-loop-variable-vs-enhanced-for-statement
 */
@ThreadSafe
public abstract class AReadWriteLockedFastIterableDelegateSet<E> implements IFastIterableSet<E> {

    //arraylist wins in raw iterator speed compared to bufferingIterator since no remove is needed, though we need protection against concurrent modification
    @GuardedBy("lock")
    private transient BufferingIterator<E> fastIterable;
    @GuardedBy("lock")
    private transient E[] array;
    @GuardedBy("lock")
    private final Set<E> delegate;
    private final IReadWriteLock lock;

    protected AReadWriteLockedFastIterableDelegateSet(final Set<E> delegate) {
        this.delegate = delegate;
        refreshFastIterable();
        this.lock = Locks.newReentrantReadWriteLock(getClass().getSimpleName());
    }

    protected AReadWriteLockedFastIterableDelegateSet(final Set<E> delegate, final IReadWriteLock lock) {
        this.delegate = delegate;
        refreshFastIterable();
        this.lock = lock;
    }

    public AReadWriteLockedFastIterableDelegateSet() {
        this.delegate = newDelegate();
        refreshFastIterable();
        this.lock = Locks.newReentrantReadWriteLock(getClass().getSimpleName());
    }

    public AReadWriteLockedFastIterableDelegateSet(final IReadWriteLock lock) {
        this.delegate = newDelegate();
        refreshFastIterable();
        this.lock = lock;
    }

    protected abstract Set<E> newDelegate();

    @Override
    public boolean add(final E e) {
        lock.writeLock().lock();
        try {
            final boolean added = delegate.add(e);
            if (added) {
                addToFastIterable(e);
            }
            return added;
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected void addToFastIterable(final E e) {
        if (fastIterable != null) {
            fastIterable.add(e);
        }
        array = null;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        lock.writeLock().lock();
        try {
            final boolean added = delegate.addAll(c);
            if (added) {
                refreshFastIterable();
            }
            return added;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean remove(final Object o) {
        lock.writeLock().lock();
        try {
            final boolean removed = delegate.remove(o);
            if (removed) {
                refreshFastIterable();
            }
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        lock.writeLock().lock();
        try {
            final boolean removed = delegate.removeAll(c);
            if (removed) {
                refreshFastIterable();
            }
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * protected so it can be used inside addToFastIterable to refresh instead if desired by overriding
     */
    protected void refreshFastIterable() {
        fastIterable = null;
        array = null;
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            if (delegate.isEmpty()) {
                return;
            }
            delegate.clear();
            if (fastIterable != null) {
                fastIterable = new BufferingIterator<E>();
            }
            array = null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public ICloseableIterator<E> iterator() {
        lock.readLock().lock();
        try {
            final E[] arrayCopy = array;
            if (arrayCopy != null) {
                return new ArrayCloseableIterator<>(arrayCopy);
            }
            final BufferingIterator<E> fastIterableCopy = fastIterable;
            if (fastIterableCopy != null) {
                return fastIterableCopy.iterator();
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            if (array != null) {
                return new ArrayCloseableIterator<>(array);
            }
            if (fastIterable == null) {
                fastIterable = new BufferingIterator<E>(delegate);
            }
            return fastIterable.iterator();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return delegate.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return delegate.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public E[] asArray(final E[] emptyArray) {
        lock.readLock().lock();
        try {
            final E[] arrayCopy = array;
            if (arrayCopy != null) {
                return arrayCopy;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            if (array == null) {
                if (delegate.isEmpty()) {
                    assert emptyArray.length == 0 : "emptyArray.length needs to be 0: " + emptyArray.length;
                    array = emptyArray;
                } else {
                    array = onArrayCreated(delegate.toArray(emptyArray));
                }
            }
            return array;
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected E[] onArrayCreated(final E[] array) {
        return array;
    }

    @Override
    public boolean contains(final Object o) {
        lock.readLock().lock();
        try {
            return delegate.contains(o);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Object[] toArray() {
        lock.readLock().lock();
        try {
            return delegate.toArray();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        lock.readLock().lock();
        try {
            return delegate.toArray(a);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        lock.readLock().lock();
        try {
            return delegate.containsAll(c);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        lock.writeLock().lock();
        try {
            return delegate.retainAll(c);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return delegate.toString();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int hashCode() {
        lock.readLock().lock();
        try {
            return delegate.hashCode();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        lock.readLock().lock();
        try {
            return delegate.equals(obj);
        } finally {
            lock.readLock().unlock();
        }
    }

}
