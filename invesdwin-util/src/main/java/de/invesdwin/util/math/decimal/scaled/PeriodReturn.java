package de.invesdwin.util.math.decimal.scaled;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.TimeRange;

@Immutable
public class PeriodReturn extends Percent {

    private final TimeRange timeRange;

    public PeriodReturn(final TimeRange timeRange, final Percent percent) {
        super(percent);
        this.timeRange = timeRange;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public static List<TimeRange> extractTimeRanges(final Iterable<PeriodReturn> values) {
        final List<TimeRange> timeRanges = new ArrayList<TimeRange>();
        for (final PeriodReturn pr : values) {
            timeRanges.add(pr.getTimeRange());
        }
        return timeRanges;
    }

    public static PeriodReturn extractValueWithMaxPeriod(final Iterable<PeriodReturn> values) {
        PeriodReturn maxPeriod = null;
        for (final PeriodReturn pr : values) {
            if (maxPeriod == null
                    || maxPeriod.getTimeRange().getDuration().isLessThan(pr.getTimeRange().getDuration())) {
                maxPeriod = pr;
            }
        }
        return maxPeriod;
    }

    public static PeriodReturn extractValueWithMinPeriod(final Iterable<PeriodReturn> values) {
        PeriodReturn minPeriod = null;
        for (final PeriodReturn pr : values) {
            if (minPeriod == null
                    || minPeriod.getTimeRange().getDuration().isGreaterThan(pr.getTimeRange().getDuration())) {
                minPeriod = pr;
            }
        }
        return minPeriod;
    }

}
