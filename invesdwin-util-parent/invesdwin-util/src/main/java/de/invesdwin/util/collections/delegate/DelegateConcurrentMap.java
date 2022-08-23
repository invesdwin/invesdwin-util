package de.invesdwin.util.collections.delegate;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DelegateConcurrentMap<K, V> extends ADelegateConcurrentMap<K, V> {

    public DelegateConcurrentMap(final ConcurrentMap<K, V> delegate) {
        super(delegate);
    }

    @Deprecated
    @Override
    protected ConcurrentMap<K, V> newDelegate() {
        throw new UnsupportedOperationException();
    }

}