package de.invesdwin.util.math.stream.duration;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public class DurationStreamMin implements IStreamAlgorithm<Duration, Double> {

    private final FTimeUnit precision;
    private double min = Double.MAX_VALUE; // our online mean estimate

    public DurationStreamMin() {
        this(FTimeUnit.MILLISECONDS);
    }

    public DurationStreamMin(final FTimeUnit precision) {
        this.precision = precision;
    }

    @Override
    public Double process(final Duration value) {
        final double doubleValue = value.doubleValue(precision);
        min = Doubles.min(min, doubleValue);
        return min;
    }

    public Duration getMin() {
        if (min == Double.MAX_VALUE) {
            return null;
        } else {
            return new Duration((long) min, precision);
        }
    }

}
