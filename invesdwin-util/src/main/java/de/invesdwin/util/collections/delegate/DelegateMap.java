package de.invesdwin.util.collections.delegate;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DelegateMap<K, V> extends ADelegateMap<K, V> {

    public DelegateMap(final Map<K, V> delegate) {
        super(delegate);
    }

    @Deprecated
    @Override
    protected Map<K, V> newDelegate() {
        throw new UnsupportedOperationException();
    }

}