package de.invesdwin.util.math.timed;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.tuple.IPair;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Floats;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@Immutable
public class TimedDouble extends Number
        implements IHistoricalValue<TimedDouble>, Comparable<Object>, IPair<FDate, Number>, ITimedDouble {

    public static final TimedDouble DUMMY = new TimedDouble(FDates.MIN_DATE, Double.NaN);
    public static final TimedDouble NAN = new TimedDouble(null, Double.NaN);

    private final FDate time;
    private final double value;

    public TimedDouble(final ITimedDouble copyOf) {
        this.time = copyOf.getTime();
        this.value = copyOf.getValue();
    }

    public TimedDouble(final FDate time, final double value) {
        this.time = time;
        this.value = value;
    }

    @Override
    public FDate getTime() {
        return time;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return TimedDoubleBaseMethods.hashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return TimedDoubleBaseMethods.equals(this, obj);
    }

    @Override
    public String toString() {
        return TimedDoubleBaseMethods.toString(this);
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
    public FDate getFirst() {
        return time;
    }

    @Override
    public Number getSecond() {
        return this;
    }

    public static TimedDouble valueOf(final ITimedDouble value) {
        if (value == null) {
            return null;
        }
        if (value instanceof TimedDouble) {
            return (TimedDouble) value;
        }
        return new TimedDouble(value);
    }

}
