package de.invesdwin.util.math.random;

import java.security.SecureRandomParameters;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

@Immutable
public class SecureRandomAdapter extends java.security.SecureRandom implements RandomGenerator {

    private final RandomGenerator delegate;

    public SecureRandomAdapter(final RandomGenerator delegate) {
        this.delegate = delegate;
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
    public void setSeed(final byte[] seed) {
        // the following number is the largest prime that fits in 32 bits (it is 2^32 - 5)
        final long prime = 4294967291L;

        long combined = 0L;
        for (int i = 0; i < seed.length; i++) {
            combined = combined * prime + seed[i];
        }
        setSeed(combined);
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
    public void nextBytes(final byte[] bytes) {
        delegate.nextBytes(bytes);
    }

    @Deprecated
    @Override
    public void nextBytes(final byte[] bytes, final SecureRandomParameters params) {
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

    @Deprecated
    @Override
    public void reseed() {
        //noop
    }

    @Deprecated
    @Override
    public void reseed(final SecureRandomParameters params) {
        //noop
    }

}
