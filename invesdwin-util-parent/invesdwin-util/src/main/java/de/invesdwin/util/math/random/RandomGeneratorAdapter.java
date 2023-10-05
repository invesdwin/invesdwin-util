package de.invesdwin.util.math.random;

import javax.annotation.concurrent.Immutable;

@Immutable
public class RandomGeneratorAdapter implements IRandomGenerator {

    private final java.util.random.RandomGenerator randomGenerator;
    private final java.util.Random random;

    public RandomGeneratorAdapter(final java.util.Random delegate) {
        this.randomGenerator = delegate;
        this.random = delegate;
    }

    public RandomGeneratorAdapter(final java.util.random.RandomGenerator delegate) {
        this.randomGenerator = delegate;
        if (delegate instanceof java.util.Random) {
            this.random = (java.util.Random) delegate;
        } else {
            this.random = null;
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

}
