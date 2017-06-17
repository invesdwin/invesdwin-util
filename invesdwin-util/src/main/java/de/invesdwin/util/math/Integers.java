package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.AIntegersStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastIntegers;
import de.invesdwin.util.math.internal.CheckedCastIntegersObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.AIntegersStaticFacade", targets = {
        CheckedCastIntegers.class, CheckedCastIntegersObj.class, com.google.common.primitives.Ints.class })
@Immutable
public final class Integers extends AIntegersStaticFacade {

    public static final ADelegateComparator<Integer> COMPARATOR = new ADelegateComparator<Integer>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Integer e) {
            return e;
        }
    };

    private Integers() {}

    public static int[] toArray(final Collection<? extends Number> vector) {
        if (vector == null) {
            return null;
        }
        return AIntegersStaticFacade.toArray(vector);
    }

    public static int[] toArrayVector(final Collection<Integer> vector) {
        return toArray(vector);
    }

    public static int[][] toArrayMatrix(final List<? extends List<Integer>> matrix) {
        if (matrix == null) {
            return null;
        }
        final int[][] arrayMatrix = new int[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Integer> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Integer> asList(final int... vector) {
        if (vector == null) {
            return null;
        } else {
            return AIntegersStaticFacade.asList(vector);
        }
    }

    public static List<Integer> asListVector(final int[] vector) {
        return asList(vector);
    }

    public static List<List<Integer>> asListMatrix(final int[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Integer>> matrixAsList = new ArrayList<List<Integer>>(matrix.length);
        for (final int[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

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

}
