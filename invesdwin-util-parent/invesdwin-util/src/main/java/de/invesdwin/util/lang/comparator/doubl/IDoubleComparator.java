package de.invesdwin.util.lang.comparator.doubl;

import org.apache.commons.math3.util.MathArrays.OrderDirection;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.lang.comparator.IComparator;

public interface IDoubleComparator extends IComparator<Double> {

    @Override
    IDoubleComparator asNullSafe();

    @Override
    default IDoubleComparator asNullSafe(final boolean nullSafe) {
        if (nullSafe) {
            return asNullSafe();
        } else {
            return asNotNullSafe();
        }
    }

    @Override
    IDoubleComparator asNotNullSafe();

    @Override
    default IDoubleComparator asNotNullSafe(final boolean notNullSafe) {
        if (notNullSafe) {
            return asNotNullSafe();
        } else {
            return asNullSafe();
        }
    }

    @Override
    IDoubleComparator asAscending();

    @Override
    default IDoubleComparator asAscending(final boolean ascending) {
        if (ascending) {
            return asAscending();
        } else {
            return asDescending();
        }
    }

    @Override
    IDoubleComparator asDescending();

    @Override
    default IDoubleComparator asDescending(final boolean descending) {
        if (descending) {
            return asDescending();
        } else {
            return asAscending();
        }
    }

    int comparePrimitive(double e1, double e2);

    default void sortPrimitive(final double[] array) {
        if (isAscending()) {
            Arrays.sortInPlace(array, OrderDirection.INCREASING);
        } else {
            Arrays.sortInPlace(array, OrderDirection.DECREASING);
        }
    }

}
