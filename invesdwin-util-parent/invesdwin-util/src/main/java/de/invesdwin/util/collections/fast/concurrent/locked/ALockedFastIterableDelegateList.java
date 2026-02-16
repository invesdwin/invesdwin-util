package de.invesdwin.util.collections.fast.concurrent.locked;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.iterable.collection.ArrayCloseableIterator;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;

@ThreadSafe
public abstract class ALockedFastIterableDelegateList<E> implements IFastIterableList<E> {

    //arraylist wins in raw iterator speed compared to bufferingIterator since no remove is needed, though we need protection against concurrent modification
    @GuardedBy("lock")
    private transient BufferingIterator<E> fastIterable;
    @GuardedBy("lock")
    private transient E[] array;
    @GuardedBy("lock")
    private final List<E> delegate;
    private final ILock lock;

    protected ALockedFastIterableDelegateList(final List<E> delegate) {
        this.delegate = delegate;
        refreshFastIterable();
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    protected ALockedFastIterableDelegateList(final List<E> delegate, final ILock lock) {
        this.delegate = delegate;
        refreshFastIterable();
        this.lock = lock;
    }

    public ALockedFastIterableDelegateList() {
        this.delegate = newDelegate();
        refreshFastIterable();
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    public ALockedFastIterableDelegateList(final ILock lock) {
        this.delegate = newDelegate();
        refreshFastIterable();
        this.lock = lock;
    }

    protected abstract List<E> newDelegate();

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
    public boolean addAll(final int index, final Collection<? extends E> c) {
        lock.lock();
        try {
            final boolean added = delegate.addAll(index, c);
            if (added) {
                refreshFastIterable();
            }
            return added;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(final int index, final E element) {
        lock.lock();
        try {
            delegate.add(index, element);
            refreshFastIterable();
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

    @Override
    public E remove(final int index) {
        lock.lock();
        try {
            final E removed = delegate.remove(index);
            refreshFastIterable();
            return removed;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int removeRange(final int fromIndexInclusive, final int toIndexExclusive) {
        lock.lock();
        try {
            final int removed = Lists.removeRange(delegate, fromIndexInclusive, toIndexExclusive);
            if (removed > 0) {
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
    public E get(final int index) {
        lock.lock();
        try {
            return delegate.get(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E set(final int index, final E element) {
        lock.lock();
        try {
            final E prev = delegate.set(index, element);
            refreshFastIterable();
            return prev;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int indexOf(final Object o) {
        lock.lock();
        try {
            return delegate.indexOf(o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int lastIndexOf(final Object o) {
        lock.lock();
        try {
            return delegate.lastIndexOf(o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        lock.lock();
        try {
            final ListIterator<E> it = delegate.listIterator();
            final RefreshingListIterator listIterator = new RefreshingListIterator(it);
            return new LockedListIterator<>(listIterator, lock);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        lock.lock();
        try {
            final ListIterator<E> it = delegate.listIterator(index);
            final RefreshingListIterator listIterator = new RefreshingListIterator(it);
            return new LockedListIterator<>(listIterator, lock);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        lock.lock();
        try {
            return new LockedList<E>(Collections.unmodifiableList(delegate).subList(fromIndex, toIndex), lock);
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

    private final class RefreshingListIterator implements ListIterator<E> {

        private final ListIterator<E> it;

        private RefreshingListIterator(final ListIterator<E> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public E next() {
            return it.next();
        }

        @Override
        public boolean hasPrevious() {
            return it.hasPrevious();
        }

        @Override
        public E previous() {
            return it.previous();
        }

        @Override
        public int nextIndex() {
            return it.nextIndex();
        }

        @Override
        public int previousIndex() {
            return it.previousIndex();
        }

        @Override
        public void remove() {
            it.remove();
            refreshFastIterable();
        }

        @Override
        public void set(final E e) {
            it.set(e);
            refreshFastIterable();
        }

        @Override
        public void add(final E e) {
            it.add(e);
            refreshFastIterable();
        }

    }

}
