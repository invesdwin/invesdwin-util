package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ADoublesStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastDoubles;
import de.invesdwin.util.math.internal.CheckedCastDoublesObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ADoublesStaticFacade", targets = {
        CheckedCastDoubles.class, CheckedCastDoublesObj.class, com.google.common.primitives.Doubles.class })
@Immutable
public final class Doubles extends ADoublesStaticFacade {

    public static final ADelegateComparator<Double> COMPARATOR = new ADelegateComparator<Double>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Double e) {
            return e;
        }
    };

    private Doubles() {}

    public static double[] toArray(final Collection<? extends Number> vector) {
        if (vector == null) {
            return null;
        }
        return ADoublesStaticFacade.toArray(vector);
    }

    public static double[] toArrayVector(final Collection<Double> vector) {
        return toArray(vector);
    }

    public static double[][] toArrayMatrix(final List<? extends List<Double>> matrix) {
        if (matrix == null) {
            return null;
        }
        final double[][] arrayMatrix = new double[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Double> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Double> asList(final double... vector) {
        if (vector == null) {
            return null;
        } else {
            return ADoublesStaticFacade.asList(vector);
        }
    }

    public static List<Double> asListVector(final double[] vector) {
        return asList(vector);
    }

    public static List<List<Double>> asListMatrix(final double[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Double>> matrixAsList = new ArrayList<List<Double>>(matrix.length);
        for (final double[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

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

}
