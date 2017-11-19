package de.invesdwin.util.collections.leastrecent;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Reconfigures apache commons LRUMap to remove the least recently added element instead of the least recently used.
 * Only the first time an element is added is counted. This improves performance a bit.
 */
@NotThreadSafe
public class LRAMap<K, V> extends LRUMap<K, V> {

    public LRAMap(final int maxSize) {
        super(maxSize);
    }

    @Override
    protected void moveToMRU(final LinkEntry<K, V> entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void updateEntry(final HashEntry<K, V> entry, final V newValue) {
        entry.setValue(newValue);
    }

    @Override
    public V get(final Object key) {
        final LinkEntry<K, V> entry = getEntry(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    @Deprecated
    @Override
    public V get(final Object key, final boolean updateToMRU) {
        return get(key);
    }
}