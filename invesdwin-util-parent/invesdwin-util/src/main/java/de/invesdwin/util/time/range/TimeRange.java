package de.invesdwin.util.time.range;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.bean.tuple.IPair;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.ACriteriaComparator;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.IFDateProvider;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public class TimeRange extends AValueObject {

    public static final IComparator<TimeRange> COMPARATOR = new ACriteriaComparator<TimeRange>() {
        @Override
        public Comparable<?> getCompareCriteriaNotNullSafe(final TimeRange e) {
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

    public String toStringWithoutDuration() {
        return getFrom() + " - " + getTo();
    }

    public TimeRange asNonNull(final IFDateProvider fromNullReplacement, final IFDateProvider toNullReplacement) {
        final FDate usedFrom;
        if (from == null) {
            usedFrom = fromNullReplacement.asFDate();
        } else {
            usedFrom = from;
        }
        final FDate usedTo;
        if (to == null) {
            usedTo = toNullReplacement.asFDate();
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

    public boolean containsInclusive(final FDate time) {
        return FDates.isBetweenInclusive(time, from, to);
    }

    public boolean containsExclusive(final FDate time) {
        return FDates.isBetweenExclusive(time, from, to);
    }

    public boolean containsInclusiveNotNullSafe(final FDate time) {
        return FDates.isBetweenInclusiveNotNullSafe(time, from, to);
    }

    public boolean containsExclusiveNotNullSafe(final FDate time) {
        return FDates.isBetweenExclusiveNotNullSafe(time, from, to);
    }

    @Override
    public int compareTo(final Object o) {
        return COMPARATOR.compare(this, o);
    }

    public boolean isSame() {
        final boolean fromNull = from == null;
        final boolean toNull = to == null;
        if (fromNull == toNull) {
            return true;
        }
        if (fromNull != toNull) {
            return false;
        }
        return from.equalsNotNullSafe(to);
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

    public TimeRange applyTimeZoneOffset(final long offsetMilliseconds) {
        if (offsetMilliseconds == 0) {
            return this;
        } else {
            return new TimeRange(from.applyTimeZoneOffset(offsetMilliseconds),
                    to.applyTimeZoneOffset(offsetMilliseconds));
        }
    }

    /**
     * WARNING: this can cause issues when apply/revert is used with offsetTimeZone because right at daylight saving
     * time switch the reference changes and can cause 1 hour difference. So better use getTimeZoneOffset as a long and
     * use apply/revert with that long value instead of this dynamic version.
     */
    @Deprecated
    public TimeRange applyTimeZoneOffset(final FTimeZone offsetTimeZone) {
        if (offsetTimeZone == null) {
            return this;
        } else {
            return new TimeRange(from.applyTimeZoneOffset(offsetTimeZone), to.applyTimeZoneOffset(offsetTimeZone));
        }
    }

    public TimeRange revertTimeZoneOffset(final long offsetMilliseconds) {
        if (offsetMilliseconds == 0) {
            return this;
        } else {
            return new TimeRange(from.revertTimeZoneOffset(offsetMilliseconds),
                    to.revertTimeZoneOffset(offsetMilliseconds));
        }
    }

    /**
     * WARNING: this can cause issues when apply/revert is used with offsetTimeZone because right at daylight saving
     * time switch the reference changes and can cause 1 hour difference. So better use getTimeZoneOffset as a long and
     * use apply/revert with that long value instead of this dynamic version.
     */
    @Deprecated
    public TimeRange revertTimeZoneOffset(final FTimeZone offsetTimeZone) {
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
        return (from == null || from.isBeforeOrEqualToNotNullSafe(FDates.MIN_DATE))
                && (to == null || to.isAfterOrEqualToNotNullSafe(FDates.MAX_DATE));
    }

    public TimeRange limit(final TimeRange timeRange) {
        return limit(timeRange.getFrom(), timeRange.getTo());
    }

    public TimeRange limit(final FDate from, final FDate to) {
        return new TimeRange(getFrom().orHigher(from), getTo().orLower(to));
    }

    @Override
    public TimeRange clone() {
        return (TimeRange) super.clone();
    }

    public TimeRange asNonUnlimited(final IFDateProvider minProvider, final IFDateProvider maxProvider) {
        final FDate usedFrom;
        if (from == null || from.isBeforeOrEqualToNotNullSafe(FDates.MIN_DATE)) {
            usedFrom = minProvider.asFDate();
        } else {
            usedFrom = from;
        }
        final FDate usedTo;
        if (to == null || to.isAfterOrEqualToNotNullSafe(FDates.MAX_DATE)) {
            usedTo = maxProvider.asFDate();
        } else {
            usedTo = to;
        }
        return new TimeRange(usedFrom, usedTo);
    }

    public static TimeRange valueOf(final List<? extends IPair<FDate, ?>> equityCurve) {
        final IPair<FDate, ?> initialEquity = equityCurve.get(0);
        final IPair<FDate, ?> finalEquity = equityCurve.get(equityCurve.size() - 1);
        return new TimeRange(initialEquity.getFirst(), finalEquity.getFirst());
    }

    public TimeRange asNonNullNow() {
        final FDate usedFrom;
        if (from == null) {
            usedFrom = new FDate();
        } else {
            usedFrom = from;
        }
        final FDate usedTo;
        if (to == null) {
            usedTo = new FDate();
        } else {
            usedTo = to;
        }
        return new TimeRange(usedFrom, usedTo);
    }

    public TimeRange addDuration(final Duration duration) {
        return new TimeRange(from.add(duration), to.add(duration));
    }

    public TimeRange subtractDuration(final Duration duration) {
        return new TimeRange(from.subtract(duration), to.subtract(duration));
    }

}
