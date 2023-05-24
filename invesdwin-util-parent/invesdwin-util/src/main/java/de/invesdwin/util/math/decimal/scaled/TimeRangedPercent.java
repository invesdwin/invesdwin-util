package de.invesdwin.util.math.decimal.scaled;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.range.TimeRange;

@Immutable
public class TimeRangedPercent extends Percent {

    public static final TimeRangedPercent INVALID_ROW = new TimeRangedPercent(
            new TimeRange(FDates.MIN_DATE, FDates.MAX_DATE), new Percent(Doubles.MIN_VALUE, PercentScale.RATE));

    private final TimeRange timeRange;

    public TimeRangedPercent(final FDate time, final FDate endTime, final Percent percent) {
        this(new TimeRange(time, endTime), percent);
    }

    public TimeRangedPercent(final FDate time, final FDate endTime, final double value, final PercentScale scale) {
        this(new TimeRange(time, endTime), value, scale);
    }

    public TimeRangedPercent(final TimeRange timeRange, final Percent percent) {
        super(percent);
        Assertions.checkNotNull(timeRange);
        Assertions.checkNotNull(timeRange.getFrom());
        Assertions.checkNotNull(timeRange.getTo());
        this.timeRange = timeRange;
    }

    public TimeRangedPercent(final TimeRange timeRange, final double value, final PercentScale scale) {
        super(value, scale);
        Assertions.checkNotNull(timeRange);
        Assertions.checkNotNull(timeRange.getFrom());
        Assertions.checkNotNull(timeRange.getTo());
        this.timeRange = timeRange;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public static List<TimeRange> extractTimeRanges(final Iterable<TimeRangedPercent> values) {
        final List<TimeRange> timeRanges = new ArrayList<TimeRange>();
        for (final TimeRangedPercent pr : values) {
            timeRanges.add(pr.getTimeRange());
        }
        return timeRanges;
    }

    public static TimeRangedPercent extractValueWithMaxPeriod(final Iterable<TimeRangedPercent> values) {
        TimeRangedPercent maxPeriod = null;
        for (final TimeRangedPercent pr : values) {
            if (maxPeriod == null
                    || maxPeriod.getTimeRange().getDuration().isLessThan(pr.getTimeRange().getDuration())) {
                maxPeriod = pr;
            }
        }
        return maxPeriod;
    }

    public static TimeRangedPercent extractValueWithMinPeriod(final Iterable<TimeRangedPercent> values) {
        TimeRangedPercent minPeriod = null;
        for (final TimeRangedPercent pr : values) {
            if (minPeriod == null
                    || minPeriod.getTimeRange().getDuration().isGreaterThan(pr.getTimeRange().getDuration())) {
                minPeriod = pr;
            }
        }
        return minPeriod;
    }

    public FDate getStartTime() {
        return getTimeRange().getFrom();
    }

    public FDate getEndTime() {
        return getTimeRange().getTo();
    }

}
