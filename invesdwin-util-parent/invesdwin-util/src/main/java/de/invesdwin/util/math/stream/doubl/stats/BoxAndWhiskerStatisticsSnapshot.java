package de.invesdwin.util.math.stream.doubl.stats;

import javax.annotation.concurrent.Immutable;

@Immutable
public class BoxAndWhiskerStatisticsSnapshot implements IBoxAndWhiskerStatistics {

    public static final BoxAndWhiskerStatisticsSnapshot DUMMY = new BoxAndWhiskerStatisticsSnapshot(Double.NaN,
            Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);

    private final double min;
    private final double avg;
    private final double firstQuartile;
    private final double median;
    private final double thirdQuartile;
    private final double max;

    private BoxAndWhiskerStatisticsSnapshot(final IBoxAndWhiskerStatistics statistics) {
        this.min = statistics.getMin();
        this.max = statistics.getMax();
        this.avg = statistics.getAvg();
        this.firstQuartile = statistics.getFirstQuartile();
        this.median = statistics.getMedian();
        this.thirdQuartile = statistics.getThirdQuartile();
    }

    public BoxAndWhiskerStatisticsSnapshot(final double min, final double max, final double avg,
            final double firstQuartile, final double median, final double thirdQuartile) {
        this.min = min;
        this.max = max;
        this.avg = avg;
        this.firstQuartile = firstQuartile;
        this.median = median;
        this.thirdQuartile = thirdQuartile;
    }

    @Override
    public double getMin() {
        return min;
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getAvg() {
        return avg;
    }

    @Override
    public double getFirstQuartile() {
        return firstQuartile;
    }

    @Override
    public double getMedian() {
        return median;
    }

    @Override
    public double getThirdQuartile() {
        return thirdQuartile;
    }

    public static BoxAndWhiskerStatisticsSnapshot snapshot(final IBoxAndWhiskerStatistics statistics) {
        if (statistics instanceof BoxAndWhiskerStatisticsSnapshot) {
            return (BoxAndWhiskerStatisticsSnapshot) statistics;
        } else {
            return new BoxAndWhiskerStatisticsSnapshot(statistics);
        }
    }

}
