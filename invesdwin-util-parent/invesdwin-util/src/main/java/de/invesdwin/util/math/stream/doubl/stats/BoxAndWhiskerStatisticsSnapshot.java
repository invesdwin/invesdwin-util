package de.invesdwin.util.math.stream.doubl.stats;

import javax.annotation.concurrent.Immutable;

@Immutable
public class BoxAndWhiskerStatisticsSnapshot implements IBoxAndWhiskerStatistics {

    public static final BoxAndWhiskerStatisticsSnapshot DUMMY = new BoxAndWhiskerStatisticsSnapshot(Double.NaN,
            Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);

    private final double max;
    private final double upperQuartile;
    private final double avg;
    private final double median;
    private final double lowerQuartile;
    private final double min;

    private BoxAndWhiskerStatisticsSnapshot(final IBoxAndWhiskerStatistics statistics) {
        this.max = statistics.getMax();
        this.upperQuartile = statistics.getUpperQuartile();
        this.avg = statistics.getAvg();
        this.median = statistics.getMedian();
        this.lowerQuartile = statistics.getLowerQuartile();
        this.min = statistics.getMin();
    }

    public BoxAndWhiskerStatisticsSnapshot(final double min, final double max, final double avg,
            final double firstQuartile, final double median, final double thirdQuartile) {
        this.max = max;
        this.upperQuartile = thirdQuartile;
        this.avg = avg;
        this.median = median;
        this.lowerQuartile = firstQuartile;
        this.min = min;
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getUpperQuartile() {
        return upperQuartile;
    }

    @Override
    public double getAvg() {
        return avg;
    }

    @Override
    public double getMedian() {
        return median;
    }

    @Override
    public double getLowerQuartile() {
        return lowerQuartile;
    }

    @Override
    public double getMin() {
        return min;
    }

    public static BoxAndWhiskerStatisticsSnapshot snapshot(final IBoxAndWhiskerStatistics statistics) {
        if (statistics instanceof BoxAndWhiskerStatisticsSnapshot) {
            return (BoxAndWhiskerStatisticsSnapshot) statistics;
        } else {
            return new BoxAndWhiskerStatisticsSnapshot(statistics);
        }
    }

}
