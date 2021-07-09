package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.tuple.IPair;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class TimedDouble extends Number
        implements IHistoricalValue<TimedDouble>, Comparable<Object>, IPair<FDate, Number> {

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
    public boolean equals(final Object obj) {
        if (obj instanceof Number) {
            final Number cObj = (Number) obj;
            return Objects.equals(value, cObj.doubleValue());
        } else {
            return false;
        }
    }

    public int compareTo(final double o) {
        return Doubles.compare(value, o);
    }

    public int compareTo(final Double o) {
        if (o == null) {
            return 1;
        }
        return Doubles.compare(value, o.doubleValue());
    }

    @Override
    public int compareTo(final Object o) {
        if (o instanceof Number) {
            final Number cO = (Number) o;
            return Doubles.compare(value, cO.doubleValue());
        }
        return 1;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(TimedDouble.class, time, value);
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

    @Override
    public int intValue() {
        return Integers.checkedCast(value);
    }

    @Override
    public long longValue() {
        return Longs.checkedCast(value);
    }

    @Override
    public float floatValue() {
        return Floats.checkedCast(value);
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return time + ": " + Decimal.toString(value);
    }

    @Override
    public FDate getFirst() {
        return time;
    }

    @Override
    public Number getSecond() {
        return this;
    }

}
