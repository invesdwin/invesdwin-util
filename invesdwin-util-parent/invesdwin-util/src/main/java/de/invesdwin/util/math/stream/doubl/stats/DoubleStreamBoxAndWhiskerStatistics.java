package de.invesdwin.util.math.stream.doubl.stats;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.stream.doubl.DoubleStreamAvg;
import de.invesdwin.util.math.stream.doubl.DoubleStreamMax;
import de.invesdwin.util.math.stream.doubl.DoubleStreamMedian;
import de.invesdwin.util.math.stream.doubl.DoubleStreamMin;
import de.invesdwin.util.math.stream.doubl.IDoubleStreamAlgorithm;

@Immutable
public class DoubleStreamBoxAndWhiskerStatistics implements IDoubleStreamAlgorithm, IBoxAndWhiskerStatistics {

    private final DoubleStreamMin min = new DoubleStreamMin();
    private final DoubleStreamAvg avg = new DoubleStreamAvg();
    private final DoubleStreamMedian median = new DoubleStreamMedian();
    private final DoubleStreamMax max = new DoubleStreamMax();

    @Override
    public double process(final double value) {
        min.process(value);
        avg.process(value);
        median.process(value);
        max.process(value);
        return Double.NaN;
    }

    @Override
    public double getMin() {
        return min.getMin();
    }

    @Override
    public double getAvg() {
        return avg.getAvg();
    }

    @Override
    public double getFirstQuartile() {
        return median.getQuantile(0.25);
    }

    @Override
    public double getMedian() {
        return median.getMedian();
    }

    @Override
    public double getThirdQuartile() {
        return median.getQuantile(0.75);
    }

    @Override
    public double getMax() {
        return max.getMax();
    }

}
