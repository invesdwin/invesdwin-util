package com.google.common.collect;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.DelegateSet;

@NotThreadSafe
public class GuavaCompactLinkedHashSet<E> extends DelegateSet<E> {

    public GuavaCompactLinkedHashSet() {
        super(new CompactLinkedHashSet<E>());
    }

    public GuavaCompactLinkedHashSet(final int expectedSize) {
        super(new CompactLinkedHashSet<E>(expectedSize));
    }

}
