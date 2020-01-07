package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class TimedDouble implements IHistoricalValue<TimedDouble> {

    public static final TimedDouble DUMMY = new TimedDouble(FDate.MIN_DATE, Double.NaN);

    private final FDate time;
    private final double value;

    public TimedDouble(final FDate time, final double value) {
        this.time = time;
        this.value = value;
    }

    public FDate getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

    @Override
    public IHistoricalEntry<? extends TimedDouble> asHistoricalEntry() {
        return new IHistoricalEntry<TimedDouble>() {

            @Override
            public FDate getKey() {
                return time;
            }

            @Override
            public TimedDouble getValue() {
                return TimedDouble.this;
            }
        };
    }

}
