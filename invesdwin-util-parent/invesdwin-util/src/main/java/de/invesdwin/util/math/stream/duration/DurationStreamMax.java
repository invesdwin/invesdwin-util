package de.invesdwin.util.math.stream.duration;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public class DurationStreamMax implements IStreamAlgorithm<Duration, Double> {

    private final FTimeUnit precision;
    private double max = Doubles.MIN_VALUE; // our online mean estimate

    public DurationStreamMax() {
        this(FTimeUnit.MILLISECONDS);
    }

    public DurationStreamMax(final FTimeUnit precision) {
        this.precision = precision;
    }

    @Override
    public Double process(final Duration value) {
        final double doubleValue = value.doubleValue(precision);
        max = Doubles.max(max, doubleValue);
        return max;
    }

    public Duration getMax() {
        if (max == Doubles.MIN_VALUE) {
            return null;
        } else {
            return new Duration((long) max, precision);
        }
    }

}
