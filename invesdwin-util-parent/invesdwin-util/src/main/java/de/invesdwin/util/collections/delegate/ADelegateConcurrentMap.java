package de.invesdwin.util.collections.delegate;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ADelegateConcurrentMap<K, V> extends ADelegateMap<K, V> {

    public ADelegateConcurrentMap() {
        super();
    }

    ADelegateConcurrentMap(final ConcurrentMap<K, V> delegate) {
        super(delegate);
    }

    @Override
    protected ConcurrentMap<K, V> getDelegate() {
        return (ConcurrentMap<K, V>) super.getDelegate();
    }

    @Override
    protected abstract ConcurrentMap<K, V> newDelegate();

}
