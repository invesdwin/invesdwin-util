package de.invesdwin.util.collections.loadingcache;

import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.internal.GuavaLoadingCache;
import de.invesdwin.util.collections.loadingcache.internal.LRAMapLoadingCache;
import de.invesdwin.util.collections.loadingcache.internal.NoCachingLoadingCache;
import de.invesdwin.util.collections.loadingcache.internal.UnlimitedCachingLoadingCache;

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

    public void increaseMaximumSize(final int maximumSize) {
        final ILoadingCache<K, V> delegate = getDelegate();
        if (delegate instanceof LRAMapLoadingCache) {
            final LRAMapLoadingCache<K, V> lru = (LRAMapLoadingCache<K, V>) delegate;
            lru.increaseMaximumSize(maximumSize);
        }
        //else ignore
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
            return new GuavaLoadingCache<K, V>(loadValue, maximumSize);
        } else if (maximumSize == null) {
            return new UnlimitedCachingLoadingCache<K, V>(loadValue);
        } else if (maximumSize == 0) {
            return new NoCachingLoadingCache<K, V>(loadValue);
        } else {
            return new LRAMapLoadingCache<K, V>(loadValue, maximumSize);
        }
    }

}
