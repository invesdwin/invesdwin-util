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

    public GuavaCompactHashSet(final int expectedSize, final float loadFactor) {
        super(expectedSize);
        init(expectedSize, loadFactor);
    }

}
