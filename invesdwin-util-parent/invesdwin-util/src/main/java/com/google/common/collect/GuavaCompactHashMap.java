package com.google.common.collect;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class GuavaCompactHashMap<K, V> extends CompactHashMap<K, V> {

    public GuavaCompactHashMap() {
        super();
    }

    public GuavaCompactHashMap(final int expectedSize) {
        super(expectedSize);
    }

}
