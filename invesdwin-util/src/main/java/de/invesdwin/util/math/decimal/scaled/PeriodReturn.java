package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.TimeRange;

@Immutable
public class PeriodReturn extends Percent {

    private final TimeRange timeRange;

    public PeriodReturn(final TimeRange timeRange, final Percent percent) {
        super(percent.getDefaultValue(), percent.getDefaultScale());
        this.timeRange = timeRange;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

}
