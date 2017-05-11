package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ABytesStaticFacade;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ABytesStaticFacade", targets = {
        com.google.common.primitives.Bytes.class })
@Immutable
public final class Bytes extends ABytesStaticFacade {

    public static final ADelegateComparator<Byte> COMPARATOR = new ADelegateComparator<Byte>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Byte e) {
            return e;
        }
    };

    private Bytes() {}

    public static Byte min(final Byte... times) {
        Byte minTime = null;
        for (final Byte time : times) {
            minTime = min(minTime, time);
        }
        return minTime;
    }

    public static Byte min(final Byte time1, final Byte time2) {
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

    public static Byte max(final Byte... times) {
        Byte maxTime = null;
        for (final Byte time : times) {
            maxTime = max(maxTime, time);
        }
        return maxTime;
    }

    public static Byte max(final Byte time1, final Byte time2) {
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

    public static Byte between(final Byte value, final Byte min, final Byte max) {
        return max(min(value, max), min);
    }

    public static Byte checkedCast(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Character) {
            final char character = (Character) value;
            return checkedCast(character);
        } else if (value instanceof Number) {
            final Number numberValue = (Number) value;
            return checkedCast(numberValue);
        } else {
            throw UnknownArgumentException.newInstance(Object.class, value);
        }
    }

    public static Byte checkedCast(final Number value) {
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
        } else if (value instanceof Short) {
            final short shortValue = value.shortValue();
            return checkedCast(shortValue);
        } else if (value instanceof Byte) {
            return value.byteValue();
        } else {
            //fallback to double
            final double doubleValue = value.doubleValue();
            return checkedCast(doubleValue);
        }
    }

    public static byte checkedCast(final short value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow");
        }
        return (byte) value;
    }

    public static byte checkedCast(final char value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow");
        }
        return (byte) value;
    }

    public static byte checkedCast(final int value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow");
        }
        return (byte) value;
    }

    public static byte checkedCast(final long value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow");
        }
        return (byte) value;
    }

    public static byte checkedCast(final float value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow");
        }
        return (byte) value;
    }

    public static byte checkedCast(final double value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow");
        }
        return (byte) value;
    }

    public static byte[] checkedCastVector(final int[] value) {
        final byte[] byteVector = new byte[value.length];
        for (int i = 0; i < value.length; i++) {
            byteVector[i] = checkedCast(value[i]);
        }
        return byteVector;
    }

    public static byte[][] checkedCastMatrix(final int[][] value) {
        final byte[][] byteMatrix = new byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            final int[] vector = value[row];
            byteMatrix[row] = checkedCastVector(vector);
        }
        return byteMatrix;
    }

}
