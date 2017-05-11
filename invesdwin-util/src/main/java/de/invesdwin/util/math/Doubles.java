package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ADoublesStaticFacade;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ADoublesStaticFacade", targets = {
        com.google.common.primitives.Doubles.class })
@Immutable
public final class Doubles extends ADoublesStaticFacade {

    public static final ADelegateComparator<Double> COMPARATOR = new ADelegateComparator<Double>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Double e) {
            return e;
        }
    };

    private Doubles() {}

    public static Double max(final Double first, final Double second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.max(first, second);
        }
    }

    public static Double min(final Double first, final Double second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.min(first, second);
        }
    }

    public static Double between(final Double value, final Double min, final Double max) {
        return max(min(value, max), min);
    }

    public static double[] checkedCastVector(final float[] value) {
        final double[] doubleVector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            doubleVector[i] = value[i];
        }
        return doubleVector;
    }

    public static double[][] checkedCastMatrix(final float[][] value) {
        final double[][] doubleMatrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            final float[] vector = value[row];
            doubleMatrix[row] = checkedCastVector(vector);
        }
        return doubleMatrix;
    }

    public static double[] checkedCastVector(final long[] value) {
        final double[] doubleVector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            doubleVector[i] = value[i];
        }
        return doubleVector;
    }

    public static double[][] checkedCastMatrix(final long[][] value) {
        final double[][] doubleMatrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            final long[] vector = value[row];
            doubleMatrix[row] = checkedCastVector(vector);
        }
        return doubleMatrix;
    }

}
