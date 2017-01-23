package de.invesdwin.util.lang;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

@Immutable
public class RandomGeneratorAdapter implements RandomGenerator {

    private final Random delegate;

    public RandomGeneratorAdapter(final Random delegate) {
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

    public static RandomGenerator currentThreadLocalRandom() {
        return new RandomGeneratorAdapter(ThreadLocalRandom.current());
    }

}
