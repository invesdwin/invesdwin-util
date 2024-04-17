package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.time.ITimeRangedValue;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.range.TimeRange;

@Immutable
public class TimeRangedPercent extends Percent implements ITimeRangedValue<Percent> {

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

    @Override
    public TimeRange getTimeRange() {
        return timeRange;
    }

    @Override
    public Percent asValue() {
        return this;
    }

}
