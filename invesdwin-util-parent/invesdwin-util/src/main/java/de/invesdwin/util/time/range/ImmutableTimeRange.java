package de.invesdwin.util.time.range;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.ImmutableFDate;

@Immutable
public final class ImmutableTimeRange extends TimeRange {

    public static final ImmutableTimeRange UNLIMITED = new ImmutableTimeRange(null, null);

    private ImmutableTimeRange(final ImmutableFDate from, final ImmutableFDate to) {
        super(from, to);
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
        final FDate from = timeRange.getFrom();
        final FDate to = timeRange.getTo();
        return valueOf(from, to);
    }

    public static ImmutableTimeRange valueOf(final FDate from, final FDate to) {
        if (from == null && to == null) {
            return UNLIMITED;
        }
        return new ImmutableTimeRange(ImmutableFDate.valueOf(from), ImmutableFDate.valueOf(to));
    }

}
