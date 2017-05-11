package de.invesdwin.util.math;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.AIntegersStaticFacade;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.AIntegersStaticFacade", targets = {
        com.google.common.primitives.Ints.class })
@Immutable
public final class Integers extends AIntegersStaticFacade {

    public static final ADelegateComparator<Integer> COMPARATOR = new ADelegateComparator<Integer>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Integer e) {
            return e;
        }
    };

    private Integers() {}

    public static Integer max(final Integer first, final Integer second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.max(first, second);
        }
    }

    public static Integer min(final Integer first, final Integer second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.min(first, second);
        }
    }

    public static Integer avg(final Integer first, final Integer second) {
        final long sum = (long) first + (long) second;
        return (int) sum / 2;
    }

    public static Integer avg(final Integer... values) {
        long sum = 0;
        for (final Integer value : values) {
            sum += value;
        }
        return (int) (sum / values.length);
    }

    public static Integer avg(final Collection<Integer> values) {
        long sum = 0;
        for (final Integer value : values) {
            sum += value;
        }
        return (int) (sum / values.size());
    }

    public static Integer sum(final Collection<Integer> values) {
        int sum = 0;
        for (final Integer value : values) {
            sum += value;
        }
        return sum;
    }

    public static Integer between(final Integer value, final Integer min, final Integer max) {
        return max(min(value, max), min);
    }

    public static Integer checkedCast(final Number value) {
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
        } else {
            return value.intValue();
        }
    }

    public static int checkedCast(final long value) {
        return Math.toIntExact(value);
    }

    public static int checkedCast(final float value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("integer overflow");
        }
        return (int) value;
    }

    public static int checkedCast(final double value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("integer overflow");
        }
        return (int) value;
    }

    public static int[] checkedCastVector(final short[] value) {
        final int[] intVector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            intVector[i] = value[i];
        }
        return intVector;
    }

    public static int[][] checkedCastMatrix(final short[][] value) {
        final int[][] intMatrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            final short[] vector = value[row];
            intMatrix[row] = checkedCastVector(vector);
        }
        return intMatrix;
    }

    public static int[] checkedCastVector(final byte[] value) {
        final int[] intVector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            intVector[i] = value[i];
        }
        return intVector;
    }

    public static int[][] checkedCastMatrix(final byte[][] value) {
        final int[][] intMatrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            final byte[] vector = value[row];
            intMatrix[row] = checkedCastVector(vector);
        }
        return intMatrix;
    }
}
