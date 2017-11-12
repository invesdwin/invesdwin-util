package de.invesdwin.util.math.statistics.runningmedian.internal;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class IndexMaxPQHeapIterator<Key extends Comparable<Key>> implements Iterator<Entry<Integer, Key>> {
    // create a new pq
    private final IndexMaxPQ<Key> copy;

    // add all elements to copy of heap
    // takes linear time since already in heap order so no keys move
    public IndexMaxPQHeapIterator(final IndexMaxPQ<Key> copy) {
        this.copy = copy;
    }

    @Override
    public boolean hasNext() {
        return !copy.isEmpty();
    }

    public int remaining() {
        return copy.size();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, Key> next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException();
        }
        return copy.delMax();
    }
}