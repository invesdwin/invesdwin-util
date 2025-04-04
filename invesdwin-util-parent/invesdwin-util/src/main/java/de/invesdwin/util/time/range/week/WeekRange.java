package de.invesdwin.util.time.range.week;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.lang.comparator.ACriteriaComparator;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.FWeekTime;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import de.invesdwin.util.time.date.timezone.TimeZoneRange;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.range.TimeRange;

@Immutable
public class WeekRange extends AValueObject implements IWeekRangeData {

    public static final IComparator<WeekRange> COMPARATOR = new ACriteriaComparator<WeekRange>() {
        @Override
        public Comparable<?> getCompareCriteriaNotNullSafe(final WeekRange e) {
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
        this.from = from;
        this.to = to;
    }

    @Override
    public FWeekTime getFrom() {
        return from;
    }

    @Override
    public FWeekTime getTo() {
        return to;
    }

    public Duration getDuration() {
        return getDuration(FDates.MIN_DATE);
    }

    public Duration getDuration(final FDate day) {
        if (from == null || to == null) {
            return null;
        } else {
            final FDate weekFrom = day.setFWeekTime(from);
            final FDate weekTo = day.setFWeekTime(to);
            return getDuration(weekFrom, weekTo);
        }
    }

    public Duration getDuration(final FDate day, final FTimeZone offsetTimeZone) {
        if (from == null || to == null) {
            return null;
        } else {
            final FDate dayZoned = day.applyTimeZoneOffset(offsetTimeZone);
            final FDate weekFrom = dayZoned.setFWeekTime(from);
            final FDate weekTo = dayZoned.setFWeekTime(to);
            return getDuration(weekFrom, weekTo);
        }
    }

    public Duration getDuration(final FDate day, final TimeZoneRange offsetTimeZone) {
        if (offsetTimeZone == null) {
            return getDuration(day);
        }
        if (offsetTimeZone.isSame()) {
            return getDuration(day, offsetTimeZone.getFrom());
        }
        if (from == null || to == null) {
            return null;
        } else {
            final TimeRange dayZoned = day.applyTimeZoneOffset(offsetTimeZone);
            final FDate weekFrom = dayZoned.getFrom().setFWeekTime(from);
            final FDate weekTo = dayZoned.getTo().setFWeekTime(to);
            return getDuration(weekFrom, weekTo);
        }
    }

    private Duration getDuration(final FDate weekFrom, final FDate weekTo) {
        final FDate endWeekTime;
        if (weekFrom.isBeforeNotNullSafe(weekTo)) {
            endWeekTime = weekTo;
        } else {
            endWeekTime = weekTo.addWeeks(1);
        }
        return new Duration(weekFrom, endWeekTime);
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

    public boolean containsInclusive(final FDate time, final FTimeZone timeZone) {
        return containsInclusive(time.applyTimeZoneOffset(timeZone));
    }

    public boolean containsInclusive(final FDate time) {
        if (time == null) {
            return false;
        }
        final FDate weekendFrom = time.setFWeekTime(from);
        FDate weekendTo = time.setFWeekTime(to);
        if (weekendTo.isBeforeOrEqualTo(weekendFrom)) {
            weekendTo = weekendTo.addWeeks(1);
        }
        return FDates.isBetweenInclusiveNotNullSafe(time, weekendFrom, weekendTo);
    }

    public boolean containsExclusive(final FDate time, final FTimeZone timeZone) {
        if (time == null) {
            return false;
        }
        return containsExclusive(time.applyTimeZoneOffset(timeZone));
    }

    public boolean containsExclusive(final FDate time) {
        if (time == null) {
            return false;
        }
        final FDate weekendFrom = time.setFWeekTime(from);
        FDate weekendTo = time.setFWeekTime(to);
        if (weekendTo.isBeforeOrEqualTo(weekendFrom)) {
            weekendTo = weekendTo.addWeeks(1);
        }
        return FDates.isBetweenExclusiveNotNullSafe(time, weekendFrom, weekendTo);
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

    public static WeekRange valueOf(final IWeekRangeData value) {
        if (value == null) {
            return null;
        } else if (value instanceof WeekRange) {
            return (WeekRange) value;
        } else {
            return new WeekRange(FWeekTime.valueOf(value.getFrom()), FWeekTime.valueOf(value.getTo()));
        }
    }

    public static WeekRange valueOf(final TimeRange value) {
        if (value == null) {
            return null;
        } else if (value.isSame()) {
            final FWeekTime fromAndTo = FWeekTime.valueOf(value.getFrom());
            return new WeekRange(fromAndTo, fromAndTo);
        } else {
            final FWeekTime from = FWeekTime.valueOf(value.getFrom());
            final FWeekTime to = FWeekTime.valueOf(value.getTo());
            return new WeekRange(from, to);
        }
    }
}
