package de.invesdwin.util.collections.concurrent;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.collections4.collection.SynchronizedCollection;

@ThreadSafe
public class SynchronizedSet<T> extends SynchronizedCollection<T> implements Set<T> {

    public SynchronizedSet(final Set<T> set, final Object lock) {
        super(set, lock);
    }
}