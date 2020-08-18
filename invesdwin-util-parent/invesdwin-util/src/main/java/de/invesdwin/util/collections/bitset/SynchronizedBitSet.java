package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedBitSet implements IBitSet {

    private final IBitSet delegate;
    private final Object lock;

    public SynchronizedBitSet(final IBitSet delegate) {
        this.delegate = delegate;
        this.lock = this;
    }

    public SynchronizedBitSet(final IBitSet delegate, final Object lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public void add(final int index) {
        synchronized (lock) {
            delegate.add(index);
        }
    }

    @Override
    public void remove(final int index) {
        synchronized (lock) {
            delegate.remove(index);
        }
    }

    @Override
    public boolean contains(final int index) {
        synchronized (lock) {
            return delegate.contains(index);
        }
    }

    @Override
    public void optimize() {
        synchronized (lock) {
            delegate.optimize();
        }
    }

}
