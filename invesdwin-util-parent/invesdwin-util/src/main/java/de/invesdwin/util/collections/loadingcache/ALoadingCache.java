package de.invesdwin.util.collections.loadingcache;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.eviction.EvictionMode;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.loadingcache.map.CaffeineLoadingCache;
import de.invesdwin.util.collections.loadingcache.map.EvictionMapLoadingCache;
import de.invesdwin.util.collections.loadingcache.map.NoCachingLoadingCache;
import de.invesdwin.util.collections.loadingcache.map.SynchronizedEvictionMapLoadingCache;
import de.invesdwin.util.collections.loadingcache.map.SynchronizedUnlimitedCachingLoadingCache;
import de.invesdwin.util.collections.loadingcache.map.UnlimitedCachingLoadingCache;
import de.invesdwin.util.concurrent.reference.WeakThreadLocalReference;

@ThreadSafe
public abstract class ALoadingCache<K, V> extends ADelegateLoadingCache<K, V> {

    /**
     * default unlimited size
     */
    protected Integer getInitialMaximumSize() {
        return ALoadingCacheConfig.DEFAULT_INITIAL_MAXIMUM_SIZE;
    }

    /**
     * default is false, since this comes at a cost
     */
    protected boolean isHighConcurrency() {
        return ALoadingCacheConfig.DEFAULT_HIGH_CONCURRENCY;
    }

    /**
     * 
     */
    protected boolean isThreadSafe() {
        return ALoadingCacheConfig.DEFAULT_THREAD_SAFE;
    }

    /**
     * default is true, otherwise it will evict the least recently added element
     */
    protected EvictionMode getEvictionMode() {
        return ALoadingCacheConfig.DEFAULT_EVICTION_MODE;
    }

    /**
     * default is false, since this comes at a cost
     */
    protected boolean isPreventRecursiveLoad() {
        return ALoadingCacheConfig.DEFAULT_PREVENT_RECURSIVE_LOAD;
    }

    protected abstract V loadValue(K key);

    @Override
    protected ILoadingCache<K, V> newDelegate() {
        final Integer maximumSize = getInitialMaximumSize();
        final Function<K, V> loadValue = newLoadValueF();
        if (isHighConcurrency()) {
            Assertions.checkTrue(isThreadSafe());
            return newConcurrentLoadingCache(loadValue, maximumSize);
        } else if (maximumSize == null) {
            if (isThreadSafe()) {
                return new SynchronizedUnlimitedCachingLoadingCache<K, V>(loadValue);
            } else {
                return new UnlimitedCachingLoadingCache<K, V>(loadValue);
            }
        } else if (maximumSize == 0) {
            return new NoCachingLoadingCache<K, V>(loadValue);
        } else {
            if (isThreadSafe()) {
                return new SynchronizedEvictionMapLoadingCache<K, V>(loadValue, getEvictionMode().newMap(maximumSize));
            } else {
                return new EvictionMapLoadingCache<>(loadValue, getEvictionMode().newMap(maximumSize));
            }
        }
    }

    protected Function<K, V> newLoadValueF() {
        if (isPreventRecursiveLoad()) {
            if (isHighConcurrency()) {
                return new Function<K, V>() {
                    private final Set<K> alreadyLoading = ILockCollectionFactory.getInstance(true).newConcurrentSet();

                    @Override
                    public V apply(final K t) {
                        return preventRecursiveLoad(alreadyLoading, t);
                    }
                };
            } else if (isThreadSafe()) {
                return new Function<K, V>() {
                    private final WeakThreadLocalReference<Set<K>> alreadyLoadingRef = new WeakThreadLocalReference<Set<K>>() {
                        @Override
                        protected Set<K> initialValue() {
                            return new HashSet<K>();
                        }
                    };

                    @Override
                    public V apply(final K t) {
                        final Set<K> alreadyLoading = alreadyLoadingRef.get();
                        return preventRecursiveLoad(alreadyLoading, t);
                    }
                };
            } else {
                return new Function<K, V>() {
                    private final Set<K> alreadyLoading = new HashSet<K>();

                    @Override
                    public V apply(final K t) {
                        return preventRecursiveLoad(alreadyLoading, t);
                    }
                };
            }
        } else {
            return this::loadValue;
        }
    }

    protected V preventRecursiveLoad(final Set<K> alreadyLoading, final K key) {
        if (alreadyLoading.add(key)) {
            try {
                final V loaded = loadValue(key);
                final ILoadingCache<K, V> delegate = getDelegate();
                if (delegate.containsKey(key)) {
                    final V existing = delegate.get(key);
                    if (loaded != existing) {
                        throw new IllegalStateException("Already loaded key: " + key);
                    }
                }
                return loaded;
            } finally {
                alreadyLoading.remove(key);
            }
        } else {
            throw new IllegalStateException("Already loading recursively key: " + key);
        }
    }

    protected ILoadingCache<K, V> newConcurrentLoadingCache(final Function<K, V> loadValue, final Integer maximumSize) {
        return new CaffeineLoadingCache<K, V>(loadValue, maximumSize);
    }

}
