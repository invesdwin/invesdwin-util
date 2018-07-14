package de.invesdwin.util.collections.loadingcache.cache2k.internal;

import java.io.Serializable;

import javax.annotation.concurrent.ThreadSafe;

import org.cache2k.Cache;
import org.cache2k.CacheEntry;
import org.cache2k.event.CacheEntryExpiredListener;
import org.cache2k.event.CacheEntryRemovedListener;

import de.invesdwin.util.collections.loadingcache.guava.IRemovalListener;

@ThreadSafe
public class WrapperRemovalListener<K, V>
        implements CacheEntryExpiredListener<K, V>, CacheEntryRemovedListener<K, V>, Serializable {

    private final IRemovalListener<K, V> delegate;

    public WrapperRemovalListener(final IRemovalListener<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onEntryRemoved(final Cache<K, V> cache, final CacheEntry<K, V> entry) {
        delegate.onRemoval(entry.getKey(), entry.getValue());
    }

    @Override
    public void onEntryExpired(final Cache<K, V> cache, final CacheEntry<K, V> entry) {
        delegate.onRemoval(entry.getKey(), entry.getValue());
    }

}
