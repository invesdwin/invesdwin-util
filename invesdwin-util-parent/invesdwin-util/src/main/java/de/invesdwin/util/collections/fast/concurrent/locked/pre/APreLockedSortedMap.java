package de.invesdwin.util.collections.fast.concurrent.locked.pre;

import java.util.Comparator;
import java.util.SortedMap;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.description.TextDescription;

@ThreadSafe
public abstract class APreLockedSortedMap<K, V> extends APreLockedMap<K, V> implements SortedMap<K, V> {

    public APreLockedSortedMap(final TextDescription iteratorName) {
        super(iteratorName);
    }

    public APreLockedSortedMap(final TextDescription iteratorName, final ILock lock) {
        super(iteratorName, lock);
    }

    @Override
    protected abstract SortedMap<K, V> getPreLockedDelegate();

    @Override
    public final Comparator<? super K> comparator() {
        final SortedMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.comparator();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final K firstKey() {
        final SortedMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.firstKey();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final K lastKey() {
        final SortedMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.lastKey();
        } finally {
            lock.unlock();
        }
    }

    private APreLockedSortedMap<K, V> getThis() {
        return this;
    }

    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        return new APreLockedSortedMap<K, V>(iteratorName, lock) {
            @Override
            protected SortedMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().subMap(fromKey, toKey);
            }
        };
    }

    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        return new APreLockedSortedMap<K, V>(iteratorName, lock) {
            @Override
            protected SortedMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().headMap(toKey);
            }
        };
    }

    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        return new APreLockedSortedMap<K, V>(iteratorName, lock) {
            @Override
            protected SortedMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().tailMap(fromKey);
            }
        };
    }

}
