package de.invesdwin.util.collections.loadingcache.caffeine;

import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.delegate.ADelegateMap;
import de.invesdwin.util.collections.loadingcache.ILoadingCacheMap;
import de.invesdwin.util.error.Throwables;

@ThreadSafe
public abstract class ACaffeineLoadingCacheMap<K, V> extends ADelegateMap<K, V> implements ILoadingCacheMap<K, V> {

    @Override
    protected final ILoadingCacheMap<K, V> newDelegate() {
        return newConfig().newMap(this);
    }

    @Override
    protected ILoadingCacheMap<K, V> getDelegate() {
        return (ILoadingCacheMap<K, V>) super.getDelegate();
    }

    /**
     * May be overwritten to reconfigure the cache.
     * 
     * It is unknown if the calculations should only be kept temporarily. Such an assumption here would cause problems
     * elsewhere, thus we don't do eviction per default.
     * 
     * Null may not be returned by this.
     */
    protected CaffeineLoadingCacheMapConfig newConfig() {
        return new CaffeineLoadingCacheMapConfig();
    }

    protected abstract V loadValue(K key);

    @Override
    public V get(final Object key) {
        try {
            return super.get(key);
        } catch (final Throwable e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public V getIfPresent(final K key) {
        return getDelegate().getIfPresent(key);
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        return getDelegate().computeIfAbsent(key, mappingFunction);
    }

}
