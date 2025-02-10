package de.invesdwin.util.math.random.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGeneratorObjectPool;

@NotThreadSafe
public class PooledDelegateRandomGenerator implements IRandomGenerator {

    private final IObjectPool<IRandomGenerator> pool;

    public PooledDelegateRandomGenerator() {
        this(PseudoRandomGeneratorObjectPool.INSTANCE);
    }

    PooledDelegateRandomGenerator(final IObjectPool<IRandomGenerator> pool) {
        this.pool = pool;
    }

    @Override
    public void setSeed(final int seed) {
        final IRandomGenerator delegate = pool.borrowObject();
        try {
            delegate.setSeed(seed);
        } finally {
            pool.returnObject(delegate);
        }
    }

    @Override
    public void setSeed(final int[] seed) {
        final IRandomGenerator delegate = pool.borrowObject();
        try {
            delegate.setSeed(seed);
        } finally {
            pool.returnObject(delegate);
        }
    }

    @Override
    public void setSeed(final long seed) {
        final IRandomGenerator delegate = pool.borrowObject();
        try {
            delegate.setSeed(seed);
        } finally {
            pool.returnObject(delegate);
        }
    }

    @Override
    public void nextBytes(final byte[] bytes) {
        final IRandomGenerator delegate = pool.borrowObject();
        try {
            delegate.nextBytes(bytes);
        } finally {
            pool.returnObject(delegate);
        }
    }

    @Override
    public int nextInt() {
        final IRandomGenerator delegate = pool.borrowObject();
        try {
            return delegate.nextInt();
        } finally {
            pool.returnObject(delegate);
        }
    }

    @Override
    public int nextInt(final int n) {
        final IRandomGenerator delegate = pool.borrowObject();
        try {
            return delegate.nextInt(n);
        } finally {
            pool.returnObject(delegate);
        }
    }

    @Override
    public long nextLong() {
        final IRandomGenerator delegate = pool.borrowObject();
        try {
            return delegate.nextLong();
        } finally {
            pool.returnObject(delegate);
        }
    }

    @Override
    public boolean nextBoolean() {
        final IRandomGenerator delegate = pool.borrowObject();
        try {
            return delegate.nextBoolean();
        } finally {
            pool.returnObject(delegate);
        }
    }

    @Override
    public float nextFloat() {
        final IRandomGenerator delegate = pool.borrowObject();
        try {
            return delegate.nextFloat();
        } finally {
            pool.returnObject(delegate);
        }
    }

    @Override
    public double nextDouble() {
        final IRandomGenerator delegate = pool.borrowObject();
        try {
            return delegate.nextDouble();
        } finally {
            pool.returnObject(delegate);
        }
    }

    @Override
    public double nextGaussian() {
        final IRandomGenerator delegate = pool.borrowObject();
        try {
            return delegate.nextGaussian();
        } finally {
            pool.returnObject(delegate);
        }
    }

}
