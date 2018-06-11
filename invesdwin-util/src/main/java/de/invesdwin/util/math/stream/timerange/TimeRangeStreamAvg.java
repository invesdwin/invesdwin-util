package de.invesdwin.util.math.stream.timerange;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.number.NumberStreamSum;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.range.TimeRange;

@NotThreadSafe
public class TimeRangeStreamAvg<E extends TimeRange> implements IStreamAlgorithm<E, Void> {

    private int count = 0;
    private final NumberStreamSum<Long> sumFrom = new NumberStreamSum<>();
    private final NumberStreamSum<Long> sumTo = new NumberStreamSum<>();

    @Override
    public Void process(final E value) {
        count++;
        if (value != null) {
            sumFrom.process(value.getFrom().millisValue());
            sumTo.process(value.getTo().millisValue());
        }
        return null;
    }

    public TimeRange getAvg() {
        final double avgFrom = sumFrom.getSum() / count;
        final double avgTo = sumTo.getSum() / count;
        final FDate from = new FDate((long) avgFrom);
        final FDate to = new FDate((long) avgTo);
        return new TimeRange(from, to);
    }

    public TimeRange getSum() {
        final double sumFromDouble = sumFrom.getSum();
        final double sumToDouble = sumTo.getSum();
        final FDate from = new FDate((long) sumFromDouble);
        final FDate to = new FDate((long) sumToDouble);
        return new TimeRange(from, to);
    }

    public int getCount() {
        return count;
    }

}
