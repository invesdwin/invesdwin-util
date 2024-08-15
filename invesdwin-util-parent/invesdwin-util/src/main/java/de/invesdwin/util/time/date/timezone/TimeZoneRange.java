package de.invesdwin.util.time.date.timezone;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.ACriteriaComparator;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.LongPair;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.range.TimeRange;

@Immutable
public final class TimeZoneRange extends AValueObject {

    public static final String SEPARATOR = ";";

    public static final IComparator<TimeZoneRange> COMPARATOR = new ACriteriaComparator<TimeZoneRange>() {
        @Override
        public Comparable<?> getCompareCriteriaNotNullSafe(final TimeZoneRange e) {
            return e.toString();
        }
    };

    private final FTimeZone from;
    private final FTimeZone to;

    private TimeZoneRange(final FTimeZone fromAndTo) {
        this(fromAndTo, fromAndTo);
    }

    private TimeZoneRange(final FTimeZone from, final FTimeZone to) {
        if (from == null && to == null) {
            throw new IllegalArgumentException("either from or to should not be null");
        }
        this.from = from;
        this.to = to;
    }

    public FTimeZone getFrom() {
        return from;
    }

    public FTimeZone getTo() {
        return to;
    }

    @Override
    public String toString() {
        if (Objects.equals(getFrom(), getTo())) {
            return String.valueOf(getFrom());
        } else {
            return getFrom() + SEPARATOR + getTo();
        }
    }

    @Override
    public int compareTo(final Object o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(TimeZoneRange.class, from, to);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FTimeZone) {
            final FTimeZone cObj = (FTimeZone) obj;
            return equals(cObj);
        } else if (obj instanceof TimeZoneRange) {
            final TimeZoneRange cObj = (TimeZoneRange) obj;
            return Objects.equals(from, cObj.from) && Objects.equals(to, cObj.to);
        } else {
            return false;
        }
    }

    public boolean equals(final FTimeZone timeZone) {
        return Objects.equals(from, timeZone) && Objects.equals(to, timeZone);
    }

    @Override
    public TimeZoneRange clone() {
        return (TimeZoneRange) super.clone();
    }

    public static TimeZoneRange valueOf(final String str) {
        if (Strings.isBlankOrNullText(str)) {
            return null;
        }
        final String[] split = Strings.splitPreserveAllTokens(str, SEPARATOR);
        if (split.length == 0) {
            return null;
        } else if (split.length == 1) {
            final FTimeZone startAndEnd = TimeZones.getFTimeZone(split[0]);
            return new TimeZoneRange(startAndEnd);
        } else if (split.length == 2) {
            final FTimeZone start = TimeZones.getFTimeZone(split[0]);
            final FTimeZone end = TimeZones.getFTimeZone(split[1]);
            return new TimeZoneRange(start, end);
        } else {
            throw new IllegalArgumentException("Expecting 1 or 2 timeZones separated by [" + SEPARATOR + "]");
        }
    }

    public static TimeZoneRange valueOf(final FTimeZone startAndEnd) {
        if (startAndEnd == null) {
            return null;
        } else {
            return new TimeZoneRange(startAndEnd);
        }
    }

    public static TimeZoneRange valueOf(final FTimeZone start, final FTimeZone end) {
        if (start == null && end == null) {
            return null;
        } else {
            return new TimeZoneRange(start, end);
        }
    }

    public static FTimeZone extractFrom(final TimeZoneRange range) {
        if (range == null) {
            return null;
        } else {
            return range.getFrom();
        }
    }

    public static FTimeZone extractTo(final TimeZoneRange range) {
        if (range == null) {
            return null;
        } else {
            return range.getTo();
        }
    }

    public LongPair getTimeZoneOffsetMilliseconds(final FDate time) {
        final long millis = time.millisValue();
        return getTimeZoneOffsetMilliseconds(millis);
    }

    private LongPair getTimeZoneOffsetMilliseconds(final long millis) {
        if (getFrom() == getTo()) {
            final long fromAndTo = FDateMillis.getTimeZoneOffsetMilliseconds(millis, getFrom());
            return LongPair.of(fromAndTo, fromAndTo);
        } else {
            final long from = FDateMillis.getTimeZoneOffsetMilliseconds(millis, getFrom());
            final long to = FDateMillis.getTimeZoneOffsetMilliseconds(millis, getTo());
            return LongPair.of(from, to);
        }
    }

    public TimeRange applyTimeZoneOffset(final FDate time) {
        final long millis = time.millisValue();
        return applyTimeZoneOffset(millis);
    }

    public TimeRange applyTimeZoneOffset(final long millis) {
        if (getFrom() == getTo()) {
            final FDate fromAndTo = new FDate(FDateMillis.applyTimeZoneOffset(millis, getFrom()));
            return new TimeRange(fromAndTo, fromAndTo);
        } else {
            final FDate from = new FDate(FDateMillis.applyTimeZoneOffset(millis, getFrom()));
            final FDate to = new FDate(FDateMillis.applyTimeZoneOffset(millis, getTo()));
            return new TimeRange(from, to);
        }
    }

    public boolean isSame() {
        return from == to;
    }

}
