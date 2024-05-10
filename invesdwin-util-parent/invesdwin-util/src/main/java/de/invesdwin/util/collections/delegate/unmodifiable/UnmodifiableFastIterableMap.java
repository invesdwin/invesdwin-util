package de.invesdwin.util.collections.delegate.unmodifiable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.fast.IFastIterableMap;

@NotThreadSafe
public class UnmodifiableFastIterableMap<K, V> extends UnmodifiableMap<K, V> implements IFastIterableMap<K, V> {

    public UnmodifiableFastIterableMap(final IFastIterableMap<K, V> delegate) {
        super(delegate);
    }

    @Override
    public IFastIterableMap<K, V> getDelegate() {
        return (IFastIterableMap<K, V>) super.getDelegate();
    }

    @Override
    public V[] asValueArray(final V[] emptyArray) {
        return getDelegate().asValueArray(emptyArray);
    }

    @Override
    public K[] asKeyArray(final K[] emptyArray) {
        return getDelegate().asKeyArray(emptyArray);
    }

    @Override
    public Entry<K, V>[] asEntryArray() {
        return getDelegate().asEntryArray();
    }

}
