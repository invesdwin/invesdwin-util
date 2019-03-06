package com.google.common.collect;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class GuavaCompactLinkedHashSet<E> extends CompactLinkedHashSet<E> {

    public GuavaCompactLinkedHashSet() {
        super();
    }

    public GuavaCompactLinkedHashSet(final int expectedSize) {
        super(expectedSize);
    }

    public GuavaCompactLinkedHashSet(final int expectedSize, final float loadFactor) {
        super(expectedSize);
        init(expectedSize, loadFactor);
    }

}
