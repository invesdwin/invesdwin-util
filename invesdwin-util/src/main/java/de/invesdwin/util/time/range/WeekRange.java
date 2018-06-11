package de.invesdwin.util.time.range;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDates;

@Immutable
public class WeekRange extends AValueObject {

    public static final ADelegateComparator<WeekRange> COMPARATOR = new ADelegateComparator<WeekRange>() {
        @Override
        protected Comparable<?> getCompareCriteria(final WeekRange e) {
            return e.getFrom();
        }
    };

    private final FWeekTime from;
    private final FWeekTime to;

    public WeekRange(final FWeekTime from, final FWeekTime to) {
        if (from == null) {
            throw new NullPointerException("from should not be null");
        }
        if (to == null) {
            throw new NullPointerException("to should not be null");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from [" + from + "] should not be after to [" + to + "]");
        }
        this.from = from;
        this.to = to;
    }

    public FWeekTime getFrom() {
        return from;
    }

    public FWeekTime getTo() {
        return to;
    }

    public Duration getDuration() {
        if (from == null || to == null) {
            return null;
        } else {
            return new Duration(FDate.MIN_DATE.setFWeekTime(from), FDate.MIN_DATE.addWeeks(1).setFWeekTime(to));
        }
    }

    @Override
    public String toString() {
        return getFrom() + "-" + getTo();
    }

    public static List<Duration> extractDurations(final Iterable<WeekRange> timeRanges) {
        final List<Duration> durations = new ArrayList<Duration>();
        for (final WeekRange pr : timeRanges) {
            durations.add(pr.getDuration());
        }
        return durations;
    }

    public static List<FWeekTime> extractFroms(final Iterable<WeekRange> timeRanges) {
        final List<FWeekTime> durations = new ArrayList<FWeekTime>();
        for (final WeekRange pr : timeRanges) {
            durations.add(pr.getFrom());
        }
        return durations;
    }

    public static List<FWeekTime> extractTos(final Iterable<WeekRange> timeRanges) {
        final List<FWeekTime> durations = new ArrayList<FWeekTime>();
        for (final WeekRange pr : timeRanges) {
            durations.add(pr.getTo());
        }
        return durations;
    }

    public boolean isZeroDuration() {
        return from == null || to == null || getDuration().isZero();
    }

    public boolean contains(final FDate time) {
        return FDates.isBetween(time, time.setFWeekTime(from), time.addWeeks(1).setFWeekTime(to));
    }

    @Override
    public int compareTo(final Object o) {
        return COMPARATOR.compare(this, o);
    }
}
