package de.invesdwin.util.collections.fast.concurrent.locked.pre;

import java.util.Comparator;
import java.util.SortedSet;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.string.description.TextDescription;

@ThreadSafe
public abstract class APreLockedSortedSet<E> extends APreLockedSet<E> implements SortedSet<E> {

    public APreLockedSortedSet(final TextDescription iteratorName) {
        super(iteratorName);
    }

    public APreLockedSortedSet(final TextDescription iteratorName, final ILock lock) {
        super(iteratorName, lock);
    }

    @Override
    protected abstract SortedSet<E> getPreLockedDelegate();

    @Override
    public final Comparator<? super E> comparator() {
        final SortedSet<E> delegate = getPreLockedDelegate();
        try {
            return delegate.comparator();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final E first() {
        final SortedSet<E> delegate = getPreLockedDelegate();
        try {
            return delegate.first();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final E last() {
        final SortedSet<E> delegate = getPreLockedDelegate();
        try {
            return delegate.last();
        } finally {
            lock.unlock();
        }
    }

    private APreLockedSortedSet<E> getThis() {
        return this;
    }

    @Override
    public final SortedSet<E> subSet(final E fromElement, final E toElement) {
        return new APreLockedSortedSet<E>(iteratorName, lock) {
            @Override
            protected SortedSet<E> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().subSet(fromElement, toElement);
            }
        };
    }

    @Override
    public final SortedSet<E> headSet(final E toElement) {
        return new APreLockedSortedSet<E>(iteratorName, lock) {
            @Override
            protected SortedSet<E> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().headSet(toElement);
            }
        };
    }

    @Override
    public final SortedSet<E> tailSet(final E fromElement) {
        return new APreLockedSortedSet<E>(iteratorName, lock) {
            @Override
            protected SortedSet<E> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().tailSet(fromElement);
            }
        };
    }

}