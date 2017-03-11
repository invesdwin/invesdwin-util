package de.invesdwin.util.math.decimal;

import java.util.List;

import javax.annotation.concurrent.Immutable;

/**
 * http://stackoverflow.com/questions/11955728/how-to-calculate-the-median-of-an-array
 */
@Immutable
public abstract class ADecimalMedian<E extends ADecimal<E>> {

    protected abstract List<E> getSortedList();

    public E getMedian() {
        final List<E> sortedList = getSortedList();
        if (sortedList.isEmpty()) {
            return null;
        }
        if (sortedList.size() == 1) {
            return sortedList.get(0);
        }
        final int middle = sortedList.size() / 2;
        final E median;
        if (sortedList.size() % 2 == 0) {
            final E medianA = sortedList.get(middle);
            final E medianB = sortedList.get(middle - 1);
            median = medianA.add(medianB).divide(2);
        } else {
            median = sortedList.get(middle + 1);
        }
        return median;
    }

}
