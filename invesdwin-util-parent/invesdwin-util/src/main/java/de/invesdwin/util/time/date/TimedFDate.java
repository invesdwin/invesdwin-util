package de.invesdwin.util.time.date;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.tuple.IPair;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.lang.Objects;

@Immutable
public class TimedFDate implements IHistoricalValue<TimedFDate>, Comparable<Object>, IPair<FDate, FDate> {

    public static final TimedFDate DUMMY = new TimedFDate(FDates.MIN_DATE, FDates.MIN_DATE);

    private final FDate time;
    private final FDate value;

    public TimedFDate(final FDate time, final FDate value) {
        this.time = time;
        this.value = value;
    }

    public FDate getTime() {
        return time;
    }

    public FDate getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TimedFDate) {
            final TimedFDate cObj = (TimedFDate) obj;
            return Objects.equals(value, cObj.value);
        } else if (obj instanceof FDate) {
            final FDate cObj = (FDate) obj;
            return Objects.equals(value, cObj);
        } else {
            return false;
        }
    }

    public int compareTo(final FDate o) {
        return FDates.compare(value, o);
    }

    @Override
    public int compareTo(final Object o) {
        if (o instanceof TimedFDate) {
            final TimedFDate cO = (TimedFDate) o;
            return FDates.compare(value, cO.value);
        } else if (o instanceof FDate) {
            final FDate cO = (FDate) o;
            return FDates.compare(value, cO);
        }
        return 1;
    }

    @Override
    public int hashCode() {
        // equals is value-only (also cross-type against the raw FDate), so the hash must be
        // value-only too — folding time or the class token in here breaks equal-but-different-hash.
        return Objects.hashCode(value);
    }

    @Override
    public IHistoricalEntry<? extends TimedFDate> asHistoricalEntry() {
        return new IHistoricalEntry<TimedFDate>() {

            @Override
            public FDate getKey() {
                return time;
            }

            @Override
            public TimedFDate getValue() {
                return TimedFDate.this;
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
    public FDate getSecond() {
        return value;
    }

}
