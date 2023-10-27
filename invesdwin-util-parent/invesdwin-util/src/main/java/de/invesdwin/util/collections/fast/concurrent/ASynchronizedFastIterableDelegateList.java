package de.invesdwin.util.collections.fast.concurrent;

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

@ThreadSafe
public abstract class ASynchronizedFastIterableDelegateList<E> implements IFastIterableList<E> {

    //arraylist wins in raw iterator speed compared to bufferingIterator since no remove is needed, though we need protection against concurrent modification
    @GuardedBy("this")
    private transient BufferingIterator<E> fastIterable;
    @GuardedBy("this")
    private transient E[] array;
    @GuardedBy("this")
    private final List<E> delegate;

    protected ASynchronizedFastIterableDelegateList(final List<E> delegate) {
        this.delegate = delegate;
        refreshFastIterable();
    }

    public ASynchronizedFastIterableDelegateList() {
        this.delegate = newDelegate();
        refreshFastIterable();
    }

    protected abstract List<E> newDelegate();

    @Override
    public synchronized boolean add(final E e) {
        final boolean added = delegate.add(e);
        if (added) {
            addToFastIterable(e);
        }
        return added;
    }

    protected void addToFastIterable(final E e) {
        if (fastIterable != null) {
            fastIterable.add(e);
        }
        array = null;
    }

    @Override
    public synchronized boolean addAll(final Collection<? extends E> c) {
        final boolean added = delegate.addAll(c);
        if (added) {
            refreshFastIterable();
        }
        return added;
    }

    @Override
    public synchronized boolean addAll(final int index, final Collection<? extends E> c) {
        final boolean added = delegate.addAll(index, c);
        if (added) {
            refreshFastIterable();
        }
        return added;
    }

    @Override
    public synchronized void add(final int index, final E element) {
        delegate.add(index, element);
        refreshFastIterable();
    }

    @Override
    public synchronized boolean remove(final Object o) {
        final boolean removed = delegate.remove(o);
        if (removed) {
            refreshFastIterable();
        }
        return removed;
    }

    @Override
    public synchronized boolean removeAll(final Collection<?> c) {
        final boolean removed = delegate.removeAll(c);
        if (removed) {
            refreshFastIterable();
        }
        return removed;
    }

    @Override
    public synchronized E remove(final int index) {
        final E removed = delegate.remove(index);
        refreshFastIterable();
        return removed;
    }

    /**
     * protected so it can be used inside addToFastIterable to refresh instead if desired by overriding
     */
    protected void refreshFastIterable() {
        fastIterable = null;
        array = null;
    }

    @Override
    public synchronized void clear() {
        if (delegate.isEmpty()) {
            return;
        }
        delegate.clear();
        if (fastIterable != null) {
            fastIterable = new BufferingIterator<E>();
        }
        array = null;
    }

    @Override
    public synchronized ICloseableIterator<E> iterator() {
        if (array != null) {
            return new ArrayCloseableIterator<>(array);
        }
        if (fastIterable == null) {
            fastIterable = new BufferingIterator<E>(delegate);
        }
        return fastIterable.iterator();
    }

    @Override
    public synchronized boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public synchronized int size() {
        return delegate.size();
    }

    @Override
    public synchronized E[] asArray(final E[] emptyArray) {
        if (array == null) {
            if (delegate.isEmpty()) {
                assert emptyArray.length == 0 : "emptyArray.length needs to be 0: " + emptyArray.length;
                array = emptyArray;
            } else {
                array = onArrayCreated(delegate.toArray(emptyArray));
            }
        }
        return array;
    }

    protected E[] onArrayCreated(final E[] array) {
        return array;
    }

    @Override
    public synchronized boolean contains(final Object o) {
        return delegate.contains(o);
    }

    @Override
    public synchronized Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public synchronized <T> T[] toArray(final T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public synchronized boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public synchronized boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public synchronized E get(final int index) {
        return delegate.get(index);
    }

    @Override
    public synchronized E set(final int index, final E element) {
        final E prev = delegate.set(index, element);
        refreshFastIterable();
        return prev;
    }

    @Override
    public synchronized int indexOf(final Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public synchronized int lastIndexOf(final Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        final ListIterator<E> it = delegate.listIterator();
        final RefreshingListIterator listIterator = new RefreshingListIterator(it);
        return new SynchronizedListIterator<>(listIterator, this);
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        final ListIterator<E> it = delegate.listIterator(index);
        final RefreshingListIterator listIterator = new RefreshingListIterator(it);
        return new SynchronizedListIterator<>(listIterator, this);
    }

    @Override
    public synchronized List<E> subList(final int fromIndex, final int toIndex) {
        return new SynchronizedList<E>(Collections.unmodifiableList(delegate).subList(fromIndex, toIndex), this);
    }

    @Override
    public synchronized String toString() {
        return delegate.toString();
    }

    @Override
    public synchronized int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public synchronized boolean equals(final Object obj) {
        return delegate.equals(obj);
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
