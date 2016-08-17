package de.invesdwin.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@ThreadSafe
public abstract class AConcurrentModificationDelegateSet<E> extends ADelegateCollection<E>
        implements ICloseableIterable<E>, Set<E> {

    @GuardedBy("this")
    private final AtomicLong openIterators = new AtomicLong(0L);
    @GuardedBy("this")
    private final Map<E, Boolean> tasks_add = new LinkedHashMap<E, Boolean>();

    @Override
    protected abstract Set<E> createDelegate();

    @Override
    public synchronized boolean add(final E e) {
        if (openIterators.get() == 0) {
            return super.add(e);
        } else {
            tasks_add.remove(e);
            return !contains(e) && tasks_add.put(e, true) == null;
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        boolean changed = false;
        for (final E e : c) {
            if (add(e)) {
                changed = true;
            }
        }
        return changed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(final Object o) {
        if (openIterators.get() == 0) {
            return super.remove(o);
        } else {
            tasks_add.remove(o);
            return contains(o) && tasks_add.put((E) o, false) == null;
        }
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        boolean changed = false;
        for (final Object e : c) {
            if (remove(e)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        final Iterator<E> delegate = super.iterator();
        synchronized (this) {
            openIterators.incrementAndGet();
        }
        return new ICloseableIterator<E>() {

            private final boolean closed = false;

            @Override
            public void close() {
                if (closed) {
                    return;
                }
                synchronized (AConcurrentModificationDelegateSet.this) {
                    if (openIterators.decrementAndGet() == 0) {
                        for (final Entry<E, Boolean> e : tasks_add.entrySet()) {
                            if (e.getValue()) {
                                AConcurrentModificationDelegateSet.this.getDelegate().add(e.getKey());
                            } else {
                                AConcurrentModificationDelegateSet.this.getDelegate().remove(e.getKey());
                            }
                        }
                        tasks_add.clear();
                    }
                }
            }

            @Override
            public boolean hasNext() {
                final boolean hasNext = delegate.hasNext();
                if (!hasNext) {
                    close();
                }
                return hasNext;
            }

            @Override
            public E next() {
                try {
                    return delegate.next();
                } catch (final NoSuchElementException e) {
                    close();
                    throw e;
                }
            }

        };
    }

}
