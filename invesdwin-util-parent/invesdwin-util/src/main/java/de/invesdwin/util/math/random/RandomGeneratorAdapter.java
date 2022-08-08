package de.invesdwin.util.math.random;

import javax.annotation.concurrent.Immutable;

@Immutable
public class RandomGeneratorAdapter implements IRandomGenerator {

    private final java.util.Random delegate;

    public RandomGeneratorAdapter(final java.util.Random delegate) {
        this.delegate = delegate;
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
        delegate.setSeed(seed);
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

}
