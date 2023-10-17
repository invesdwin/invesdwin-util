package de.invesdwin.util.math.random;

import javax.annotation.concurrent.Immutable;

@Immutable
public class RandomAdapterFromJava extends java.util.Random implements IRandomGenerator {

    private final java.util.random.RandomGenerator randomGenerator;
    private final java.util.Random random;
    private final IRandomGenerator iDelegate;

    public RandomAdapterFromJava(final java.util.Random delegate) {
        this.randomGenerator = delegate;
        this.random = delegate;
        if (delegate instanceof IRandomGenerator) {
            this.iDelegate = (IRandomGenerator) delegate;
        } else {
            this.iDelegate = null;
        }
    }

    public RandomAdapterFromJava(final java.util.random.RandomGenerator delegate) {
        this.randomGenerator = delegate;
        if (delegate instanceof java.util.Random) {
            this.random = (java.util.Random) delegate;
        } else {
            this.random = null;
        }
        if (delegate instanceof IRandomGenerator) {
            this.iDelegate = (IRandomGenerator) delegate;
        } else {
            this.iDelegate = null;
        }
    }

    @Override
    public String toString() {
        return randomGenerator.toString();
    }

    @Override
    public void setSeed(final int seed) {
        setSeed((long) seed);
    }

    @Override
    public void setSeed(final int[] seed) {
        // the following number is the largest prime that fits in 32 bits (it is 2^32 - 5)
        final long prime = 4294967291L;

        long combined = 0L;
        for (final int s : seed) {
            combined = combined * prime + s;
        }
        setSeed(combined);
    }

    @Override
    public void setSeed(final long seed) {
        if (random != null) {
            random.setSeed(seed);
        } else if (iDelegate != null) {
            iDelegate.setSeed(seed);
        }
    }

    @Override
    public void reseed() {
        if (iDelegate != null) {
            iDelegate.reseed();
        }
    }

    @Override
    public void nextBytes(final byte[] bytes) {
        randomGenerator.nextBytes(bytes);
    }

    @Override
    public int nextInt() {
        return randomGenerator.nextInt();
    }

    @Override
    public int nextInt(final int n) {
        return randomGenerator.nextInt(n);
    }

    @Override
    public long nextLong() {
        return randomGenerator.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return randomGenerator.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return randomGenerator.nextFloat();
    }

    @Override
    public double nextDouble() {
        return randomGenerator.nextDouble();
    }

    @Override
    public double nextGaussian() {
        return randomGenerator.nextGaussian();
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
