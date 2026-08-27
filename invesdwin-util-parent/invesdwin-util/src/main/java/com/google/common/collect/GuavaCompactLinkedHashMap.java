package com.google.common.collect;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.DelegateMap;

@NotThreadSafe
public class GuavaCompactLinkedHashMap<K, V> extends DelegateMap<K, V> {

    public GuavaCompactLinkedHashMap() {
        super(new CompactLinkedHashMap<K, V>());
    }

    public GuavaCompactLinkedHashMap(final int expectedSize) {
        super(new CompactLinkedHashMap<K, V>(expectedSize));
    }

    public GuavaCompactLinkedHashMap(final int expectedSize, final boolean accessOrder) {
        super(new CompactLinkedHashMap<K, V>(expectedSize, accessOrder));
    }

}
