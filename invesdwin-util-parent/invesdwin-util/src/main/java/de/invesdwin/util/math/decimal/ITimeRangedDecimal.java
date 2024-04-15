package de.invesdwin.util.math.decimal;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.range.TimeRange;

public interface ITimeRangedDecimal<E extends ADecimal<E>> {

    E asDecimal();

    TimeRange getTimeRange();

    default FDate getStartTime() {
        return getTimeRange().getFrom();
    }

    default FDate getEndTime() {
        return getTimeRange().getTo();
    }

    static <T extends ADecimal<T>> T asDecimal(final ITimeRangedDecimal<T> value) {
        if (value == null) {
            return null;
        } else {
            return value.asDecimal();
        }
    }

}
