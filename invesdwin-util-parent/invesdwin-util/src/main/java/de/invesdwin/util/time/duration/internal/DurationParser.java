package de.invesdwin.util.time.duration.internal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public class DurationParser {

    private final String trimmedValue;
    private long years = 0;
    private long months = 0;
    private long weeks = 0;
    private long days = 0;
    private long hours = 0;
    private long minutes = 0;
    private long seconds = 0;
    private long milliseconds = 0;
    private long microseconds = 0;
    private long nanoseconds = 0;
    private int dotsCount = 0;

    public DurationParser(final String trimmedValue) {
        this.trimmedValue = trimmedValue;
    }

    public Duration parse() {
        if ("P0".equals(trimmedValue)) {
            return Duration.ZERO;
        }
        //P[JY][MM][WW][TD][T[hH][mM][s[.f]S]]
        final String beforeT;
        final String afterT;
        if (trimmedValue.contains("T")) {
            if (Strings.startsWith(trimmedValue, "PT")) {
                beforeT = null;
            } else {
                beforeT = Strings.substringBetween(trimmedValue, "P", "T");
            }
            afterT = Strings.substringAfter(trimmedValue, "T");
        } else {
            beforeT = Strings.substringAfter(trimmedValue, "P");
            afterT = null;
        }
        if (beforeT != null) {
            parseBeforeT(beforeT);
        }
        if (afterT != null) {
            parseAfterT(afterT);
        }
        final FTimeUnit smallestUnit = determineSmallestUnit();
        final long duration = calculateDuration(smallestUnit);
        return new Duration(duration, smallestUnit);
    }

    private long calculateDuration(final FTimeUnit smallestUnit) {
        long duration = 0;
        switch (smallestUnit) {
        case NANOSECONDS:
            duration += smallestUnit.convert(nanoseconds, FTimeUnit.NANOSECONDS);
        case MICROSECONDS:
            duration += smallestUnit.convert(microseconds, FTimeUnit.MICROSECONDS);
        case MILLISECONDS:
            duration += smallestUnit.convert(milliseconds, FTimeUnit.MILLISECONDS);
        case MINUTES:
            duration += smallestUnit.convert(seconds, FTimeUnit.SECONDS);
        case SECONDS:
            duration += smallestUnit.convert(minutes, FTimeUnit.MINUTES);
        case HOURS:
            duration += smallestUnit.convert(hours, FTimeUnit.HOURS);
        case DAYS:
            duration += smallestUnit.convert(days, FTimeUnit.DAYS);
        case WEEKS:
            duration += smallestUnit.convert(weeks, FTimeUnit.WEEKS);
        case MONTHS:
            duration += smallestUnit.convert(months, FTimeUnit.MONTHS);
        case YEARS:
            duration += smallestUnit.convert(years, FTimeUnit.YEARS);
            break;
        default:
            throw UnknownArgumentException.newInstance(FTimeUnit.class, smallestUnit);
        }
        return duration;
    }

    private void parseAfterT(final String afterT) {
        final StringBuilder numberStr = new StringBuilder();
        for (int i = 0; i < afterT.length(); i++) {
            final char curChar = afterT.charAt(i);
            switch (curChar) {
            case 'H':
                hours = Long.parseLong(numberStr.toString());
                numberStr.setLength(0);
                break;
            case 'M':
                minutes = Long.parseLong(numberStr.toString());
                numberStr.setLength(0);
                break;
            case '.':
                switch (dotsCount) {
                case 0:
                    seconds = Long.parseLong(numberStr.toString());
                    numberStr.setLength(0);
                    break;
                case 1:
                    milliseconds = Long.parseLong(numberStr.toString());
                    numberStr.setLength(0);
                    break;
                case 2:
                    microseconds = Long.parseLong(numberStr.toString());
                    numberStr.setLength(0);
                    break;
                default:
                    throw new IllegalStateException("More than 3 dots are invalid: " + trimmedValue);
                }
                dotsCount++;
                break;
            case 'S':
                switch (dotsCount) {
                case 0:
                    seconds = Long.parseLong(numberStr.toString());
                    numberStr.setLength(0);
                    break;
                case 1:
                    milliseconds = Long.parseLong(numberStr.toString());
                    numberStr.setLength(0);
                    break;
                case 2:
                    microseconds = Long.parseLong(numberStr.toString());
                    numberStr.setLength(0);
                    break;
                case 3:
                    nanoseconds = Long.parseLong(numberStr.toString());
                    numberStr.setLength(0);
                    break;
                default:
                    throw new IllegalStateException("More than 3 dots are invalid: " + trimmedValue);
                }
                break;
            default:
                numberStr.append(curChar);
            }
        }
    }

    private void parseBeforeT(final String beforeT) {
        final StringBuilder numberStr = new StringBuilder();
        for (int i = 0; i < beforeT.length(); i++) {
            final char curChar = beforeT.charAt(i);
            switch (curChar) {
            case 'Y':
                years = Long.parseLong(numberStr.toString());
                numberStr.setLength(0);
                break;
            case 'M':
                months = Long.parseLong(numberStr.toString());
                numberStr.setLength(0);
                break;
            case 'W':
                weeks = Long.parseLong(numberStr.toString());
                numberStr.setLength(0);
                break;
            case 'D':
                days = Long.parseLong(numberStr.toString());
                numberStr.setLength(0);
                break;
            default:
                numberStr.append(curChar);
            }
        }
    }

    private FTimeUnit determineSmallestUnit() {
        final FTimeUnit smallestUnit;
        if (nanoseconds > 0) {
            smallestUnit = FTimeUnit.NANOSECONDS;
        } else if (microseconds > 0) {
            smallestUnit = FTimeUnit.MICROSECONDS;
        } else if (milliseconds > 0) {
            smallestUnit = FTimeUnit.MILLISECONDS;
        } else if (seconds > 0) {
            smallestUnit = FTimeUnit.SECONDS;
        } else if (minutes > 0) {
            smallestUnit = FTimeUnit.MINUTES;
        } else if (hours > 0) {
            smallestUnit = FTimeUnit.HOURS;
        } else if (days > 0) {
            smallestUnit = FTimeUnit.DAYS;
        } else if (weeks > 0) {
            smallestUnit = FTimeUnit.WEEKS;
        } else if (months > 0) {
            smallestUnit = FTimeUnit.MONTHS;
        } else if (years > 0) {
            smallestUnit = FTimeUnit.YEARS;
        } else {
            throw new IllegalStateException(
                    "At least one timeunit with a non zero value expected when not prefixed with \"P0\": "
                            + trimmedValue);
        }
        return smallestUnit;
    }

}
