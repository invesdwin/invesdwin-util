package de.invesdwin.util.time;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDates;

@Immutable
public class TimeRange extends AValueObject {

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
        if (from == null || to == null) {
            return null;
        } else {
            return new Duration(from, to);
        }
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

    public static List<Duration> extractDurations(final Iterable<TimeRange> timeRanges) {
        final List<Duration> durations = new ArrayList<Duration>();
        for (final TimeRange pr : timeRanges) {
            durations.add(pr.getDuration());
        }
        return durations;
    }

    public static List<FDate> extractFroms(final Iterable<TimeRange> timeRanges) {
        final List<FDate> durations = new ArrayList<FDate>();
        for (final TimeRange pr : timeRanges) {
            durations.add(pr.getFrom());
        }
        return durations;
    }

    public static List<FDate> extractTos(final Iterable<TimeRange> timeRanges) {
        final List<FDate> durations = new ArrayList<FDate>();
        for (final TimeRange pr : timeRanges) {
            durations.add(pr.getTo());
        }
        return durations;
    }

    public static TimeRange avg(final List<TimeRange> timeRanges) {
        final FDate avgFrom = FDates.avg(extractFroms(timeRanges));
        final FDate avgTo = FDates.avg(extractTos(timeRanges));
        return new TimeRange(avgFrom, avgTo);
    }

    public boolean isZeroDuration() {
        return from == null || to == null || getDuration().isZero();
    }
}
