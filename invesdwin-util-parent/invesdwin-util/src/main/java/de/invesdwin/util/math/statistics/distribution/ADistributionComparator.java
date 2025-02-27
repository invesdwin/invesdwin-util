package de.invesdwin.util.math.statistics.distribution;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.comparator.AComparator;
import de.invesdwin.util.math.decimal.scaled.Percent;

@Immutable
public abstract class ADistributionComparator<E> extends AComparator<E> {

    public abstract String getName();

    public abstract String getRankName();

    public abstract String getStatisticName();

    public abstract String getConfidenceName();

    @Override
    public int compareTypedNotNullSafe(final E o1, final E o2) {
        final double tStatistic = getStatistic(o1, o2);
        if (isHigherBetter(o1)) {
            if (tStatistic < 0D) {
                return 1;
            } else if (tStatistic > 0D) {
                return -1;
            } else {
                return 0;
            }
        } else {
            if (tStatistic < 0D) {
                return -1;
            } else if (tStatistic > 0D) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    protected abstract boolean isHigherBetter(E element);

    public abstract double getStatistic(E o1, E o2);

    /**
     * This is the probability that the distributions of the two samples differ. Probability of dependence would be the
     * inverse. Depending on the algorithm H0 and H1 can be assigned to one or the other and needs to be transformed via
     * 1-pValue which should be done by the implementation automatically.
     */
    public abstract Percent getProbabilityOfDifference(E o1, E o2);

}
