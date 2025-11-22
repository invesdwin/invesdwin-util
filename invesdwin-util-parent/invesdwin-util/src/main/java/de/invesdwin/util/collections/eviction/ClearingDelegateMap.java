package de.invesdwin.util.collections.eviction;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ClearingDelegateMap<K, V> extends AClearingDelegateMap<K, V> {

    public ClearingDelegateMap(final boolean threadSafe, final int maximumSize, final Map<K, V> delegate) {
        super(threadSafe, maximumSize, delegate);
    }

    @Deprecated
    @Override
    protected Map<K, V> newDelegate() {
        throw new UnsupportedOperationException();
    }

}