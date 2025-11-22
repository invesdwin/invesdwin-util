package de.invesdwin.util.time.range.week;

import de.invesdwin.util.lang.comparator.ACriteriaComparator;
import de.invesdwin.util.lang.comparator.IComparator;

public interface IWeekTimeData {

    IComparator<IWeekTimeData> COMPARATOR = new ACriteriaComparator<IWeekTimeData>() {
        @Override
        public Comparable<?> getCompareCriteriaNotNullSafe(final IWeekTimeData e) {
            return e.longValue();
        }
    };

    long longValue();

}
