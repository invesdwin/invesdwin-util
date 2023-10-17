package de.invesdwin.util.math.random;

import javax.annotation.concurrent.Immutable;

@Immutable
public class RandomAdapter extends java.util.Random implements IRandomGenerator {

    private final org.apache.commons.math3.random.RandomGenerator delegate;
    private IRandomGenerator iDelegate;

    public RandomAdapter(final org.apache.commons.math3.random.RandomGenerator delegate) {
        this.delegate = delegate;
        if (delegate instanceof IRandomGenerator) {
            this.iDelegate = (IRandomGenerator) delegate;
        } else {
            this.iDelegate = null;
        }
    }

    public RandomAdapter(final IRandomGenerator delegate) {
        this.delegate = delegate;
        this.iDelegate = delegate;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public void setSeed(final int seed) {
        delegate.setSeed(seed);
    }

    @Override
    public void setSeed(final int[] seed) {
        delegate.setSeed(seed);
    }

    @Override
    public void setSeed(final long seed) {
        if (delegate == null) {
            //ignore super constructor
            return;
        }
        delegate.setSeed(seed);
    }

    @Override
    public void reseed() {
        if (iDelegate != null) {
            iDelegate.reseed();
        }
    }

    @Override
    public void nextBytes(final byte[] bytes) {
        delegate.nextBytes(bytes);
    }

    @Override
    public int nextInt() {
        return delegate.nextInt();
    }

    @Override
    public int nextInt(final int n) {
        return delegate.nextInt(n);
    }

    @Override
    public long nextLong() {
        return delegate.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return delegate.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return delegate.nextFloat();
    }

    @Override
    public double nextDouble() {
        return delegate.nextDouble();
    }

    @Override
    public double nextGaussian() {
        return delegate.nextGaussian();
    }

    @Override
    public float nextFloat(final float minInclusive, final float maxExclusive) {
        return IRandomGenerator.super.nextFloat(minInclusive, maxExclusive);
    }

    @Override
    public double nextDouble(final double maxExclusive) {
        return IRandomGenerator.super.nextDouble(maxExclusive);
    }

    @Override
    public int nextInt(final int minInclusive, final int maxExclusive) {
        return IRandomGenerator.super.nextInt(minInclusive, maxExclusive);
    }

    @Override
    public float nextFloat(final float maxExclusive) {
        return IRandomGenerator.super.nextFloat(maxExclusive);
    }

    @Override
    public long nextLong(final long maxExclusive) {
        return IRandomGenerator.super.nextLong(maxExclusive);
    }

    @Override
    public double nextDouble(final double minInclusive, final double maxExclusive) {
        return IRandomGenerator.super.nextDouble(minInclusive, maxExclusive);
    }

    @Override
    public long nextLong(final long minInclusive, final long maxExclusive) {
        return IRandomGenerator.super.nextLong(minInclusive, maxExclusive);
    }

    @Override
    public double nextGaussian(final double mean, final double stddev) {
        return IRandomGenerator.super.nextGaussian(mean, stddev);
    }

    @Override
    public double nextExponential() {
        return IRandomGenerator.super.nextExponential();
    }

}
