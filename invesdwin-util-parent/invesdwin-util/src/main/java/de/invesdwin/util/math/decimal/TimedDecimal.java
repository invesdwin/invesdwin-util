package de.invesdwin.util.math.decimal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.bean.tuple.IPair;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@Immutable
public class TimedDecimal extends Decimal implements IHistoricalValue<TimedDecimal>, IPair<FDate, Decimal> {

    public static final TimedDecimal DUMMY = new TimedDecimal(FDates.MIN_DATE, Decimal.ZERO);
    private final FDate time;

    public TimedDecimal(final FDate time, final Decimal decimal) {
        this(time, decimal.doubleValue());
    }

    public TimedDecimal(final FDate time, final double decimal) {
        super(decimal);
        Assertions.checkNotNull(time);
        this.time = time;
    }

    public FDate getTime() {
        return time;
    }

    @Override
    public IHistoricalEntry<? extends TimedDecimal> asHistoricalEntry() {
        return new IHistoricalEntry<TimedDecimal>() {

            @Override
            public FDate getKey() {
                return TimedDecimal.this.getTime();
            }

            @Override
            public TimedDecimal getValue() {
                return TimedDecimal.this;
            }

            @Override
            public String toString() {
                return getKey() + " -> " + getValue();
            }
        };
    }

    @Override
    public String toString() {
        return time + ": " + super.toString();
    }

    @Override
    public FDate getFirst() {
        return time;
    }

    @Override
    public Decimal getSecond() {
        return this;
    }

}
