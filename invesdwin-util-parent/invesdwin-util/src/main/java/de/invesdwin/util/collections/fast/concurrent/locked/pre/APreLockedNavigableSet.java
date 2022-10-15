package de.invesdwin.util.collections.fast.concurrent.locked.pre;

import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.string.description.TextDescription;

@ThreadSafe
public abstract class APreLockedNavigableSet<E> extends APreLockedSortedSet<E> implements NavigableSet<E> {

    public APreLockedNavigableSet(final TextDescription iteratorName) {
        super(iteratorName);
    }

    public APreLockedNavigableSet(final TextDescription iteratorName, final ILock lock) {
        super(iteratorName, lock);
    }

    @Override
    protected abstract NavigableSet<E> getPreLockedDelegate();

    @Override
    public E lower(final E e) {
        final NavigableSet<E> delegate = getPreLockedDelegate();
        try {
            return delegate.lower(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E floor(final E e) {
        final NavigableSet<E> delegate = getPreLockedDelegate();
        try {
            return delegate.floor(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E ceiling(final E e) {
        final NavigableSet<E> delegate = getPreLockedDelegate();
        try {
            return delegate.ceiling(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E higher(final E e) {
        final NavigableSet<E> delegate = getPreLockedDelegate();
        try {
            return delegate.higher(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E pollFirst() {
        final NavigableSet<E> delegate = getPreLockedDelegate();
        try {
            return delegate.pollFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E pollLast() {
        final NavigableSet<E> delegate = getPreLockedDelegate();
        try {
            return delegate.pollLast();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return null;
    }

    private APreLockedNavigableSet<E> getThis() {
        return this;
    }

    @Override
    public ICloseableIterator<E> descendingIterator() {
        final Collection<E> delegate = getPreLockedDelegate();
        final Iterator<E> iterator = delegate.iterator();
        return new PreLockedIterator<E>(iteratorName, iterator, lock);
    }

    @Override
    public NavigableSet<E> subSet(final E fromElement, final boolean fromInclusive, final E toElement,
            final boolean toInclusive) {
        return new APreLockedNavigableSet<E>(iteratorName, lock) {
            @Override
            protected NavigableSet<E> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().subSet(fromElement, fromInclusive, toElement, toInclusive);
            }
        };
    }

    @Override
    public NavigableSet<E> headSet(final E toElement, final boolean inclusive) {
        return new APreLockedNavigableSet<E>(iteratorName, lock) {
            @Override
            protected NavigableSet<E> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().headSet(toElement, inclusive);
            }
        };
    }

    @Override
    public NavigableSet<E> tailSet(final E fromElement, final boolean inclusive) {
        return new APreLockedNavigableSet<E>(iteratorName, lock) {
            @Override
            protected NavigableSet<E> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().tailSet(fromElement, inclusive);
            }
        };
    }

}