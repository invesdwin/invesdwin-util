package de.invesdwin.util.time;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class TimeRange {

    private final FDate from;
    private final FDate to;

    public TimeRange(final FDate from, final FDate to) {
        Assertions.checkNotNull(from);
        Assertions.checkNotNull(to);
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

}
