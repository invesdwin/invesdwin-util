package com.google.common.collect;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class GuavaCompactLinkedHashMap<K, V> extends CompactLinkedHashMap<K, V> {

    public GuavaCompactLinkedHashMap() {
        super();
    }

    public GuavaCompactLinkedHashMap(final int capacity) {
        super(capacity);
    }

    public GuavaCompactLinkedHashMap(final int expectedSize, final float loadFactor, final boolean accessOrder) {
        super(expectedSize, loadFactor, accessOrder);
    }

}
