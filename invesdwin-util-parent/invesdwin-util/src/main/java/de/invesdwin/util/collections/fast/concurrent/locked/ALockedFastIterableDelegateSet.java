package de.invesdwin.util.collections.fast.concurrent.locked;

import java.util.Collection;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.iterable.collection.ArrayCloseableIterator;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;

/**
 * Boosts the iteration speed over the values by keeping a fast iterator instance that only gets modified when changes
 * to the map occur.
 * 
 * The iterator returned from this set is also suitable for concurrent modification during iteration.
 * 
 * http://stackoverflow.com/questions/1006395/fastest-way-to-iterate-an-array-in-java-loop-variable-vs-enhanced-for-statement
 */
@ThreadSafe
public abstract class ALockedFastIterableDelegateSet<E> implements IFastIterableSet<E> {

    //arraylist wins in raw iterator speed compared to bufferingIterator since no remove is needed, though we need protection against concurrent modification
    @GuardedBy("lock")
    private transient BufferingIterator<E> fastIterable;
    @GuardedBy("lock")
    private transient E[] array;
    @GuardedBy("lock")
    private final Set<E> delegate;
    private final ILock lock;

    protected ALockedFastIterableDelegateSet(final Set<E> delegate) {
        this.delegate = delegate;
        refreshFastIterable();
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    protected ALockedFastIterableDelegateSet(final Set<E> delegate, final ILock lock) {
        this.delegate = delegate;
        refreshFastIterable();
        this.lock = lock;
    }

    public ALockedFastIterableDelegateSet() {
        this.delegate = newDelegate();
        refreshFastIterable();
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    public ALockedFastIterableDelegateSet(final ILock lock) {
        this.delegate = newDelegate();
        refreshFastIterable();
        this.lock = lock;
    }

    protected abstract Set<E> newDelegate();

    @Override
    public boolean add(final E e) {
        lock.lock();
        try {
            final boolean added = delegate.add(e);
            if (added) {
                addToFastIterable(e);
            }
            return added;
        } finally {
            lock.unlock();
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
        lock.lock();
        try {
            final boolean added = delegate.addAll(c);
            if (added) {
                refreshFastIterable();
            }
            return added;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(final Object o) {
        lock.lock();
        try {
            final boolean removed = delegate.remove(o);
            if (removed) {
                refreshFastIterable();
            }
            return removed;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        lock.lock();
        try {
            final boolean removed = delegate.removeAll(c);
            if (removed) {
                refreshFastIterable();
            }
            return removed;
        } finally {
            lock.unlock();
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
        lock.lock();
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
            lock.unlock();
        }
    }

    @Override
    public ICloseableIterator<E> iterator() {
        lock.lock();
        try {
            if (array != null) {
                return new ArrayCloseableIterator<>(array);
            }
            if (fastIterable == null) {
                fastIterable = new BufferingIterator<E>(delegate);
            }
            return fastIterable.iterator();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            return delegate.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return delegate.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E[] asArray(final E[] emptyArray) {
        lock.lock();
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
            lock.unlock();
        }
    }

    protected E[] onArrayCreated(final E[] array) {
        return array;
    }

    @Override
    public boolean contains(final Object o) {
        lock.lock();
        try {
            return delegate.contains(o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object[] toArray() {
        lock.lock();
        try {
            return delegate.toArray();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        lock.lock();
        try {
            return delegate.toArray(a);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        lock.lock();
        try {
            return delegate.containsAll(c);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        lock.lock();
        try {
            return delegate.retainAll(c);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return delegate.toString();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int hashCode() {
        lock.lock();
        try {
            return delegate.hashCode();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        lock.lock();
        try {
            return delegate.equals(obj);
        } finally {
            lock.unlock();
        }
    }

}
