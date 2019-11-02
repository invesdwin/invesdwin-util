package de.invesdwin.util.math.stream.timerange;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.doubl.DoubleStreamAvg;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.range.TimeRange;

@NotThreadSafe
public class TimeRangeStreamAvg<E extends TimeRange> implements IStreamAlgorithm<E, Void> {

    private final DoubleStreamAvg avgFrom = new DoubleStreamAvg();
    private final DoubleStreamAvg avgTo = new DoubleStreamAvg();

    @Override
    public Void process(final E value) {
        if (value != null) {
            avgFrom.process(value.getFrom().millisValue());
            avgTo.process(value.getTo().millisValue());
        }
        return null;
    }

    public TimeRange getAvg() {
        final FDate from = new FDate((long) avgFrom.getAvg());
        final FDate to = new FDate((long) avgTo.getAvg());
        return new TimeRange(from, to);
    }

    public long getCount() {
        return avgFrom.getCount();
    }

}
