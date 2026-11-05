package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.tuple.IPair;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@Immutable
public class TimedReference<V> implements IHistoricalValue<TimedReference<V>>, IPair<FDate, V>, IReference<V> {

    public static final TimedReference DUMMY = new TimedReference(FDates.MIN_DATE, null);

    private final FDate time;
    private final V value;

    public TimedReference(final FDate time, final V value) {
        this.time = time;
        this.value = value;
    }

    public FDate getTime() {
        return time;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TimedReference) {
            final TimedReference<?> cObj = (TimedReference<?>) obj;
            return Objects.equals(value, cObj.value);
        } else if (obj instanceof FDate) {
            final FDate cObj = (FDate) obj;
            return Objects.equals(value, cObj);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // equals is value-only (also cross-type against the raw value), so the hash must be
        // value-only too — folding time or the class token in here breaks equal-but-different-hash.
        return Objects.hashCode(value);
    }

    @Override
    public IHistoricalEntry<? extends TimedReference<V>> asHistoricalEntry() {
        return new IHistoricalEntry<TimedReference<V>>() {

            @Override
            public FDate getKey() {
                return time;
            }

            @Override
            public TimedReference<V> getValue() {
                return TimedReference.this;
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
    public V getSecond() {
        return value;
    }

    @Override
    public V get() {
        return value;
    }

}
