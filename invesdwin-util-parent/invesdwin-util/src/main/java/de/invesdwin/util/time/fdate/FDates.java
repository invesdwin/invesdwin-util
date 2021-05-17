package de.invesdwin.util.time.fdate;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.millis.FDatesMillis;

@ThreadSafe
public final class FDates {

    public static final int MISSING_INDEX = -1;
    public static final long MILLISECONDS_IN_DAY = FTimeUnit.MILLISECONDS_IN_DAY;
    public static final long MILLISECONDS_IN_HOUR = FTimeUnit.MILLISECONDS_IN_HOUR;
    public static final long MILLISECONDS_IN_MINUTE = FTimeUnit.MILLISECONDS_IN_MINUTE;
    public static final long MILLISECONDS_IN_SECOND = FTimeUnit.MILLISECONDS_IN_SECOND;
    private static FTimeZone defaultTimeZone;

    static {
        setDefaultTimeZone(new FTimeZone(TimeZone.getDefault()));
    }

    private FDates() {
    }

    public static void setDefaultTimeZone(final FTimeZone defaultTimeZone) {
        FDates.defaultTimeZone = defaultTimeZone;
    }

    public static FTimeZone getDefaultTimeZone() {
        return defaultTimeZone;
    }

    public static ICloseableIterable<FDate> iterable(final FDate start, final FDate end, final Duration increment) {
        return new FDateIterable(start, end, increment.getTimeUnit(), increment.intValue());
    }

    public static ICloseableIterable<FDate> iterable(final FDate start, final FDate end, final FTimeUnit timeUnit,
            final int incrementAmount) {
        return new FDateIterable(start, end, timeUnit, incrementAmount);
    }

    static class FDateIterable implements ICloseableIterable<FDate> {
        private final FDate startFinal;
        private final FDate endFinal;
        private final FTimeUnit timeUnit;
        private final int incrementAmount;

        FDateIterable(final FDate startFinal, final FDate endFinal, final FTimeUnit timeUnit,
                final int incrementAmount) {
            this.startFinal = startFinal;
            this.endFinal = endFinal;
            this.timeUnit = timeUnit;
            this.incrementAmount = incrementAmount;
            if (incrementAmount == 0) {
                throw new IllegalArgumentException("incrementAmount must not be 0");
            }
            if (startFinal.isBefore(endFinal) && incrementAmount < 0) {
                throw new IllegalArgumentException("When iterating forward [" + startFinal + " -> " + endFinal
                        + "], incrementAmount [" + incrementAmount + "] needs to be positive.");
            } else if (startFinal.isAfter(endFinal) && incrementAmount > 0) {
                throw new IllegalArgumentException("When iterating backward [" + startFinal + " -> " + endFinal
                        + "], incrementAmount [" + incrementAmount + "] needs to be negative.");
            }
        }

        @Override
        public ICloseableIterator<FDate> iterator() {
            if (incrementAmount > 0) {
                return new ICloseableIterator<FDate>() {

                    private FDate spot = startFinal;
                    private boolean first = true;
                    private boolean end = false;

                    @Override
                    public boolean hasNext() {
                        return first || spot.isBefore(endFinal);
                    }

                    @Override
                    public FDate next() {
                        if (first) {
                            first = false;
                            return spot;
                        } else {
                            if (spot.isAfter(endFinal) || end) {
                                throw new FastNoSuchElementException("FDateIterable: incrementing next reached end");
                            }
                            spot = spot.add(timeUnit, incrementAmount);
                            if (spot.isAfterOrEqualTo(endFinal)) {
                                end = true;
                                return endFinal;
                            } else {
                                return spot;
                            }
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void close() {
                        spot = endFinal;
                        first = false;
                        end = true;
                    }
                };
            } else {
                //reverse
                return new ICloseableIterator<FDate>() {

                    private boolean first = true;
                    private FDate spot = startFinal;
                    private boolean end = false;

                    @Override
                    public boolean hasNext() {
                        return first || spot.isAfter(endFinal);
                    }

                    @Override
                    public FDate next() {
                        if (first) {
                            first = false;
                            return spot;
                        } else {
                            if (spot.isBefore(endFinal) || end) {
                                throw new FastNoSuchElementException("FDateIterable: decrementing next reached end");
                            }
                            spot = spot.add(timeUnit, incrementAmount);
                            if (spot.isBeforeOrEqualTo(endFinal)) {
                                end = true;
                                return endFinal;
                            } else {
                                return spot;
                            }
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void close() {
                        spot = endFinal;
                        first = false;
                        end = true;
                    }
                };

            }

        }
    }

    public static String toString(final FDate date) {
        if (date == null) {
            return null;
        } else {
            return date.toString();
        }
    }

    public static String toString(final FDate date, final FTimeZone timeZone) {
        if (date == null) {
            return null;
        }
        return date.toString(timeZone);
    }

    public static String toString(final FDate date, final String format) {
        if (date == null) {
            return null;
        }
        return date.toString(format);
    }

    public static String toString(final FDate date, final String format, final FTimeZone timeZone) {
        if (date == null) {
            return null;
        }
        return date.toString(format, timeZone);
    }

    public static FDate min(final FDate... dates) {
        FDate minDate = null;
        for (final FDate date : dates) {
            minDate = min(minDate, date);
        }
        return minDate;
    }

    public static FDate min(final Iterable<FDate> dates) {
        FDate minDate = null;
        for (final FDate date : dates) {
            minDate = min(minDate, date);
        }
        return minDate;
    }

    public static FDate min(final FDate date1, final FDate date2) {
        if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        }

        return minNotNullSafe(date1, date2);
    }

    public static FDate minNotNullSafe(final FDate date1, final FDate date2) {
        if (date1.isBeforeNotNullSafe(date2)) {
            return date1;
        } else {
            return date2;
        }
    }

    public static FDate max(final Iterable<FDate> dates) {
        FDate maxDate = null;
        for (final FDate date : dates) {
            maxDate = max(maxDate, date);
        }
        return maxDate;
    }

    public static FDate max(final FDate... dates) {
        FDate maxDate = null;
        for (final FDate date : dates) {
            maxDate = max(maxDate, date);
        }
        return maxDate;
    }

    public static FDate max(final FDate date1, final FDate date2) {
        if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        }

        return maxNotNullSafe(date1, date2);
    }

    public static FDate maxNotNullSafe(final FDate date1, final FDate date2) {
        if (date1.isAfterNotNullSafe(date2)) {
            return date1;
        } else {
            return date2;
        }
    }

    public static FDate between(final FDate value, final FDate min, final FDate max) {
        return max(min(value, max), min);
    }

    public static boolean isBetween(final FDate value, final FDate min, final FDate max) {
        final boolean outside = value.isBefore(min) || value.isAfter(max);
        return !outside;
    }

    public static boolean isSameYear(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameYear(date1.millisValue(), date2.millisValue());
    }

    public static boolean isSameYear(final FDate date1, final FDate date2, final FTimeZone timeZone) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameYear(date1.millisValue(), date2.millisValue(), timeZone);
    }

