package de.invesdwin.util.time;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class TimeRange {

    private final FDate from;
    private final FDate to;

    public TimeRange(final FDate from, final FDate to) {
        this.from = from;
        this.to = to;
    }

    public FDate getFrom() {
        return from;
    }

    public FDate getTo() {
        return to;
    }

    public Duration getDuration() {
        return new Duration(from, to);
    }

    @Override
    public String toString() {
        return getFrom() + " -> " + getTo() + " => " + getDuration();
    }

    public TimeRange asNonNull(final FDate fromNullReplacement, final FDate toNullReplacement) {
        final FDate usedFrom;
        if (from == null) {
            usedFrom = fromNullReplacement;
        } else {
            usedFrom = from;
        }
        final FDate usedTo;
        if (to == null) {
            usedTo = toNullReplacement;
        } else {
            usedTo = to;
        }
        return new TimeRange(usedFrom, usedTo);
    }

}
