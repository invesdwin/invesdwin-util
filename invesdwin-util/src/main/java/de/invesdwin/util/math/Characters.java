package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ACharactersStaticFacade;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ACharactersStaticFacade", targets = {
        com.google.common.primitives.Chars.class })
@Immutable
public final class Characters extends ACharactersStaticFacade {

    public static final ADelegateComparator<Character> COMPARATOR = new ADelegateComparator<Character>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Character e) {
            return e;
        }
    };

    private Characters() {}

    public static Character min(final Character... times) {
        Character minTime = null;
        for (final Character time : times) {
            minTime = min(minTime, time);
        }
        return minTime;
    }

    public static Character min(final Character time1, final Character time2) {
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

    public static Character max(final Character... times) {
        Character maxTime = null;
        for (final Character time : times) {
            maxTime = max(maxTime, time);
        }
        return maxTime;
    }

    public static Character max(final Character time1, final Character time2) {
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

    public static Character between(final Character value, final Character min, final Character max) {
        return max(min(value, max), min);
    }

    public static Character checkedCast(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            final String stringValue = (String) value;
            return checkedCast(stringValue);
        } else if (value instanceof Character) {
            return (Character) value;
        } else if (value instanceof Number) {
            final Number numberValue = (Number) value;
            return checkedCast(numberValue);
        } else {
            throw UnknownArgumentException.newInstance(Class.class, value.getClass());
        }
    }

    public static Character checkedCast(final Number value) {
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
            final byte byteValue = value.byteValue();
            return checkedCast(byteValue);
        } else {
            final long longValue = Longs.checkedCast(value.doubleValue());
            return checkedCast(longValue);
        }
    }

    public static char checkedCast(final byte value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow");
        }
        return (char) value;
    }

    public static char checkedCast(final short value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow");
        }
        return (char) value;
    }

    public static char checkedCast(final int value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow");
        }
        return (char) value;
    }

    public static char checkedCast(final long value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow");
        }
        return (char) value;
    }

    public static char checkedCast(final float value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow");
        }
        return (char) value;
    }

    public static char checkedCast(final double value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow");
        }
        return (char) value;
    }

    public static char[] checkedCastVector(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof char[]) {
            return (char[]) value;
        } else if (value instanceof String) {
            final String stringValue = (String) value;
            return checkedCastVector(stringValue);
        } else {
            throw UnknownArgumentException.newInstance(Class.class, value.getClass());
        }
    }

    public static char[] checkedCastVector(final String value) {
        return value.toCharArray();
    }

    public static char[] checkedCastVector(final int[] value) {
        final char[] charVector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            charVector[i] = checkedCast(value[i]);
        }
        return charVector;
    }

    public static char[][] checkedCastMatrix(final int[][] value) {
        final char[][] charMatrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            final int[] vector = value[row];
            charMatrix[row] = checkedCastVector(vector);
        }
        return charMatrix;
    }

    public static char checkedCast(final String value) {
        if (value == null) {
            throw new NullPointerException("String should not be null!");
        }
        if (value.length() != 1) {
            throw new IllegalArgumentException("Expecting exactly one character in string: " + value);
        }
        return value.charAt(0);
    }

    public static char[] checkedCastVector(final String[] value) {
        final char[] charVector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            charVector[i] = checkedCast(value[i]);
        }
        return charVector;
    }

    public static char[][] checkedCastMatrix(final String[][] value) {
        final char[][] charMatrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            final String[] vector = value[row];
            charMatrix[row] = checkedCastVector(vector);
        }
        return charMatrix;
    }

}
