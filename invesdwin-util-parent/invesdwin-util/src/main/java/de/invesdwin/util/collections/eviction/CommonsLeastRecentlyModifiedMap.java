package de.invesdwin.util.collections.eviction;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Reconfigures apache commons LRUMap to remove the least recently modified element instead of the least recently used.
 * When an element is updated, it is being counted as modified. This improves performance a bit.
 */
@NotThreadSafe
public class CommonsLeastRecentlyModifiedMap<K, V> extends CommonsLeastRecentlyUsedMap<K, V> {

    public CommonsLeastRecentlyModifiedMap(final int maximumSize) {
        super(maximumSize);
    }

    @Override
    public EvictionMode getEvictionMode() {
        return EvictionMode.LeastRecentlyModified;
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