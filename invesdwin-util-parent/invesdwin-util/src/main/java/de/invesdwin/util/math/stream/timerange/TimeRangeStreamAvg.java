package de.invesdwin.util.math.stream.timerange;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.doubl.DoubleStreamAvg;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.range.TimeRange;

@NotThreadSafe
public class TimeRangeStreamAvg<E extends TimeRange> implements IStreamAlgorithm<E, Void> {

    private final DoubleStreamAvg avgFromMillis = new DoubleStreamAvg();
    private final DoubleStreamAvg avgFromPicos = new DoubleStreamAvg();
    private final DoubleStreamAvg avgToMillis = new DoubleStreamAvg();
    private final DoubleStreamAvg avgToPicos = new DoubleStreamAvg();

    @Override
    public Void process(final E value) {
        if (value != null && value.getFrom() != null && value.getTo() != null) {
            avgFromMillis.process(value.getFrom().millisValue());
            avgFromPicos.process(value.getFrom().picosValue());
            avgToMillis.process(value.getTo().millisValue());
            avgToPicos.process(value.getTo().picosValue());
        }
        return null;
    }

    public TimeRange getAvg() {
        final FDate from = new FDate((long) avgFromMillis.getAvg(), (int) avgFromPicos.getAvg());
        final FDate to = new FDate((long) avgToMillis.getAvg(), (int) avgToPicos.getAvg());
        return new TimeRange(from, to);
    }

    public long getCount() {
        return avgFromMillis.getCount();
    }

}
