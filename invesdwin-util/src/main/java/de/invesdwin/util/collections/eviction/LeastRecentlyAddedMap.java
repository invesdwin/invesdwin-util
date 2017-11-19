package de.invesdwin.util.collections.eviction;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Reconfigures apache commons LRUMap to remove the least recently added element instead of the least recently used.
 * Only the first time an element is added is counted. This improves performance a bit.
 */
@NotThreadSafe
public class LeastRecentlyAddedMap<K, V> extends LeastRecentlyModifiedMap<K, V> {

    public LeastRecentlyAddedMap(final int maximumSize) {
        super(maximumSize);
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
    public EvictionMode getEvictionMode() {
        return EvictionMode.LeastRecentlyAdded;
    }
}