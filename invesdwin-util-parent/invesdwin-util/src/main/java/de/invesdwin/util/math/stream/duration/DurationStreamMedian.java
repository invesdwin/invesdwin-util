package de.invesdwin.util.math.stream.duration;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.doubl.DoubleStreamMedian;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class DurationStreamMedian implements IStreamAlgorithm<Duration, Double> {

    private final FTimeUnit precision;

    private final DoubleStreamMedian median = new DoubleStreamMedian();

    public DurationStreamMedian() {
        this(FTimeUnit.MILLISECONDS);
    }

    public DurationStreamMedian(final FTimeUnit precision) {
        this.precision = precision;
    }

    @Override
    public Double process(final Duration value) {
        final double doubleValue = value.doubleValue(precision);
        median.process(doubleValue);
        return Double.NaN;
    }

    public Duration getMedian() {
        final double m = median.getMedian();
        return new Duration((long) m, precision);
    }

    public Duration getQuantile(final double quantile) {
        final double q = median.getQuantile(quantile);
        return new Duration((long) q, precision);
    }

}
