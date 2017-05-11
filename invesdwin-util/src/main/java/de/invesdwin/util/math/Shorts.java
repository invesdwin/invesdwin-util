package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.AShortsStaticFacade;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.AShortsStaticFacade", targets = {
        com.google.common.primitives.Shorts.class })
@Immutable
public final class Shorts extends AShortsStaticFacade {

    public static final ADelegateComparator<Short> COMPARATOR = new ADelegateComparator<Short>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Short e) {
            return e;
        }
    };

    private Shorts() {}

    public static Short min(final Short... times) {
        Short minTime = null;
        for (final Short time : times) {
            minTime = min(minTime, time);
        }
        return minTime;
    }

    public static Short min(final Short time1, final Short time2) {
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

    public static Short max(final Short... times) {
        Short maxTime = null;
        for (final Short time : times) {
            maxTime = max(maxTime, time);
        }
        return maxTime;
    }

    public static Short max(final Short time1, final Short time2) {
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

    public static Short between(final Short value, final Short min, final Short max) {
        return max(min(value, max), min);
    }

    public static Short checkedCast(final Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Long) {
            final long longValue = value.longValue();
            return checkedCast(longValue);
        } else if (value instanceof Double) {
            final double doubleValue = value.doubleValue();
            return checkedCast(doubleValue);
        } else if (value instanceof Float) {
            final float floatValue = value.floatValue();
            return checkedCast(floatValue);
        } else if (value instanceof Integer) {
            final int integerValue = value.intValue();
            return checkedCast(integerValue);
        } else {
            return value.shortValue();
        }
    }

    public static short checkedCast(final int value) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new ArithmeticException("short overflow");
        }
        return (short) value;
    }

    public static short checkedCast(final long value) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new ArithmeticException("short overflow");
        }
        return (short) value;
    }

    public static short checkedCast(final float value) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new ArithmeticException("short overflow");
        }
        return (short) value;
    }

    public static short checkedCast(final double value) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new ArithmeticException("short overflow");
        }
        return (short) value;
    }

    public static short[] checkedCastVector(final int[] value) {
        final short[] shortVector = new short[value.length];
        for (int i = 0; i < value.length; i++) {
            shortVector[i] = checkedCast(value[i]);
        }
        return shortVector;
    }

    public static short[][] checkedCastMatrix(final int[][] value) {
        final short[][] shortMatrix = new short[value.length][];
        for (int row = 0; row < value.length; row++) {
            final int[] vector = value[row];
            shortMatrix[row] = checkedCastVector(vector);
        }
        return shortMatrix;
    }

}
