package de.invesdwin.util.math.stream.duration;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class DurationStreamMinMax implements IStreamAlgorithm<Duration, Double> {

    private final DurationStreamMin minDelegate = new DurationStreamMin();
    private final DurationStreamMax maxDelegate = new DurationStreamMax();

    @Override
    public Double process(final Duration value) {
        minDelegate.process(value);
        maxDelegate.process(value);
        return null;
    }

    public Duration getMin() {
        return minDelegate.getMin();
    }

    public Duration getMax() {
        return maxDelegate.getMax();
    }

}
