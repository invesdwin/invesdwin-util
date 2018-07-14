package de.invesdwin.util.collections.loadingcache;

import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.eviction.EvictionMode;
import de.invesdwin.util.collections.loadingcache.map.CaffeineLoadingCache;
import de.invesdwin.util.collections.loadingcache.map.EvictionMapLoadingCache;
import de.invesdwin.util.collections.loadingcache.map.NoCachingLoadingCache;
import de.invesdwin.util.collections.loadingcache.map.UnlimitedCachingLoadingCache;

@ThreadSafe
public abstract class ALoadingCache<K, V> extends ADelegateLoadingCache<K, V> {

    /**
     * default unlimited size
     */
    protected Integer getInitialMaximumSize() {
        return null;
    }

    /**
     * default is false, since this comes at a cost
     */
    protected boolean isHighConcurrency() {
        return false;
    }

    /**
     * default is true, otherwise it will evict the least recently added element
     */
    protected EvictionMode getEvictionMode() {
        return EvictionMode.LeastRecentlyUsed;
    }

    protected abstract V loadValue(K key);

    @Override
    protected ILoadingCache<K, V> createDelegate() {
        final Integer maximumSize = getInitialMaximumSize();
        final Function<K, V> loadValue = new Function<K, V>() {
            @Override
            public V apply(final K key) {
                return loadValue(key);
            }
        };
        if (isHighConcurrency()) {
            return new CaffeineLoadingCache<K, V>(loadValue, maximumSize);
        } else if (maximumSize == null) {
            return new UnlimitedCachingLoadingCache<K, V>(loadValue);
        } else if (maximumSize == 0) {
            return new NoCachingLoadingCache<K, V>(loadValue);
        } else {
            return new EvictionMapLoadingCache<K, V>(loadValue, getEvictionMode().newMap(maximumSize));
        }
    }

}
