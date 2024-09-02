package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.comparator.AComparator;
import de.invesdwin.util.lang.comparator.IComparator;

@Immutable
public final class Numbers {

    public static final IComparator<Number> COMPARATOR = new AComparator<Number>() {
        @Override
        public int compareTypedNotNullSafe(final Number o1, final Number o2) {
            return Double.compare(o1.doubleValue(), o2.doubleValue());
        }
    };

    private Numbers() {}

}
