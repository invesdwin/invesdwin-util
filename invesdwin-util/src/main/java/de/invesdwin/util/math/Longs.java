package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ALongsStaticFacade;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ALongsStaticFacade", targets = {
        com.google.common.primitives.Longs.class })
@Immutable
public final class Longs extends ALongsStaticFacade {

    public static final ADelegateComparator<Long> COMPARATOR = new ADelegateComparator<Long>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Long e) {
            return e;
        }
    };

    private Longs() {}

    public static Long min(final Long... times) {
        Long minTime = null;
        for (final Long time : times) {
            minTime = min(minTime, time);
        }
        return minTime;
    }

    public static Long min(final Long time1, final Long time2) {
        if (time1 == null) {
            return time2;
        } else if (time2 == null) {
            return time1;
        }

        if (time1 < time2) {
            return time1;
        } else {
            return time2;
        }
    }

    public static Long max(final Long... times) {
        Long maxTime = null;
        for (final Long time : times) {
            maxTime = max(maxTime, time);
        }
        return maxTime;
    }

    public static Long max(final Long time1, final Long time2) {
        if (time1 == null) {
            return time2;
        } else if (time2 == null) {
            return time1;
        }

        if (time1 > time2) {
            return time1;
        } else {
            return time2;
        }
    }

    public static Long between(final Long value, final Long min, final Long max) {
        return max(min(value, max), min);
    }

    public static long checkedCast(final double value) {
        if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
            throw new ArithmeticException("long overflow");
        }
        return (long) value;
    }

    public static long[] checkedCastVector(final double[] value) {
        final long[] longVector = new long[value.length];
        for (int i = 0; i < value.length; i++) {
            longVector[i] = checkedCast(value[i]);
        }
        return longVector;
    }

    public static long[][] checkedCastMatrix(final double[][] value) {
        final long[][] longMatrix = new long[value.length][];
        for (int row = 0; row < value.length; row++) {
            final double[] vector = value[row];
            longMatrix[row] = checkedCastVector(vector);
        }
        return longMatrix;
    }

}
