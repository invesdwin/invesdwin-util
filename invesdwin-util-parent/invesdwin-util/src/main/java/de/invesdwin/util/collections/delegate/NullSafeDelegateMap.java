package de.invesdwin.util.collections.delegate;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

@Immutable
public class NullSafeDelegateMap<K, V> extends DelegateMap<K, V> {

    public NullSafeDelegateMap(final Map<K, V> delegate) {
        super(delegate);
    }

    @Override
    public boolean isPutAllowed(final K key, final V value) {
        return key != null;
    }

}
