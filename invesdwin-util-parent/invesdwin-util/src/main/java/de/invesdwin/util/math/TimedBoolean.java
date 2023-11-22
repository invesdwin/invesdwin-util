package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.tuple.IPair;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@Immutable
public class TimedBoolean implements IHistoricalValue<TimedBoolean>, Comparable<Object>, IPair<FDate, Boolean> {

    public static final TimedBoolean DUMMY = new TimedBoolean(FDates.MIN_DATE, false);

    private final FDate time;
    private final boolean value;

    public TimedBoolean(final FDate time, final boolean value) {
        this.time = time;
        this.value = value;
    }

    public FDate getTime() {
        return time;
    }

    public boolean getValue() {
        return value;
    }

    public boolean booleanValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TimedBoolean) {
            final TimedBoolean cObj = (TimedBoolean) obj;
            return Objects.equals(value, cObj.booleanValue());
        } else if (obj instanceof Boolean) {
            final Boolean cObj = (Boolean) obj;
            return Objects.equals(value, cObj.booleanValue());
        } else {
            return false;
        }
    }

    public int compareTo(final boolean o) {
        return Boolean.compare(value, o);
    }

    public int compareTo(final Boolean o) {
        if (o == null) {
            return 1;
        }
        return Boolean.compare(value, o.booleanValue());
    }

    @Override
    public int compareTo(final Object o) {
        if (o instanceof TimedBoolean) {
            final TimedBoolean cO = (TimedBoolean) o;
            return Boolean.compare(value, cO.booleanValue());
        } else if (o instanceof Boolean) {
            final Boolean cO = (Boolean) o;
            return Boolean.compare(value, cO.booleanValue());
        }
        return 1;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(TimedBoolean.class, time, value);
    }

    @Override
    public IHistoricalEntry<? extends TimedBoolean> asHistoricalEntry() {
        return new IHistoricalEntry<TimedBoolean>() {

            @Override
            public FDate getKey() {
                return time;
            }

            @Override
            public TimedBoolean getValue() {
                return TimedBoolean.this;
            }
        };
    }

    @Override
    public String toString() {
        return time + ": " + value;
    }

    @Override
    public FDate getFirst() {
        return time;
    }

    @Override
    public Boolean getSecond() {
        return value;
    }

}
