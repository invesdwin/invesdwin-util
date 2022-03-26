package de.invesdwin.util.lang;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public class ProcessedEventsRateString {

    private final long countEvents;
    private final Duration duration;

    public ProcessedEventsRateString(final long countEvents, final Duration duration) {
        this.countEvents = countEvents;
        this.duration = duration;
    }

    public ProcessedEventsRateString(final long countEvents, final Instant startInstant, final Instant endInstant) {
        this.countEvents = countEvents;
        this.duration = new Duration(startInstant, endInstant);
    }

    @Override
    public String toString() {
        if (countEvents == 0) {
            return "0/s";
        }
        final double milliseconds = duration.doubleValue(FTimeUnit.MILLISECONDS);
        if (milliseconds <= 0D) {
            return "0/s";
        }
        final double ratePerMillisecond = countEvents / milliseconds;
        if (ratePerMillisecond < 10 && duration.isGreaterThan(Duration.ONE_SECOND)) {
            final double seconds = duration.doubleValue(FTimeUnit.SECONDS);
            final double ratePerSecond = countEvents / seconds;
            final double ratePerSecondPrecise = ratePerMillisecond * FTimeUnit.MILLISECONDS_IN_SECOND;
            if (ratePerSecond < 1) {
                final double ratePerMinute = ratePerSecond * FTimeUnit.SECONDS_IN_MINUTE;
                final double ratePerMinutePrecise = ratePerSecondPrecise * FTimeUnit.SECONDS_IN_MINUTE;
                if (ratePerMinute < 1) {
                    final double ratePerHourPrecise = ratePerMinutePrecise * FTimeUnit.MINUTES_IN_HOUR;
                    return new Decimal(ratePerHourPrecise).round(2) + "/h";
                } else {
                    return new Decimal(ratePerMinutePrecise).round(2) + "/m";
                }
            } else {
                return new Decimal(ratePerSecondPrecise).round(2) + "/s";
            }
        } else {
            if (ratePerMillisecond > 10_000) {
                final double ratePerMicrosecond = ratePerMillisecond / FTimeUnit.MICROSECONDS_IN_MILLISECOND;
                return new Decimal(ratePerMicrosecond).round(2) + "/µs";
            } else {
                return new Decimal(ratePerMillisecond).round(2) + "/ms";
            }
        }
    }

}
