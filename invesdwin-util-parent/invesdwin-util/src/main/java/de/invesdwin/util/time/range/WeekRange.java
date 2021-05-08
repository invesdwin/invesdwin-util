package de.invesdwin.util.time.range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDates;
import de.invesdwin.util.time.fdate.FTimeZone;
import de.invesdwin.util.time.fdate.FWeekTime;

@Immutable
public class WeekRange extends AValueObject {

    public static final String FROM_TO_SEPARATOR = "-";

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
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("from [" + from + "] should be before to [" + to + "]");
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
        return getFrom() + FROM_TO_SEPARATOR + getTo();
    }

    public String toNumberString() {
        return getFrom().toNumberString() + FROM_TO_SEPARATOR + getTo().toNumberString();
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

    public boolean contains(final FDate time, final FTimeZone timeZone) {
        return contains(time.applyTimeZoneOffset(timeZone));
    }

    public boolean contains(final FDate time) {
        final FDate weekendFrom = time.setFWeekTime(from);
        FDate weekendTo = time.setFWeekTime(to);
        if (weekendTo.isBeforeOrEqualTo(weekendFrom)) {
            weekendTo = weekendTo.addWeeks(1);
        }
        return FDates.isBetween(time, weekendFrom, weekendTo);
    }

    @Override
    public int compareTo(final Object o) {
        return COMPARATOR.compare(this, o);
    }

    public static WeekRange valueOf(final String value) {
        if (Strings.isBlankOrNullText(value)) {
            return null;
        }
        final String[] args = Strings.splitPreserveAllTokens(value, FROM_TO_SEPARATOR);
        try {
            if (args.length == 2) {
                final FWeekTime from = FWeekTime.valueOf(args[0], false);
                final FWeekTime to = FWeekTime.valueOf(args[1], true);
                return new WeekRange(from, to);
            } else {
                throw new IllegalArgumentException("Expecting two arguments for from and to (e.g. ["
                        + new WeekRange(new FWeekTime(new FDate().addDays(-1)), new FWeekTime(new FDate())) + "])");
            }
        } catch (final Throwable t) {
            throw new RuntimeException("Args: " + Arrays.toString(args) + " from " + value, t);
        }
    }

    public static WeekRange valueOfOrNull(final String value) {
        if (Strings.isBlankOrNullText(value)) {
            return null;
        }
        final String[] args = Strings.splitPreserveAllTokens(value, FROM_TO_SEPARATOR);
        try {
            if (args.length == 2) {
                final FWeekTime from = FWeekTime.valueOf(args[0], false);
                final FWeekTime to = FWeekTime.valueOf(args[1], true);
                return new WeekRange(from, to);
            } else {
                return null;
            }
        } catch (final Throwable t) {
            return null;
        }
    }
}
