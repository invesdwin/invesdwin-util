package de.invesdwin.util.collections.loadingcache.guava.internal;

import java.io.Serializable;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Optional;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import de.invesdwin.util.collections.loadingcache.guava.IRemovalListener;

@ThreadSafe
public class OptionalValueWrapperRemovalListener<K, V> implements RemovalListener<K, Optional<V>>, Serializable {

    private final IRemovalListener<K, V> delegate;

    public OptionalValueWrapperRemovalListener(final IRemovalListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onRemoval(final RemovalNotification<K, Optional<V>> notification) {
        delegate.onRemoval(notification.getKey(), notification.getValue().get(), notification.getCause());
    }

}
