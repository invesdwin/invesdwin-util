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

}
