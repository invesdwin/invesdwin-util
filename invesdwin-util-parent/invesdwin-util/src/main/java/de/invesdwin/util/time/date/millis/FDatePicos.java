package de.invesdwin.util.time.date.millis;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.time.date.FTimeUnit;

@Immutable
public final class FDatePicos {

    //microsecond_nanosecond_picosecond
    public static final int END_OF_DAY_PICOS = 999_999_999;

    private FDatePicos() {}

    public static int getPicosecond(final int picos) {
        return picos % FTimeUnit.PICOSECONDS_IN_NANOSECOND;
    }

    public static int getNanosecond(final int picos) {
        return (picos / FTimeUnit.PICOSECONDS_IN_NANOSECOND) % FTimeUnit.NANOSECONDS_IN_MICROSECOND;
    }

    public static int getMicrosecond(final int picos) {
        return picos / FTimeUnit.PICOSECONDS_IN_MICROSECOND % FTimeUnit.MICROSECONDS_IN_MILLISECOND;
    }

    public static int truncate(final int picos, final FTimeUnit timeUnit) {
        if (picos == 0) {
            return 0;
        }
        switch (timeUnit) {
        case MILLENIA:
            return 0;
        case CENTURIES:
            return 0;
        case DECADES:
            return 0;
        case YEARS:
            return 0;
        case MONTHS:
            return 0;
        case WEEKS:
            return 0;
        case DAYS:
            return 0;
        case HOURS:
            return 0;
        case MINUTES:
            return 0;
        case SECONDS:
            return 0;
        case MILLISECONDS:
            return 0;
        case MICROSECONDS:
            return truncateMicroseconds(picos);
        case NANOSECONDS:
            return truncateNanoseconds(picos);
        case PICOSECONDS:
            return picos;
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, timeUnit);
        }
    }

    public static int truncateNanoseconds(final int picos) {
        return picos / FTimeUnit.PICOSECONDS_IN_NANOSECOND * FTimeUnit.PICOSECONDS_IN_NANOSECOND;
    }

    public static int truncateMicroseconds(final int picos) {
        return picos / FTimeUnit.PICOSECONDS_IN_MICROSECOND * FTimeUnit.PICOSECONDS_IN_MICROSECOND;
    }

    public static long addMaybeOverflow(final int picos, final FTimeUnit field, final long amount) {
        if (amount == 0) {
            return picos;
        }
        switch (field) {
        case MILLENIA:
            return 0;
        case CENTURIES:
            return 0;
        case DECADES:
            return 0;
        case YEARS:
            return 0;
        case MONTHS:
            return 0;
        case WEEKS:
            return 0;
        case DAYS:
            return 0;
        case HOURS:
            return 0;
        case MINUTES:
            return 0;
        case SECONDS:
            return 0;
        case MILLISECONDS:
            return 0;
        case MICROSECONDS:
            return addMicrosecondsMaybeOverflow(picos, amount);
        case NANOSECONDS:
            return addNanosecondsMaybeOverflow(picos, amount);
        case PICOSECONDS:
            return addPicosecondsMaybeOverflow(picos, amount);
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, field);
        }
    }

    public static long addPicosecondsMaybeOverflow(final int picos, final long picoseconds) {
        return picos + picoseconds;
    }

    public static long addNanosecondsMaybeOverflow(final int picos, final long nanoseconds) {
        return picos + nanoseconds * FTimeUnit.PICOSECONDS_IN_NANOSECOND;
    }

    public static long addMicrosecondsMaybeOverflow(final int picos, final long microseconds) {
        return picos + microseconds * FTimeUnit.PICOSECONDS_IN_MICROSECOND;
    }

    public static long toMillisecondsOverflow(final long picosMaybeOverflow) {
        long millisOverflow = picosMaybeOverflow / FTimeUnit.PICOSECONDS_IN_MILLISECOND;
        if (picosMaybeOverflow < 0 && picosMaybeOverflow % FTimeUnit.PICOSECONDS_IN_MILLISECOND != 0) {
            millisOverflow--;
        }
        return millisOverflow;
    }

    public static int toPicosWithoutOverflow(final long picosMaybeOverflow) {
        final int picosWithoutOverflow = (int) (picosMaybeOverflow % FTimeUnit.PICOSECONDS_IN_MILLISECOND);
        if (picosWithoutOverflow < 0) {
            return picosWithoutOverflow + FTimeUnit.PICOSECONDS_IN_MILLISECOND;
        }
        return picosWithoutOverflow;
    }

    public static boolean isValidPicos(final int picos) {
        return picos >= 0 && picos <= END_OF_DAY_PICOS;
    }

    public static boolean isSameMicrosecond(final int picos1, final int picos2) {
        return truncateMicroseconds(picos1) == truncateMicroseconds(picos2);
    }

    public static boolean isSameNanosecond(final int picos1, final int picos2) {
        return truncateNanoseconds(picos1) == truncateNanoseconds(picos2);
    }

    public static boolean isSamePicosecond(final int picos1, final int picos2) {
        return picos1 == picos2;
    }

    public static boolean isSameTruncated(final int picos1, final int picos2, final FTimeUnit field) {
        return picos1 == picos2 || truncate(picos1, field) == truncate(picos2, field);
    }
}