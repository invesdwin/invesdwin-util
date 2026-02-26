package de.invesdwin.util.time.range;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.ImmutableFDate;

@Immutable
public final class ImmutableTimeRange extends TimeRange {

    private ImmutableTimeRange(final FDate from, final FDate to) {
        super(ImmutableFDate.valueOf(from), ImmutableFDate.valueOf(to));
    }

    @Override
    public ImmutableFDate getFrom() {
        return (ImmutableFDate) super.getFrom();
    }

    @Override
    public ImmutableFDate getTo() {
        return (ImmutableFDate) super.getTo();
    }

    public static ImmutableTimeRange valueOf(final TimeRange timeRange) {
        if (timeRange == null) {
            return null;
        }
        if (timeRange instanceof ImmutableTimeRange) {
            return (ImmutableTimeRange) timeRange;
        }
        return new ImmutableTimeRange(timeRange.getFrom(), timeRange.getTo());
    }

}
