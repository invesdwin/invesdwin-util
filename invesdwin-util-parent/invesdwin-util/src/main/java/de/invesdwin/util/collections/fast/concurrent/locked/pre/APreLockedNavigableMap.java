package de.invesdwin.util.collections.fast.concurrent.locked.pre;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.string.description.TextDescription;

@ThreadSafe
public abstract class APreLockedNavigableMap<K, V> extends APreLockedSortedMap<K, V> implements NavigableMap<K, V> {

    private NavigableMap<K, V> descendingMap;
    private NavigableSet<K> descendingKeySet;

    public APreLockedNavigableMap(final TextDescription iteratorName) {
        super(iteratorName);
    }

    public APreLockedNavigableMap(final TextDescription iteratorName, final ILock lock) {
        super(iteratorName, lock);
    }

    protected NavigableSet<K> newDescendingKeySet() {
        return new APreLockedNavigableSet<K>(iteratorName, lock) {
            @Override
            protected NavigableSet<K> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().descendingKeySet();
            }
        };
    }

    protected NavigableMap<K, V> newDescendingMap() {
        return new APreLockedNavigableMap<K, V>(iteratorName, lock) {
            @Override
            protected NavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().descendingMap();
            }
        };
    }

    @Override
    protected abstract NavigableMap<K, V> getPreLockedDelegate();

    @Override
    public final Entry<K, V> lowerEntry(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.lowerEntry(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final K lowerKey(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.lowerKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final Entry<K, V> floorEntry(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.floorEntry(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final K floorKey(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.floorKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final Entry<K, V> ceilingEntry(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.ceilingEntry(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final K ceilingKey(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.ceilingKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final Entry<K, V> higherEntry(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.higherEntry(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final K higherKey(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.higherKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final Entry<K, V> firstEntry() {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.firstEntry();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final Entry<K, V> lastEntry() {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.lastEntry();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final Entry<K, V> pollFirstEntry() {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.pollFirstEntry();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final Entry<K, V> pollLastEntry() {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.pollLastEntry();
        } finally {
            lock.unlock();
        }
    }

    private APreLockedNavigableMap<K, V> getThis() {
        return this;
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        if (descendingMap == null) {
            descendingMap = newDescendingMap();
        }
        return descendingMap;
    }

    @Override
    protected Set<K> newKeySet() {
        return new APreLockedNavigableSet<K>(iteratorName, lock) {
            @Override
            protected NavigableSet<K> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().navigableKeySet();
            }
        };
    }

    @Override
    public final NavigableSet<K> keySet() {
        return (NavigableSet<K>) super.keySet();
    }

    @Override
    public final NavigableSet<K> navigableKeySet() {
        return keySet();
    }

    @Override
    public final NavigableSet<K> descendingKeySet() {
        if (descendingKeySet == null) {
            descendingKeySet = newDescendingKeySet();
        }
        return descendingKeySet;
    }

    @Override
    public NavigableMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey,
            final boolean toInclusive) {
        return new APreLockedNavigableMap<K, V>(iteratorName, lock) {
            @Override
            protected NavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().subMap(fromKey, fromInclusive, toKey, toInclusive);
            }
        };
    }

    @Override
    public NavigableMap<K, V> headMap(final K toKey, final boolean inclusive) {
        return new APreLockedNavigableMap<K, V>(iteratorName, lock) {
            @Override
            protected NavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().headMap(toKey, inclusive);
            }
        };
    }

    @Override
    public NavigableMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
        return new APreLockedNavigableMap<K, V>(iteratorName, lock) {
            @Override
            protected NavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().tailMap(fromKey, inclusive);
            }
        };
    }

}
