package de.invesdwin.util.math.random;

import javax.annotation.concurrent.Immutable;

/**
 * Taken from: jdk.internal.util.random.RandomSupport
 */
@Immutable
public final class RandomGeneratorBaseMethods {

    private RandomGeneratorBaseMethods() {}

    public static float nextFloat(final IRandomGenerator thisObj, final float maxExclusive) {
        // Specialize boundedNextFloat for origin == 0, bound > 0
        float r = thisObj.nextFloat();
        r = r * maxExclusive;
        if (r >= maxExclusive) { // may need to correct a rounding problem
            r = Float.intBitsToFloat(Float.floatToIntBits(maxExclusive) - 1);
        }
        return r;
    }

    public static float nextFloat(final IRandomGenerator thisObj, final float minInclusive, final float maxExclusive) {
        float r = thisObj.nextFloat();
        if (minInclusive < maxExclusive) {
            r = r * (maxExclusive - minInclusive) + minInclusive;
            if (r >= maxExclusive) { // may need to correct a rounding problem
                r = Float.intBitsToFloat(Float.floatToIntBits(maxExclusive) - 1);
            }
        } else {
            throw new IllegalArgumentException(
                    "minInclusive [" + minInclusive + "] should be smaller than maxExclusive [" + maxExclusive + "]");
        }
        return r;
    }

    public static double nextDouble(final IRandomGenerator thisObj, final double maxExclusive) {
        // Specialize boundedNextDouble for origin == 0, bound > 0
        double r = thisObj.nextDouble();
        r = r * maxExclusive;
        if (r >= maxExclusive) { // may need to correct a rounding problem
            r = Double.longBitsToDouble(Double.doubleToLongBits(maxExclusive) - 1);
        }
        return r;
    }

    public static double nextDouble(final IRandomGenerator thisObj, final double minInclusive,
            final double maxExclusive) {
        double r = thisObj.nextDouble();
        if (minInclusive < maxExclusive) {
            r = r * (maxExclusive - minInclusive) + minInclusive;
            if (r >= maxExclusive) { // may need to correct a rounding problem
                r = Double.longBitsToDouble(Double.doubleToLongBits(maxExclusive) - 1);
            }
        } else {
            throw new IllegalArgumentException(
                    "minInclusive [" + minInclusive + "] should be smaller than maxExclusive [" + maxExclusive + "]");
        }
        return r;
    }

    public static int nextInt(final IRandomGenerator thisObj, final int minInclusive, final int maxExclusive) {
        int r = thisObj.nextInt();
        if (minInclusive < maxExclusive) {
            // It's not case (1).
            final int n = maxExclusive - minInclusive;
            final int m = n - 1;
            if ((n & m) == 0) {
                // It is case (2): length of range is a power of 2.
                r = (r & m) + minInclusive;
            } else if (n > 0) {
                // It is case (3): need to reject over-represented candidates.
                //CHECKSTYLE:OFF
                for (int u = r >>> 1; u + m - (r = u % n) < 0; u = thisObj.nextInt() >>> 1) {
                    ;
                }
                //CHECKSTYLE:ON
                r += minInclusive;
            } else {
                // It is case (4): length of range not representable as long.
                while (r < minInclusive || r >= maxExclusive) {
                    r = thisObj.nextInt();
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "minInclusive [" + minInclusive + "] should be smaller than maxExclusive [" + maxExclusive + "]");
        }
        return r;
    }

    public static long nextLong(final IRandomGenerator thisObj, final long maxExclusive) {
        // Specialize boundedNextLong for origin == 0, bound > 0
        final long m = maxExclusive - 1;
        long r = thisObj.nextLong();
        if ((maxExclusive & m) == 0L) {
            // The bound is a power of 2.
            r &= m;
        } else {
            // Must reject over-represented candidates
            /*
             * This loop takes an unlovable form (but it works): because the first candidate is already available, we
             * need a break-in-the-middle construction, which is concisely but cryptically performed within the
             * while-condition of a body-less for loop.
             */
            //CHECKSTYLE:OFF
            for (long u = r >>> 1; u + m - (r = u % maxExclusive) < 0L; u = thisObj.nextLong() >>> 1) {
                ;
            }
            //CHECKSTYLE:ON
        }
        return r;
    }

    public static long nextLong(final IRandomGenerator thisObj, final long minInclusive, final long maxExclusive) {
        long r = thisObj.nextLong();
        if (minInclusive < maxExclusive) {
            // It's not case (1).
            final long n = maxExclusive - minInclusive;
            final long m = n - 1;
            if ((n & m) == 0L) {
                // It is case (2): length of range is a power of 2.
                r = (r & m) + minInclusive;
            } else if (n > 0L) {
                // It is case (3): need to reject over-represented candidates.
                /*
                 * This loop takes an unlovable form (but it works): because the first candidate is already available,
                 * we need a break-in-the-middle construction, which is concisely but cryptically performed within the
                 * while-condition of a body-less for loop.
                 */
                //CHECKSTYLE:OFF
                for (long u = r >>> 1; // ensure nonnegative
                        u + m - (r = u % n) < 0L; // rejection check
                        u = thisObj.nextLong() >>> 1) {
                    ;
                }
                //CHECKSTYLE:ON
                r += minInclusive;
            } else {
                // It is case (4): length of range not representable as long.
                while (r < minInclusive || r >= maxExclusive) {
                    r = thisObj.nextLong();
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "minInclusive [" + minInclusive + "] should be smaller than maxExclusive [" + maxExclusive + "]");
        }
        return r;
    }

}
