package de.invesdwin.util.time.range.day;

import de.invesdwin.util.lang.comparator.ACriteriaComparator;
import de.invesdwin.util.lang.comparator.IComparator;

public interface IDayTimeData {

    IComparator<IDayTimeData> COMPARATOR = new ACriteriaComparator<IDayTimeData>() {
        @Override
        public Comparable<?> getCompareCriteriaNotNullSafe(final IDayTimeData e) {
            return e.intValue();
        }
    };

    int intValue();

}
