package de.invesdwin.util.math.stream.doubl.stats;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.stream.doubl.DoubleStreamAvg;
import de.invesdwin.util.math.stream.doubl.DoubleStreamMax;
import de.invesdwin.util.math.stream.doubl.DoubleStreamMedian;
import de.invesdwin.util.math.stream.doubl.DoubleStreamMin;
import de.invesdwin.util.math.stream.doubl.IDoubleStreamAlgorithm;

@Immutable
public class DoubleStreamBoxAndWhiskerStatistics implements IDoubleStreamAlgorithm, IBoxAndWhiskerStatistics {

    private final DoubleStreamMax max = new DoubleStreamMax();
    private final DoubleStreamAvg avg = new DoubleStreamAvg();
    private final DoubleStreamMedian median = new DoubleStreamMedian();
    private final DoubleStreamMin min = new DoubleStreamMin();

    @Override
    public double process(final double value) {
        if (!Doubles.isNaN(value)) {
            max.process(value);
            avg.process(value);
            median.process(value);
            min.process(value);
        }
        return Double.NaN;
    }

    @Override
    public double getMax() {
        return max.getMax();
    }

    @Override
    public double getUpperQuartile() {
        return median.getQuantile(0.75);
    }

    @Override
    public double getAvg() {
        return avg.getAvg();
    }

    @Override
    public double getMedian() {
        return median.getMedian();
    }

    @Override
    public double getLowerQuartile() {
        return median.getQuantile(0.25);
    }

    @Override
    public double getMin() {
        return min.getMin();
    }

}
