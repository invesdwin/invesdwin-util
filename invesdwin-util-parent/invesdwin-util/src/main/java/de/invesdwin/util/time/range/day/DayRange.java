package de.invesdwin.util.time.range.day;

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
import de.invesdwin.util.time.date.FDayTime;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import de.invesdwin.util.time.date.timezone.TimeZoneRange;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.range.TimeRange;

@Immutable
public class DayRange extends AValueObject implements IDayRangeData {

    public static final IComparator<DayRange> COMPARATOR = new ACriteriaComparator<DayRange>() {
        @Override
        public Comparable<?> getCompareCriteriaNotNullSafe(final DayRange e) {
            return e.getFrom();
        }
    };

    private final FDayTime from;
    private final FDayTime to;

    public DayRange(final FDayTime from, final FDayTime to) {
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
    public FDayTime getFrom() {
        return from;
    }

    @Override
    public FDayTime getTo() {
        return to;
    }

    public Duration getDuration() {
        return getDuration(FDates.MIN_DATE);
    }

    public Duration getDuration(final FDate day) {
        if (from == null || to == null) {
            return null;
        } else {
            final FDate dayFrom = day.setFDayTime(from);
            final FDate dayTo = day.setFDayTime(to);
            return getDuration(dayFrom, dayTo);
        }
    }

    public Duration getDuration(final FDate day, final FTimeZone offsetTimeZone) {
        if (from == null || to == null) {
            return null;
        } else {
            final FDate dayZoned = day.applyTimeZoneOffset(offsetTimeZone);
            final FDate dayFrom = dayZoned.setFDayTime(from);
            final FDate dayTo = dayZoned.setFDayTime(to);
            return getDuration(dayFrom, dayTo);
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
            final FDate dayFrom = dayZoned.getFrom().setFDayTime(from);
            final FDate dayTo = dayZoned.getTo().setFDayTime(to);
            return getDuration(dayFrom, dayTo);
        }
    }

    private Duration getDuration(final FDate dayFrom, final FDate dayTo) {
        final FDate endDayTime;
        if (dayFrom.isBeforeNotNullSafe(dayTo)) {
            endDayTime = dayTo;
        } else {
            endDayTime = dayTo.addDays(1);
        }
        return new Duration(dayFrom, endDayTime);
    }

    @Override
    public String toString() {
        return getFrom() + FROM_TO_SEPARATOR + getTo();
    }

    public String toNumberString() {
        return getFrom().toNumberString() + FROM_TO_SEPARATOR + getTo().toNumberString();
    }

    public static List<Duration> extractDurations(final Iterable<DayRange> timeRanges) {
        final List<Duration> durations = new ArrayList<Duration>();
        for (final DayRange pr : timeRanges) {
            durations.add(pr.getDuration());
        }
        return durations;
    }

    public static List<FDayTime> extractFroms(final Iterable<DayRange> timeRanges) {
        final List<FDayTime> durations = new ArrayList<FDayTime>();
        for (final DayRange pr : timeRanges) {
            durations.add(pr.getFrom());
        }
        return durations;
    }

    public static List<FDayTime> extractTos(final Iterable<DayRange> timeRanges) {
        final List<FDayTime> durations = new ArrayList<FDayTime>();
        for (final DayRange pr : timeRanges) {
            durations.add(pr.getTo());
        }
        return durations;
    }

    public boolean isZeroDuration() {
        return from == null || to == null || getDuration().isZero();
    }

    public boolean containsInclusive(final FDate time, final FTimeZone timeZone) {
        if (time == null) {
            return false;
        }
        return containsInclusive(time.applyTimeZoneOffset(timeZone));
    }

    public boolean containsInclusive(final FDate time) {
        if (time == null) {
            return false;
        }
        final FDate sessionFrom = time.setFDayTime(from);
        FDate sessionTo = time.setFDayTime(to);
        if (sessionTo.isBeforeOrEqualToNotNullSafe(sessionFrom)) {
            sessionTo = sessionTo.addDays(1);
        }
        return FDates.isBetweenInclusiveNotNullSafe(time, sessionFrom, sessionTo);
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
        final FDate sessionFrom = time.setFDayTime(from);
        FDate sessionTo = time.setFDayTime(to);
        if (sessionTo.isBeforeOrEqualToNotNullSafe(sessionFrom)) {
            sessionTo = sessionTo.addDays(1);
        }
        return FDates.isBetweenExclusiveNotNullSafe(time, sessionFrom, sessionTo);
    }

    @Override
    public int compareTo(final Object o) {
        return COMPARATOR.compare(this, o);
    }

    public static DayRange valueOf(final String value) {
        if (Strings.isBlankOrNullText(value)) {
            return null;
        }
        final String[] args = Strings.splitPreserveAllTokens(value, FROM_TO_SEPARATOR);
        try {
            if (args.length == 2) {
                final FDayTime from = FDayTime.valueOf(args[0], false);
                final FDayTime to = FDayTime.valueOf(args[1], false);
                if (from.equals(to)) {
                    //no session
                    return null;
                } else {
                    return new DayRange(from, to);
                }
            } else {
                throw new IllegalArgumentException("Expecting two arguments for from and to (e.g. ["
                        + new DayRange(new FDayTime(new FDate().addDays(-1)), new FDayTime(new FDate())) + "])");
            }
        } catch (final Throwable t) {
            throw new RuntimeException("Args: " + Arrays.toString(args) + " from " + value, t);
        }
    }

    public static DayRange valueOfOrNull(final String value) {
        if (Strings.isBlankOrNullText(value)) {
            return null;
        }
        final String[] args = Strings.splitPreserveAllTokens(value, FROM_TO_SEPARATOR);
        try {
            if (args.length == 2) {
                final FDayTime from = FDayTime.valueOf(args[0], false);
                final FDayTime to = FDayTime.valueOf(args[1], false);
                if (from.equals(to)) {
                    //no session
                    return null;
                } else {
                    return new DayRange(from, to);
                }
            } else {
                return null;
            }
        } catch (final Throwable t) {
            return null;
        }
    }

    public static DayRange valueOf(final IDayRangeData value) {
        if (value == null) {
            return null;
        } else if (value instanceof DayRange) {
            return (DayRange) value;
        } else {
            return new DayRange(FDayTime.valueOf(value.getFrom()), FDayTime.valueOf(value.getTo()));
        }
    }

    public static DayRange valueOf(final TimeRange value) {
        if (value == null) {
            return null;
        } else if (value.isSame()) {
            final FDayTime fromAndTo = FDayTime.valueOf(value.getFrom());
            return new DayRange(fromAndTo, fromAndTo);
        } else {
            final FDayTime from = FDayTime.valueOf(value.getFrom());
            final FDayTime to = FDayTime.valueOf(value.getTo());
            return new DayRange(from, to);
        }
    }

}