    public static boolean isSameMonth(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameMonth(date1.millisValue(), date2.millisValue());
    }

    public static boolean isSameMonth(final FDate date1, final FDate date2, final FTimeZone timeZone) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameMonth(date1.millisValue(), date2.millisValue(), timeZone);
    }

    public static boolean isSameWeek(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameWeek(date1.millisValue(), date2.millisValue());
    }

    public static boolean isSameWeek(final FDate date1, final FDate date2, final FTimeZone timeZone) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameWeek(date1.millisValue(), date2.millisValue(), timeZone);
    }

    public static boolean isSameWeekPart(final FDate date1, final FDate date2, final FWeekday statOfWeekPart,
            final FWeekday endOfWeekPart, final FTimeZone timeZone) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameWeekPart(date1.millisValue(), date2.millisValue(), statOfWeekPart, endOfWeekPart,
                timeZone);
    }

    public static boolean isSameWeekPart(final FDate date1, final FDate date2, final FWeekday statOfWeekPart,
            final FWeekday endOfWeekPart) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameWeekPart(date1.millisValue(), date2.millisValue(), statOfWeekPart, endOfWeekPart);
    }

    public static boolean isWeekdayBetween(final FDate date1, final FDate date2, final FWeekday weekday,
            final FTimeZone timeZone) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isWeekdayBetween(date1.millisValue(), date2.millisValue(), weekday, timeZone);
    }

    public static boolean isWeekdayBetween(final FDate date1, final FDate date2, final FWeekday weekday) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isWeekdayBetween(date1.millisValue(), date2.millisValue(), weekday);
    }

    public static boolean isSameDay(final FDate date1, final FDate date2, final FTimeZone timeZone) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameDay(date1.millisValue(), date2.millisValue(), timeZone);
    }

    public static boolean isSameDay(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameDay(date1.millisValue(), date2.millisValue());
    }

    public static boolean isSameHour(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameHour(date1.millisValue(), date2.millisValue());
    }

    public static boolean isSameMinute(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameMinute(date1.millisValue(), date2.millisValue());
    }

    public static boolean isSameSecond(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameSecond(date1.millisValue(), date2.millisValue());
    }

    public static boolean isSameMillisecond(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        } else {
            return date1.millisValue() == date2.millisValue();
        }
    }

    public static boolean isSameTruncated(final FDate date1, final FDate date2, final FDateField field) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameTruncated(date1.millisValue(), date2.millisValue(), field);
    }

    public static boolean isSamePeriod(final FDate date1, final FDate date2, final FTimeUnit period,
            final FTimeZone timeZone) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSamePeriod(date1.millisValue(), date2.millisValue(), period, timeZone);
    }

    public static boolean isSamePeriod(final FDate date1, final FDate date2, final FTimeUnit period) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSamePeriod(date1.millisValue(), date2.millisValue(), period);
    }

    public static boolean isSameJulianPeriod(final FDate date1, final FDate date2, final FTimeUnit period,
            final FTimeZone timeZone) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameJulianPeriod(date1.millisValue(), date2.millisValue(), period, timeZone);
    }

    public static boolean isSameJulianPeriod(final FDate date1, final FDate date2, final FTimeUnit period) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameJulianPeriod(date1.millisValue(), date2.millisValue(), period);
    }

    public static boolean isSameJulianDay(final FDate date1, final FDate date2, final FTimeZone timeZone) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameJulianDay(date1.millisValue(), date2.millisValue(), timeZone);
    }

    /**
     * Fast but unprecise variation of isSameDay(). Does not count in daylight saving time. Though does not matter when
     * working with UTC.
     */
    public static boolean isSameJulianDay(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameJulianDay(date1.millisValue(), date2.millisValue());
    }

    /**
     * Fast but unprecise variation of isSameHour(). Does not count in daylight saving time. Though does not matter when
     * working with UTC.
     */
    public static boolean isSameJulianHour(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameJulianHour(date1.millisValue(), date2.millisValue());
    }

    /**
     * Fast but unprecise variation of isSameMinute(). Does not count in daylight saving time. Though does not matter
     * when working with UTC.
     */
    public static boolean isSameJulianMinute(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameJulianMinute(date1.millisValue(), date2.millisValue());
    }

    /**
     * Fast but unprecise variation of isSameSecond(). Does not count in daylight saving time. Though does not matter
     * when working with UTC.
     */
    public static boolean isSameJulianSecond(final FDate date1, final FDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return FDatesMillis.isSameJulianSecond(date1.millisValue(), date2.millisValue());
    }

    public static Date toDate(final FDate date) {
        if (date != null) {
            return date.dateValue();
        } else {
            return null;
        }
    }

    public static FDate avg(final FDate first, final FDate second) {
        return new FDate((first.millisValue() + second.millisValue()) / 2);
    }

    public static FDate avg(final FDate... values) {
        double sum = 0;
        for (final FDate value : values) {
            sum += value.millisValue();
        }
        final double avg = sum / values.length;
        return new FDate((long) avg);
    }

    public static FDate avg(final Collection<FDate> values) {
        double sum = 0;
        for (final FDate value : values) {
            sum += value.millisValue();
        }
        final double avg = sum / values.size();
        return new FDate((long) avg);
    }

    public static void putFDate(final ByteBuffer buffer, final FDate time) {
        if (time == null) {
            buffer.putLong(Long.MIN_VALUE);
        } else {
            buffer.putLong(time.millisValue());
        }
    }

    public static FDate extractFDate(final ByteBuffer buffer, final int index) {
        final long time = buffer.getLong(index);
        return extractFDate(time);
    }

    public static FDate extractFDate(final ByteBuffer buffer) {
        final long time = buffer.getLong();
        return extractFDate(time);
    }

    public static FDate extractFDate(final long time) {
        if (time == Long.MIN_VALUE) {
            return null;
        } else {
            return new FDate(time);
        }
    }

    public static int bisect(final FDate[] keys, final FDate skippingKeysAbove) {
        int lo = 0;
        int hi = keys.length;
        while (lo < hi) {
            // same as (low+high)/2
            final int mid = (lo + hi) >>> 1;
            //if (x < list.get(mid)) {
            final FDate midKey = keys[mid];
            final int compareTo = midKey.compareToNotNullSafe(skippingKeysAbove);
            switch (compareTo) {
            case MISSING_INDEX:
                lo = mid + 1;
                break;
            case 0:
                return mid;
            case 1:
                hi = mid;
                break;
            default:
                throw UnknownArgumentException.newInstance(Integer.class, compareTo);
            }
        }
        if (lo <= 0) {
            return 0;
        }
        if (lo >= keys.length) {
            lo = lo - 1;
        }
        final FDate loTime = keys[lo];
        if (loTime.isAfterNotNullSafe(skippingKeysAbove)) {
            final int index = lo - 1;
            return index;
        } else {
            return lo;
        }
    }

    public static int[] mapIndexes(final FDate[] fromKeys, final FDate[] toKeys) {
        final int[] mappingFromTo = new int[fromKeys.length];
        int toKeyIndex = MISSING_INDEX;
        for (int fromKeyIndex = 0; fromKeyIndex < fromKeys.length; fromKeyIndex++) {
            final FDate fromKey = fromKeys[fromKeyIndex];
            while (true) {
                final int nextToKeyIndex = toKeyIndex + 1;
                if (nextToKeyIndex >= toKeys.length) {
                    break;
                }
                final FDate nextToKey = toKeys[nextToKeyIndex];
                if (nextToKey.isBeforeOrEqualTo(fromKey)) {
                    toKeyIndex = nextToKeyIndex;
                } else {
                    break;
                }
            }
            mappingFromTo[fromKeyIndex] = toKeyIndex;
        }
        return mappingFromTo;
    }

    public static FDate nullToNow(final FDate time) {
        if (time == null) {
            return new FDate();
        } else {
            return time;
        }
    }

}
