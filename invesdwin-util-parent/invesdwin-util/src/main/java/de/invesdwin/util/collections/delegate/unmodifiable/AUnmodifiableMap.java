package de.invesdwin.util.collections.delegate.unmodifiable;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class AUnmodifiableMap<K, V> implements Map<K, V> {

    @Override
    public final V put(final K key, final V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final V remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void putAll(final Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }

}
