package de.invesdwin.util.collections.loadingcache.caffeine.internal;

import java.io.Serializable;

import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.RemovalListener;

import de.invesdwin.util.collections.loadingcache.guava.IRemovalListener;
import de.invesdwin.util.collections.loadingcache.guava.RemovalCause;

@ThreadSafe
public class WrapperRemovalListener<K, V> implements RemovalListener<K, V>, Serializable {

    private final IRemovalListener<K, V> delegate;

    public WrapperRemovalListener(final IRemovalListener<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onRemoval(final K key, final V value, final com.github.benmanes.caffeine.cache.RemovalCause cause) {
        delegate.onRemoval(key, value, RemovalCause.valueOf(cause));
    }

}
