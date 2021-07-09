package de.invesdwin.util.math.stream.duration;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class DurationStreamAvg implements IStreamAlgorithm<Duration, Double> {

    private final FTimeUnit precision;
    private long count = 0;
    private double avg = 0D; // our online mean estimate

    public DurationStreamAvg() {
        this(FTimeUnit.MILLISECONDS);
    }

    public DurationStreamAvg(final FTimeUnit precision) {
        this.precision = precision;
    }

    @Override
    public Double process(final Duration value) {
        count++;
        final double doubleValue = value.doubleValue(precision);
        final double delta = doubleValue - avg;
        avg += delta / count;
        return delta;
    }

    public Duration getAvg() {
        return new Duration((long) avg, precision);
    }

    public long getCount() {
        return count;
    }

}
