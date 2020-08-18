package de.invesdwin.util.collections.fast;

import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class FastIterableDelegateMap<K, V> extends AFastIterableDelegateMap<K, V> {

    public FastIterableDelegateMap(final Map<K, V> delegate) {
        super(delegate);
    }

    @Override
    protected Map<K, V> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
