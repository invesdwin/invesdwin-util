package de.invesdwin.util.time.range.day;

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
import de.invesdwin.util.time.fdate.FDayTime;
import de.invesdwin.util.time.fdate.FTimeZone;

@Immutable
public class DayRange extends AValueObject implements IDayRangeData {

    public static final ADelegateComparator<DayRange> COMPARATOR = new ADelegateComparator<DayRange>() {
        @Override
        protected Comparable<?> getCompareCriteria(final DayRange e) {
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
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("from [" + from + "] should be before to [" + to + "]");
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
        if (from == null || to == null) {
            return null;
        } else {
            return new Duration(FDate.MIN_DATE.setFDayTime(from), FDate.MIN_DATE.addWeeks(1).setFDayTime(to));
        }
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

    public boolean contains(final FDate time, final FTimeZone timeZone) {
        return contains(time.applyTimeZoneOffset(timeZone));
    }

    public boolean contains(final FDate time) {
        final FDate weekendFrom = time.setFDayTime(from);
        FDate weekendTo = time.setFDayTime(to);
        if (weekendTo.isBeforeOrEqualTo(weekendFrom)) {
            weekendTo = weekendTo.addWeeks(1);
        }
        return FDates.isBetween(time, weekendFrom, weekendTo);
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

}
