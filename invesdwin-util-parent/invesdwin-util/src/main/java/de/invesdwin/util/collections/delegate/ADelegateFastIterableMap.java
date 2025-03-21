package de.invesdwin.util.collections.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.fast.IFastIterableMap;

@NotThreadSafe
public abstract class ADelegateFastIterableMap<K, V> extends ADelegateMap<K, V> implements IFastIterableMap<K, V> {

    public ADelegateFastIterableMap() {
        super();
    }

    protected ADelegateFastIterableMap(final IFastIterableMap<K, V> delegate) {
        super(delegate);
    }

    @Override
    protected abstract IFastIterableMap<K, V> newDelegate();

    @Override
    protected IFastIterableMap<K, V> getDelegate() {
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
