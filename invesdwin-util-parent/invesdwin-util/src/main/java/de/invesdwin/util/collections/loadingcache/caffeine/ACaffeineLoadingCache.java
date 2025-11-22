package de.invesdwin.util.collections.loadingcache.caffeine;

import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.collections.loadingcache.map.CaffeineLoadingCache;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public abstract class ACaffeineLoadingCache<K, V> extends ALoadingCache<K, V> {

    @Override
    protected boolean isHighConcurrency() {
        return ACaffeineLoadingCacheConfig.DEFAULT_HIGH_CONCURRENCY;
    }

    @Override
    protected boolean isThreadSafe() {
        return ACaffeineLoadingCacheConfig.DEFAULT_THREAD_SAFE;
    }

    protected Duration getRefreshAfterWrite() {
        return ACaffeineLoadingCacheConfig.DEFAULT_REFRESH_AFTER_WRITE;
    }

    protected Duration getExpireAfterAccess() {
        return ACaffeineLoadingCacheConfig.DEFAULT_EXPIRE_AFTER_ACCESS;
    }

    protected Duration getExpireAfterWrite() {
        return ACaffeineLoadingCacheConfig.DEFAULT_EXPIRE_AFTER_WRITE;
    }

    protected Boolean getWeakKeys() {
        return ACaffeineLoadingCacheConfig.DEFAULT_WEAK_KEYS;
    }

    protected Boolean getWeakValues() {
        return ACaffeineLoadingCacheConfig.DEFAULT_WEAK_VALUES;
    }

    protected Boolean getSoftValues() {
        return ACaffeineLoadingCacheConfig.DEFAULT_SOFT_VALUES;
    }

    @Override
    protected ILoadingCache<K, V> newDelegate() {
        Assertions.checkTrue(isHighConcurrency());
        Assertions.checkTrue(isThreadSafe());
        final Integer maximumSize = getInitialMaximumSize();
        final Function<K, V> loadValue = newLoadValueF();
        return newConcurrentLoadingCache(loadValue, maximumSize);
    }

    @Override
    protected ILoadingCache<K, V> newConcurrentLoadingCache(final Function<K, V> loadValue, final Integer maximumSize) {
        return new CaffeineLoadingCache<K, V>(loadValue, maximumSize) {
            @Override
            protected CaffeineLoadingCacheMapConfig newConfig() {
                return super.newConfig().setExpireAfterAccess(getExpireAfterAccess())
                        .setExpireAfterWrite(getExpireAfterWrite())
                        .setRefreshAfterWrite(getRefreshAfterWrite())
                        .setWeakKeys(getWeakKeys())
                        .setWeakValues(getWeakValues())
                        .setSoftValues(getSoftValues());
            }
        };
    }

}
