package de.invesdwin.util.time.fdate.millis;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDateField;
import de.invesdwin.util.time.fdate.FDates;
import de.invesdwin.util.time.fdate.FTimeUnit;
import de.invesdwin.util.time.fdate.FWeekday;
import de.invesdwin.util.time.fdate.ftimezone.FTimeZone;
import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;

@ThreadSafe
public final class FDatesMillis {

    private FDatesMillis() {
    }

    public static LongIterable iterable(final long start, final long end, final Duration increment) {
        return new FDateMillisIterable(start, end, increment.getTimeUnit(), increment.intValue());
    }

    public static LongIterable iterable(final long start, final long end, final FTimeUnit timeUnit,
            final int incrementAmount) {
        return new FDateMillisIterable(start, end, timeUnit, incrementAmount);
    }

    static class FDateMillisIterable implements LongIterable {
        private final Long startFinal;
        private final Long endFinal;
        private final FTimeUnit timeUnit;
        private final int incrementAmount;

        FDateMillisIterable(final Long startFinal, final Long endFinal, final FTimeUnit timeUnit,
                final int incrementAmount) {
            this.startFinal = startFinal;
            this.endFinal = endFinal;
            this.timeUnit = timeUnit;
            this.incrementAmount = incrementAmount;
            if (incrementAmount == 0) {
                throw new IllegalArgumentException("incrementAmount must not be 0");
            }
            if (startFinal < endFinal && incrementAmount < 0) {
                throw new IllegalArgumentException("When iterating forward [" + startFinal + " -> " + endFinal
                        + "], incrementAmount [" + incrementAmount + "] needs to be positive.");
            } else if (startFinal > endFinal && incrementAmount > 0) {
                throw new IllegalArgumentException("When iterating backward [" + startFinal + " -> " + endFinal
                        + "], incrementAmount [" + incrementAmount + "] needs to be negative.");
            }
        }

