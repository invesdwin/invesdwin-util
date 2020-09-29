package de.invesdwin.util.time.range;

import java.nio.ByteBuffer;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDates;

@Immutable
public class TimeRange extends AValueObject {

    public static final ADelegateComparator<TimeRange> COMPARATOR = new ADelegateComparator<TimeRange>() {
        @Override
        protected Comparable<?> getCompareCriteria(final TimeRange e) {
            return e.getFrom();
        }
    };

    public static final TimeRange UNLIMITED = new TimeRange(null, null);

    private final FDate from;
    private final FDate to;

    public TimeRange(final FDate from, final FDate to) {
        this.from = from;
        this.to = to;
    }

    public FDate getFrom() {
        return from;
    }

    public FDate getTo() {
        return to;
    }

    public Duration getDuration() {
        if (from == null || to == null) {
            return null;
        } else {
            return new Duration(from, to);
        }
    }

    @Override
    public String toString() {
        return getFrom() + " -> " + getTo() + " => " + getDuration();
    }

    public TimeRange asNonNull(final FDate fromNullReplacement, final FDate toNullReplacement) {
        final FDate usedFrom;
        if (from == null) {
            usedFrom = fromNullReplacement;
        } else {
            usedFrom = from;
        }
        final FDate usedTo;
        if (to == null) {
            usedTo = toNullReplacement;
        } else {
            usedTo = to;
        }
        return new TimeRange(usedFrom, usedTo);
    }

    public static List<Duration> extractDurations(final Iterable<TimeRange> timeRanges) {
        final List<Duration> durations = new ArrayList<Duration>();
        for (final TimeRange pr : timeRanges) {
            durations.add(pr.getDuration());
        }
        return durations;
    }

    public static List<FDate> extractFroms(final Iterable<TimeRange> timeRanges) {
        final List<FDate> durations = new ArrayList<FDate>();
        for (final TimeRange pr : timeRanges) {
            durations.add(pr.getFrom());
        }
        return durations;
    }

    public static List<FDate> extractTos(final Iterable<TimeRange> timeRanges) {
        final List<FDate> durations = new ArrayList<FDate>();
        for (final TimeRange pr : timeRanges) {
            durations.add(pr.getTo());
        }
        return durations;
    }

    public static TimeRange avg(final List<TimeRange> timeRanges) {
        final FDate avgFrom = FDates.avg(extractFroms(timeRanges));
        final FDate avgTo = FDates.avg(extractTos(timeRanges));
        return new TimeRange(avgFrom, avgTo);
    }

    public boolean isZeroDuration() {
        return from == null || to == null || getDuration().isZero();
    }

    public static void putTimeRange(final ByteBuffer buffer, final TimeRange timeRange) {
        if (timeRange == null) {
            buffer.putLong(Long.MAX_VALUE);
            buffer.putLong(Long.MAX_VALUE);
        } else {
            FDates.putFDate(buffer, timeRange.getFrom());
            FDates.putFDate(buffer, timeRange.getTo());
        }
    }

    public static TimeRange extractTimeRange(final ByteBuffer buffer, final int index) {
        final long from = buffer.getLong(index);
        final long to = buffer.getLong(index + 8);
        if (from == Long.MAX_VALUE && to == Long.MAX_VALUE) {
            return null;
        } else {
            return new TimeRange(FDates.extractFDate(buffer, index), FDates.extractFDate(buffer, index + 8));
        }
    }

    public boolean contains(final FDate time) {
        return FDates.isBetween(time, from, to);
    }

    @Override
    public int compareTo(final Object o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(TimeRange.class, from, to);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TimeRange) {
            final TimeRange cObj = (TimeRange) obj;
            return Objects.equals(from, cObj.from) && Objects.equals(to, cObj.to);
        } else {
            return false;
        }
    }

    public TimeRange applyTimeZoneOffset(final ZoneId offsetTimeZone) {
        if (offsetTimeZone == null) {
            return this;
        } else {
            return new TimeRange(from.applyTimeZoneOffset(offsetTimeZone), to.applyTimeZoneOffset(offsetTimeZone));
        }
    }

    public TimeRange revertTimeZoneOffset(final ZoneId offsetTimeZone) {
        if (offsetTimeZone == null) {
            return this;
        } else {
            return new TimeRange(from.revertTimeZoneOffset(offsetTimeZone), to.revertTimeZoneOffset(offsetTimeZone));
        }
    }

    public static void assertNotNull(final TimeRange timeRange) {
        if (timeRange == null) {
            throw new NullPointerException("timeRange should not be null");
        }
        if (timeRange.getFrom() == null) {
            throw new NullPointerException("timeRange.from should not be null");
        }
        if (timeRange.getTo() == null) {
            throw new NullPointerException("timeRange.to should not be null");
        }

    }

    public boolean isUnlimited() {
        return isUnlimited(from, to);
    }

    public static boolean isUnlimited(final FDate from, final FDate to) {
        return (from == null || FDate.MIN_DATE.equalsNotNullSafe(from))
                && (to == null || FDate.MAX_DATE.equalsNotNullSafe(to));
    }

    public TimeRange limit(final TimeRange timeRange) {
        return limit(timeRange.getFrom(), timeRange.getTo());
    }

    public TimeRange limit(final FDate from, final FDate to) {
        return new TimeRange(getFrom().orHigher(from), getTo().orLower(to));
    }
}
