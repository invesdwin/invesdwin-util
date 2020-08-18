package de.invesdwin.util.collections.bitset;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;

@ThreadSafe
public class LockedBitSet implements IBitSet {

    private final IBitSet delegate;
    private final ILock lock;

    public LockedBitSet(final IBitSet delegate) {
        this.delegate = delegate;
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    public LockedBitSet(final IBitSet delegate, final ILock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public void add(final int index) {
        lock.lock();
        try {
            delegate.add(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(final int index) {
        lock.lock();
        try {
            delegate.remove(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(final int index) {
        lock.lock();
        try {
            return delegate.contains(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void optimize() {
        lock.lock();
        try {
            delegate.optimize();
        } finally {
            lock.unlock();
        }
    }

}
