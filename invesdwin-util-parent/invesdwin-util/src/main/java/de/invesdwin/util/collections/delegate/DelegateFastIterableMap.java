package de.invesdwin.util.collections.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.fast.IFastIterableMap;

@NotThreadSafe
public class DelegateFastIterableMap<K, V> extends ADelegateFastIterableMap<K, V> implements IFastIterableMap<K, V> {

    public DelegateFastIterableMap(final IFastIterableMap<K, V> delegate) {
        super(delegate);
    }

    @Override
    protected IFastIterableMap<K, V> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
