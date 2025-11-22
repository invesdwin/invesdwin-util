package de.invesdwin.util.math.random.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.random.IRandomGenerator;

@NotThreadSafe
public abstract class ADelegateRandomGenerator implements IRandomGenerator {

    private final IRandomGenerator delegate;

    public ADelegateRandomGenerator() {
        this.delegate = newDelegate();
    }

    ADelegateRandomGenerator(final IRandomGenerator delegate) {
        this.delegate = delegate;
    }

    protected abstract IRandomGenerator newDelegate();

    protected IRandomGenerator getDelegate() {
        return delegate;
    }

    @Override
    public void setSeed(final int seed) {
        getDelegate().setSeed(seed);
    }

    @Override
    public void setSeed(final int[] seed) {
        getDelegate().setSeed(seed);
    }

    @Override
    public void setSeed(final long seed) {
        getDelegate().setSeed(seed);
    }

    @Override
    public void nextBytes(final byte[] bytes) {
        getDelegate().nextBytes(bytes);
    }

    @Override
    public int nextInt() {
        return getDelegate().nextInt();
    }

    @Override
    public int nextInt(final int n) {
        return getDelegate().nextInt(n);
    }

    @Override
    public long nextLong() {
        return getDelegate().nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return getDelegate().nextBoolean();
    }

    @Override
    public float nextFloat() {
        return getDelegate().nextFloat();
    }

    @Override
    public double nextDouble() {
        return getDelegate().nextDouble();
    }

    @Override
    public double nextGaussian() {
        return getDelegate().nextGaussian();
    }

}
