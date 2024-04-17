package de.invesdwin.util.time;

import java.util.ArrayList;
import java.util.List;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.range.TimeRange;

public interface ITimeRangedValue<E> {

    E asValue();

    TimeRange getTimeRange();

    default FDate getStartTime() {
        return getTimeRange().getFrom();
    }

    default FDate getEndTime() {
        return getTimeRange().getTo();
    }

    static <T> T extractValue(final ITimeRangedValue<T> value) {
        if (value == null) {
            return null;
        } else {
            return value.asValue();
        }
    }

    static <T extends ITimeRangedValue<?>> List<TimeRange> extractTimeRanges(final Iterable<T> values) {
        final List<TimeRange> timeRanges = new ArrayList<TimeRange>();
        for (final T pr : values) {
            timeRanges.add(pr.getTimeRange());
        }
        return timeRanges;
    }

    static <E, T extends ITimeRangedValue<E>> List<E> extractValues(final Iterable<T> values) {
        final List<E> timeRanges = new ArrayList<E>();
        for (final T pr : values) {
            timeRanges.add(pr.asValue());
        }
        return timeRanges;
    }

    static <T extends ITimeRangedValue<?>> T extractValueWithMaxPeriod(final Iterable<T> values) {
        T maxPeriod = null;
        for (final T pr : values) {
            if (maxPeriod == null
                    || maxPeriod.getTimeRange().getDuration().isLessThan(pr.getTimeRange().getDuration())) {
                maxPeriod = pr;
            }
        }
        return maxPeriod;
    }

    static <T extends ITimeRangedValue<?>> T extractValueWithMinPeriod(final Iterable<T> values) {
        T minPeriod = null;
        for (final T pr : values) {
            if (minPeriod == null
                    || minPeriod.getTimeRange().getDuration().isGreaterThan(pr.getTimeRange().getDuration())) {
                minPeriod = pr;
            }
        }
        return minPeriod;
    }

}
