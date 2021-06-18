package de.invesdwin.util.collections.loadingcache;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DelegateLoadingCache<K, V> extends ADelegateLoadingCache<K, V> {

    public DelegateLoadingCache(final ILoadingCache<K, V> delegate) {
        super(delegate);
    }

    @Deprecated
    @Override
    protected ILoadingCache<K, V> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