        @Override
        public LongIterator iterator() {
            if (incrementAmount > 0) {
                return new LongIterator() {

                    private long spot = startFinal;
                    private boolean first = true;
                    private boolean end = false;

                    @Override
                    public boolean hasNext() {
                        return first || spot < endFinal;
                    }

                    @Override
                    public long nextLong() {
                        if (first) {
                            first = false;
                            return spot;
                        } else {
                            if (spot > endFinal || end) {
                                throw new FastNoSuchElementException("FDateIterable: incrementing next reached end");
                            }
                            spot = FDateMillis.add(spot, timeUnit, incrementAmount);
                            if (spot >= endFinal) {
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

                };
            } else {
                //reverse
                return new LongIterator() {

                    private boolean first = true;
                    private long spot = startFinal;
                    private boolean end = false;

                    @Override
                    public boolean hasNext() {
                        return first || spot > endFinal;
                    }

                    @Override
                    public long nextLong() {
                        if (first) {
                            first = false;
                            return spot;
                        } else {
                            if (spot < endFinal || end) {
                                throw new FastNoSuchElementException("FDateIterable: decrementing next reached end");
                            }
                            spot = FDateMillis.add(spot, timeUnit, incrementAmount);
                            if (spot <= endFinal) {
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

                };

            }

        }
    }

    public static boolean isSameYear(final long date1, final long date2) {
        return isSameTruncated(date1, date2, FDateField.Year);
    }

    public static boolean isSameYear(final long date1, final long date2, final FTimeZone timeZone) {
        final long offset = FDateMillis.getTimeZoneOffsetMilliseconds(date1, timeZone);
        return isSameYear(FDateMillis.applyTimeZoneOffset(date1, offset),
                FDateMillis.applyTimeZoneOffset(date2, offset));
    }

    public static boolean isSameMonth(final long date1, final long date2) {
        return isSameTruncated(date1, date2, FDateField.Month);
    }

    public static boolean isSameMonth(final long date1, final long date2, final FTimeZone timeZone) {
        final long offset = FDateMillis.getTimeZoneOffsetMilliseconds(date1, timeZone);
        return isSameMonth(FDateMillis.applyTimeZoneOffset(date1, offset),
                FDateMillis.applyTimeZoneOffset(date2, offset));
    }

    public static boolean isSameWeek(final long date1, final long date2) {
        return isSameWeekPart(date1, date2, FWeekday.Monday, FWeekday.Sunday);
    }

    public static boolean isSameWeek(final long date1, final long date2, final FTimeZone timeZone) {
        final long offset = FDateMillis.getTimeZoneOffsetMilliseconds(date1, timeZone);
        return isSameWeek(FDateMillis.applyTimeZoneOffset(date1, offset),
                FDateMillis.applyTimeZoneOffset(date2, offset));
    }

    public static boolean isSameWeekPart(final long date1, final long date2, final FWeekday statOfWeekPart,
            final FWeekday endOfWeekPart, final FTimeZone timeZone) {
        final long offset = FDateMillis.getTimeZoneOffsetMilliseconds(date1, timeZone);
        return isSameWeekPart(FDateMillis.applyTimeZoneOffset(date1, offset),
                FDateMillis.applyTimeZoneOffset(date2, offset), statOfWeekPart, endOfWeekPart);
    }

    public static boolean isSameWeekPart(final long date1, final long date2, final FWeekday statOfWeekPart,
            final FWeekday endOfWeekPart) {
        final long startOfWeek = FDateMillis.setFWeekday(FDateMillis.withoutTime(date1), statOfWeekPart);
        long endOfWeek = FDateMillis.addMilliseconds(
                FDateMillis.addDays(FDateMillis.setFWeekday(FDateMillis.withoutTime(date1), endOfWeekPart), 1), -1);
        if (startOfWeek >= endOfWeek) {
            endOfWeek = FDateMillis.addWeeks(endOfWeek, 1);
        }
        return Longs.isBetween(date2, startOfWeek, endOfWeek);
    }

    public static boolean isWeekdayBetween(final long date1, final long date2, final FWeekday weekday,
            final FTimeZone timeZone) {
        return isWeekdayBetween(FDateMillis.applyTimeZoneOffset(date1, timeZone),
                FDateMillis.applyTimeZoneOffset(date2, timeZone), weekday);
    }

    public static boolean isWeekdayBetween(final long date1, final long date2, final FWeekday weekday) {
        final long from = FDateMillis.withoutTime(date1);
        final long to = FDateMillis.withoutTime(date2);
        if (to < from) {
            return false;
        }
        final LongIterator iterator = iterable(from, to, FTimeUnit.DAYS, 1).iterator();
        try {
            while (true) {
                final long day = iterator.nextLong();
                if (FDateMillis.getFWeekday(day) == weekday) {
                    return true;
                }
            }
        } catch (final NoSuchElementException e) {
            //end reached
        }
        return false;
    }

    public static boolean isSameDay(final long date1, final long date2, final FTimeZone timeZone) {
        final long offset = FDateMillis.getTimeZoneOffsetMilliseconds(date1, timeZone);
        return isSameDay(FDateMillis.applyTimeZoneOffset(date1, offset),
                FDateMillis.applyTimeZoneOffset(date2, offset));
    }

    public static boolean isSameDay(final long date1, final long date2) {
        return isSameTruncated(date1, date2, FDateField.Day);
    }

    public static boolean isSameHour(final long date1, final long date2) {
        return isSameTruncated(date1, date2, FDateField.Hour);
    }

    public static boolean isSameMinute(final long date1, final long date2) {
        return isSameTruncated(date1, date2, FDateField.Minute);
    }

    public static boolean isSameSecond(final long date1, final long date2) {
        return isSameTruncated(date1, date2, FDateField.Second);
    }

    public static boolean isSameMillisecond(final long date1, final long date2) {
        return date1 == date2;
    }

    public static boolean isSameTruncated(final long date1, final long date2, final FDateField field) {
        return date1 == date2 || FDateMillis.truncate(date1, field) == FDateMillis.truncate(date2, field);
    }

    public static boolean isSamePeriod(final long date1, final long date2, final FTimeUnit period,
            final FTimeZone timeZone) {
        final long offset = FDateMillis.getTimeZoneOffsetMilliseconds(date1, timeZone);
        return isSamePeriod(FDateMillis.applyTimeZoneOffset(date1, offset),
                FDateMillis.applyTimeZoneOffset(date2, offset), period);
    }

    public static boolean isSamePeriod(final long date1, final long date2, final FTimeUnit period) {
        switch (period) {
        case MILLISECONDS:
            return isSameMillisecond(date1, date2);
        case SECONDS:
            return isSameSecond(date1, date2);
        case MINUTES:
            return isSameMinute(date1, date2);
        case HOURS:
            return isSameHour(date1, date2);
        case DAYS:
            return isSameDay(date1, date2);
        case WEEKS:
            return isSameWeek(date1, date2);
        case MONTHS:
            return isSameMonth(date1, date2);
        case YEARS:
            return isSameYear(date1, date2);
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, period);
        }
    }

    public static boolean isSameJulianPeriod(final long date1, final long date2, final FTimeUnit period,
            final FTimeZone timeZone) {
        final long offset = FDateMillis.getTimeZoneOffsetMilliseconds(date1, timeZone);
        return isSameJulianPeriod(FDateMillis.applyTimeZoneOffset(date1, offset),
                FDateMillis.applyTimeZoneOffset(date2, offset), period);
    }

    public static boolean isSameJulianPeriod(final long date1, final long date2, final FTimeUnit period) {
        switch (period) {
        case MILLISECONDS:
            return isSameMillisecond(date1, date2);
        case SECONDS:
            return isSameJulianSecond(date1, date2);
        case MINUTES:
            return isSameJulianMinute(date1, date2);
        case HOURS:
            return isSameJulianHour(date1, date2);
        case DAYS:
            return isSameJulianDay(date1, date2);
        case WEEKS:
            return isSameWeek(date1, date2);
        case MONTHS:
            return isSameMonth(date1, date2);
        case YEARS:
            return isSameYear(date1, date2);
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, period);
        }
    }

    public static boolean isSameJulianDay(final long date1, final long date2, final FTimeZone timeZone) {
        final long offset = FDateMillis.getTimeZoneOffsetMilliseconds(date1, timeZone);
        return isSameJulianDay(FDateMillis.applyTimeZoneOffset(date1, offset),
                FDateMillis.applyTimeZoneOffset(date2, offset));
    }

    /**
     * Fast but unprecise variation of isSameDay(). Does not count in daylight saving time. Though does not matter when
     * working with UTC.
     */
    public static boolean isSameJulianDay(final long date1, final long date2) {
        // Strip out the time part of each date.
        final long julianDayNumber1 = date1 / FDates.MILLISECONDS_IN_DAY;
        final long julianDayNumber2 = date2 / FDates.MILLISECONDS_IN_DAY;

        // If they now are equal then it is the same day.
        return julianDayNumber1 == julianDayNumber2;
    }

    /**
     * Fast but unprecise variation of isSameHour(). Does not count in daylight saving time. Though does not matter when
     * working with UTC.
     */
    public static boolean isSameJulianHour(final long date1, final long date2) {
        // Strip out the time part of each date.
        final long julianHourNumber1 = date1 / FDates.MILLISECONDS_IN_HOUR;
        final long julianHourNumber2 = date2 / FDates.MILLISECONDS_IN_HOUR;

        // If they now are equal then it is the same day.
        return julianHourNumber1 == julianHourNumber2;
    }

    /**
     * Fast but unprecise variation of isSameMinute(). Does not count in daylight saving time. Though does not matter
     * when working with UTC.
     */
    public static boolean isSameJulianMinute(final long date1, final long date2) {
        // Strip out the time part of each date.
        final long julianMinuteNumber1 = date1 / FDates.MILLISECONDS_IN_MINUTE;
        final long julianMinuteNumber2 = date2 / FDates.MILLISECONDS_IN_MINUTE;

        // If they now are equal then it is the same day.
        return julianMinuteNumber1 == julianMinuteNumber2;
    }

    /**
     * Fast but unprecise variation of isSameSecond(). Does not count in daylight saving time. Though does not matter
     * when working with UTC.
     */
    public static boolean isSameJulianSecond(final long date1, final long date2) {
        // Strip out the time part of each date.
        final long julianSecondNumber1 = date1 / FDates.MILLISECONDS_IN_SECOND;
        final long julianSecondNumber2 = date2 / FDates.MILLISECONDS_IN_SECOND;

        // If they now are equal then it is the same day.
        return julianSecondNumber1 == julianSecondNumber2;
    }

    public static int bisect(final long[] keys, final long skippingKeysAbove) {
        int lo = 0;
        int hi = keys.length;
        while (lo < hi) {
            // same as (low+high)/2
            final int mid = (lo + hi) >>> 1;
            //if (x < list.get(mid)) {
            final long midKey = keys[mid];
            final int compareTo = Long.compare(midKey, skippingKeysAbove);
            switch (compareTo) {
            case FDates.MISSING_INDEX:
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
        final long loTime = keys[lo];
        if (loTime > skippingKeysAbove) {
            final int index = lo - 1;
            return index;
        } else {
            return lo;
        }
    }

    public static int[] mapIndexes(final long[] fromKeys, final long[] toKeys) {
        final int[] mappingFromTo = new int[fromKeys.length];
        int toKeyIndex = FDates.MISSING_INDEX;
        for (int fromKeyIndex = 0; fromKeyIndex < fromKeys.length; fromKeyIndex++) {
            final long fromKey = fromKeys[fromKeyIndex];
            while (true) {
                final int nextToKeyIndex = toKeyIndex + 1;
                if (nextToKeyIndex >= toKeys.length) {
                    break;
                }
                final long nextToKey = toKeys[nextToKeyIndex];
                if (nextToKey <= fromKey) {
                    toKeyIndex = nextToKeyIndex;
                } else {
                    break;
                }
            }
            mappingFromTo[fromKeyIndex] = toKeyIndex;
        }
        return mappingFromTo;
    }

}
