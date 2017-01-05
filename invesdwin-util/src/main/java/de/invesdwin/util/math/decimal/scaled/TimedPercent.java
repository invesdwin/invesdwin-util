package de.invesdwin.util.math.decimal.scaled;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.TimeRange;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class TimedPercent extends Percent {

    public static final TimedPercent INVALID_ROW = new TimedPercent(new TimeRange(null, null),
            new Percent(new Decimal(Double.MIN_VALUE), PercentScale.RATE));

    private final TimeRange timeRange;

    public TimedPercent(final FDate time, final FDate endTime, final Percent percent) {
        this(new TimeRange(time, endTime), percent);
    }

    public TimedPercent(final TimeRange timeRange, final Percent percent) {
        super(percent);
        Assertions.checkNotNull(timeRange);
        this.timeRange = timeRange;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public static List<TimeRange> extractTimeRanges(final Iterable<TimedPercent> values) {
        final List<TimeRange> timeRanges = new ArrayList<TimeRange>();
        for (final TimedPercent pr : values) {
            timeRanges.add(pr.getTimeRange());
        }
        return timeRanges;
    }

    public static TimedPercent extractValueWithMaxPeriod(final Iterable<TimedPercent> values) {
        TimedPercent maxPeriod = null;
        for (final TimedPercent pr : values) {
            if (maxPeriod == null
                    || maxPeriod.getTimeRange().getDuration().isLessThan(pr.getTimeRange().getDuration())) {
                maxPeriod = pr;
            }
        }
        return maxPeriod;
    }

    public static TimedPercent extractValueWithMinPeriod(final Iterable<TimedPercent> values) {
        TimedPercent minPeriod = null;
        for (final TimedPercent pr : values) {
            if (minPeriod == null
                    || minPeriod.getTimeRange().getDuration().isGreaterThan(pr.getTimeRange().getDuration())) {
                minPeriod = pr;
            }
        }
        return minPeriod;
    }

    public FDate getTime() {
        return getTimeRange().getFrom();
    }

    public FDate getEndTime() {
        return getTimeRange().getTo();
    }

}
