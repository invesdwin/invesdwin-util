package com.google.common.collect;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class GuavaCompactHashSet<E> extends CompactHashSet<E> {

    public GuavaCompactHashSet() {
        super();
    }

    public GuavaCompactHashSet(final int expectedSize) {
        super(expectedSize);
    }

}
