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

    public static Float checkedCast(final Number number) {
        if (number == null) {
            return null;
        } else if (number instanceof Double) {
            final double doubleValue = number.doubleValue();
            return checkedCast(doubleValue);
        } else if (number instanceof Long) {
            final long longValue = number.longValue();
            return checkedCast(longValue);
        } else {
            return number.floatValue();
        }
    }

    public static float checkedCast(final long longValue) {
        final float floatValue = longValue;
        return floatValue;
    }

    public static float checkedCast(final double doubleValue) {
        if (doubleValue < Float.MIN_VALUE || doubleValue > Float.MAX_VALUE) {
            throw new ArithmeticException("float overflow");
        }
        final float floatValue = (float) doubleValue;
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

    public static float[] checkedCastVector(final double[] vector) {
        final float[] floatVector = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            floatVector[i] = Floats.checkedCast(vector[i]);
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

    public static float[][] checkedCastMatrix(final double[][] matrix) {
        final float[][] floatMatrix = new float[matrix.length][];
        for (int row = 0; row < matrix.length; row++) {
            final double[] vector = matrix[row];
            floatMatrix[row] = checkedCastVector(vector);
        }
        return floatMatrix;
    }

}
