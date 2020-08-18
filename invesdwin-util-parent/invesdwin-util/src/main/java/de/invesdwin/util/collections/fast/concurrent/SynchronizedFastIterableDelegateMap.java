package de.invesdwin.util.collections.fast.concurrent;

import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedFastIterableDelegateMap<K, V> extends ASynchronizedFastIterableDelegateMap<K, V> {

    public SynchronizedFastIterableDelegateMap(final Map<K, V> delegate) {
        super(delegate);
    }

    @Override
    protected Map<K, V> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
