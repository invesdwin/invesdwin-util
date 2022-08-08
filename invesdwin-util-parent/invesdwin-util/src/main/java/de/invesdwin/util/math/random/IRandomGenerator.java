package de.invesdwin.util.math.random;

import java.util.Collection;

/**
 * adapted from jdk.internal.util.random.RandomSupport
 * 
 * Workaround so that compiled classes in Java 17 are downwards compatible to Java 8.
 */
public interface IRandomGenerator extends org.apache.commons.math3.random.RandomGenerator {

    default float nextFloat(final float maxExclusive) {
        return RandomGeneratorBaseMethods.nextFloat(this, maxExclusive);
    }

    default float nextFloat(final float minInclusive, final float maxExclusive) {
        return RandomGeneratorBaseMethods.nextFloat(this, minInclusive, maxExclusive);
    }

    default double nextDouble(final double maxExclusive) {
        return RandomGeneratorBaseMethods.nextDouble(this, maxExclusive);
    }

    default double nextDouble(final double minInclusive, final double maxExclusive) {
        return RandomGeneratorBaseMethods.nextDouble(this, minInclusive, maxExclusive);
    }

    default int nextInt(final int minInclusive, final int maxExclusive) {
        return RandomGeneratorBaseMethods.nextInt(this, minInclusive, maxExclusive);
    }

    default long nextLong(final long maxExclusive) {
        return RandomGeneratorBaseMethods.nextLong(this, maxExclusive);
    }

    default long nextLong(final long minInclusive, final long maxExclusive) {
        return RandomGeneratorBaseMethods.nextLong(this, minInclusive, maxExclusive);
    }

    default double nextGaussian(final double mean, final double stddev) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextGaussian(mean, stddev);
    }

    default double nextExponential() {
        return nextExponential(1D);
    }

    default String nextHexString(final int len) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextHexString(len);
    }

    default long nextPoisson(final double mean) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextPoisson(mean);
    }

    default double nextExponential(final double mean) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextExponential(mean);
    }

    default double nextGamma(final double shape, final double scale) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextGamma(shape, scale);
    }

    default int nextHypergeometric(final int populationSize, final int numberOfSuccesses, final int sampleSize) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextHypergeometric(populationSize,
                numberOfSuccesses, sampleSize);
    }

    default int nextPascal(final int r, final double p) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextPascal(r, p);
    }

    default double nextT(final double df) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextT(df);
    }

    default double nextWeibull(final double shape, final double scale) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextWeibull(shape, scale);
    }

    default int nextZipf(final int numberOfElements, final double exponent) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextZipf(numberOfElements, exponent);
    }

    default double nextBeta(final double alpha, final double beta) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextBeta(alpha, beta);
    }

    default int nextBinomial(final int numberOfTrials, final double probabilityOfSuccess) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextBinomial(numberOfTrials,
                probabilityOfSuccess);
    }

    default double nextCauchy(final double median, final double scale) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextCauchy(median, scale);
    }

    default double nextChiSquare(final double df) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextChiSquare(df);
    }

    default double nextF(final double numeratorDf, final double denominatorDf) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextF(numeratorDf, denominatorDf);
    }

    default double nextUniform(final double minInclusive, final double maxExclusive) {
        return nextDouble(minInclusive, maxExclusive);
    }

    default int[] nextPermutation(final int n, final int k) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextPermutation(n, k);
    }

    default Object[] nextSample(final Collection<?> c, final int k) {
        return new org.apache.commons.math3.random.RandomDataGenerator(this).nextSample(c, k);
    }

    default void reseed() {
        setSeed(System.currentTimeMillis() + System.identityHashCode(this));
    }

}
