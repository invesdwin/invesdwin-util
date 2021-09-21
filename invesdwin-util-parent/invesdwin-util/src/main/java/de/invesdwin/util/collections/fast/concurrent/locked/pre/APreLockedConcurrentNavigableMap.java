package de.invesdwin.util.collections.fast.concurrent.locked.pre;

import java.util.concurrent.ConcurrentNavigableMap;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.description.TextDescription;

@ThreadSafe
public abstract class APreLockedConcurrentNavigableMap<K, V> extends APreLockedNavigableMap<K, V>
        implements ConcurrentNavigableMap<K, V> {

    public APreLockedConcurrentNavigableMap(final TextDescription iteratorName) {
        super(iteratorName);
    }

    public APreLockedConcurrentNavigableMap(final TextDescription iteratorName, final ILock lock) {
        super(iteratorName, lock);
    }

    @Override
    protected abstract ConcurrentNavigableMap<K, V> getPreLockedDelegate();

    private APreLockedConcurrentNavigableMap<K, V> getThis() {
        return this;
    }

    @Override
    public final ConcurrentNavigableMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey,
            final boolean toInclusive) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, lock) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().subMap(fromKey, fromInclusive, toKey, toInclusive);
            }
        };
    }

    @Override
    public final ConcurrentNavigableMap<K, V> headMap(final K toKey, final boolean inclusive) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, lock) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().headMap(toKey, inclusive);
            }
        };
    }

    @Override
    public final ConcurrentNavigableMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, lock) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().tailMap(fromKey, inclusive);
            }
        };
    }

    @Override
    public final ConcurrentNavigableMap<K, V> subMap(final K fromKey, final K toKey) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, lock) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().subMap(fromKey, toKey);
            }
        };
    }

    @Override
    public final ConcurrentNavigableMap<K, V> headMap(final K toKey) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, lock) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().headMap(toKey);
            }
        };
    }

    @Override
    public final ConcurrentNavigableMap<K, V> tailMap(final K fromKey) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, lock) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().tailMap(fromKey);
            }
        };
    }

    @Override
    protected ConcurrentNavigableMap<K, V> newDescendingMap() {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, lock) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().descendingMap();
            }
        };
    }

    @Override
    public final ConcurrentNavigableMap<K, V> descendingMap() {
        return (ConcurrentNavigableMap<K, V>) super.descendingMap();
    }

}
