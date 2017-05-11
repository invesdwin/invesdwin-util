package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.AFloatsStaticFacade;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.AFloatsStaticFacade", targets = {
        com.google.common.primitives.Floats.class })
@Immutable
public final class Floats extends AFloatsStaticFacade {

    public static final ADelegateComparator<Float> COMPARATOR = new ADelegateComparator<Float>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Float e) {
            return e;
        }
    };

    private Floats() {}

    public static Float max(final Float first, final Float second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.max(first, second);
        }
    }

    public static Float min(final Float first, final Float second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.min(first, second);
        }
    }

    public static Float between(final Float value, final Float min, final Float max) {
        return max(min(value, max), min);
    }

    public static Float checkedCast(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            final Number numberValue = (Number) value;
            return checkedCast(numberValue);
        } else {
            throw new IllegalArgumentException("Not a " + Number.class.getSimpleName() + ": " + value);
        }
    }

    public static Float checkedCast(final Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Double) {
            final double doubleValue = value.doubleValue();
            return checkedCast(doubleValue);
        } else if (value instanceof Long) {
            final long longValue = value.longValue();
            return checkedCast(longValue);
        } else {
            return value.floatValue();
        }
    }

    public static float checkedCast(final long value) {
        final float floatValue = value;
        return floatValue;
    }

    public static float checkedCast(final double value) {
        if (value < Float.MIN_VALUE || value > Float.MAX_VALUE) {
            throw new ArithmeticException("float overflow");
        }
        final float floatValue = (float) value;
        return floatValue;
    }

    public static float[] checkedCastVector(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof float[]) {
            return (float[]) value;
        } else if (value instanceof double[]) {
            final double[] doubleVector = (double[]) value;
            return checkedCastVector(doubleVector);
        } else {
            //we need to extend this as needed
            throw UnknownArgumentException.newInstance(Object.class, value);
        }
    }

    public static float[] checkedCastVector(final double[] value) {
        final float[] floatVector = new float[value.length];
        for (int i = 0; i < value.length; i++) {
            floatVector[i] = Floats.checkedCast(value[i]);
        }
        return floatVector;
    }

    public static float[][] checkedCastMatrix(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof float[][]) {
            return (float[][]) value;
        } else if (value instanceof double[][]) {
            final double[][] doubleMatrix = (double[][]) value;
            return checkedCastMatrix(doubleMatrix);
        } else {
            //we need to extend this as needed
            throw UnknownArgumentException.newInstance(Object.class, value);
        }
    }

    public static float[][] checkedCastMatrix(final double[][] value) {
        final float[][] floatMatrix = new float[value.length][];
        for (int row = 0; row < value.length; row++) {
            final double[] vector = value[row];
            floatMatrix[row] = checkedCastVector(vector);
        }
        return floatMatrix;
    }

}
