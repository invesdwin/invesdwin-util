package de.invesdwin.util.lang;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

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
            return new Decimal(ratePerSecond).round(2) + "/s";
        } else {
            if (ratePerMillisecond > 10_000) {
                return new Decimal(ratePerMillisecond / 1000).round(2) + "/µs";
            } else {
                return new Decimal(ratePerMillisecond).round(2) + "/ms";
            }
        }
    }

}
