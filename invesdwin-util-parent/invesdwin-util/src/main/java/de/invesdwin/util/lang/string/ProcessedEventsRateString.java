package de.invesdwin.util.lang.string;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.FTimeUnitFractional;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public class ProcessedEventsRateString {

    private final long countEvents;
    private final Duration duration;
    private int decimalPlaces = 2;
    private FTimeUnit fixedTimeUnit;

    public ProcessedEventsRateString(final long countEvents, final Duration duration) {
        this.countEvents = countEvents;
        this.duration = duration;
    }

    public ProcessedEventsRateString(final long countEvents, final Instant startInstant, final Instant endInstant) {
        this.countEvents = countEvents;
        this.duration = new Duration(startInstant, endInstant);
    }

    public ProcessedEventsRateString setDecimalPlaces(final int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
        return this;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public ProcessedEventsRateString setFixedTimeUnit(final FTimeUnit fixedTimeUnit) {
        this.fixedTimeUnit = fixedTimeUnit;
        return this;
    }

    public FTimeUnit getFixedTimeUnit() {
        return fixedTimeUnit;
    }

    public FTimeUnit getTimeUnit() {
        if (fixedTimeUnit != null) {
            return fixedTimeUnit;
        }
        return getDefaultTimeUnit();
    }

    public FTimeUnit getDefaultTimeUnit() {
        if (countEvents == 0) {
            return FTimeUnit.SECONDS;
        }
        final double milliseconds = duration.doubleValue(FTimeUnit.MILLISECONDS);
        if (milliseconds <= 0D) {
            return FTimeUnit.SECONDS;
        }
        final double ratePerMillisecond = countEvents / milliseconds;
        if (ratePerMillisecond < 10 && duration.isGreaterThan(Duration.ONE_SECOND)) {
            final double ratePerSecond = ratePerMillisecond * FTimeUnit.MILLISECONDS_IN_SECOND;
            if (ratePerSecond < 1) {
                final double ratePerMinute = ratePerSecond * FTimeUnit.SECONDS_IN_MINUTE;
                if (ratePerMinute < 1) {
                    return FTimeUnit.HOURS;
                } else {
                    return FTimeUnit.MINUTES;
                }
            } else {
                return FTimeUnit.SECONDS;
            }
        } else {
            if (ratePerMillisecond > 10_000) {
                return FTimeUnit.MICROSECONDS;
            } else {
                return FTimeUnit.MILLISECONDS;
            }
        }
    }

    public long getCountEvents() {
        return countEvents;
    }

    public Duration getDuration() {
        return duration;
    }

    public double getRate(final FTimeUnit timeUnit) {
        final FTimeUnitFractional fractional = timeUnit.asFractional();
        return getRate(fractional);
    }

    public double getRate(final FTimeUnitFractional timeUnit) {
        final double milliseconds = duration.doubleValue(FTimeUnit.MILLISECONDS);
        final double ratePerMillisecond = countEvents / milliseconds;
        return FTimeUnitFractional.MILLISECONDS.convert(ratePerMillisecond, timeUnit);
    }

    @Override
    public String toString() {
        final FTimeUnit timeUnit = getTimeUnit();
        final double rate = getRate(timeUnit);
        return rateToString(new Decimal(rate)) + "/" + timeUnit.getShortName();
    }

    protected String rateToString(final Decimal rate) {
        return rate.toStringBuilder().setDecimalDigits(decimalPlaces).toString();
    }

}
