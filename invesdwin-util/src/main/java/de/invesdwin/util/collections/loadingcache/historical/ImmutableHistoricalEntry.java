package de.invesdwin.util.collections.loadingcache.historical;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class ImmutableHistoricalEntry<T> implements IHistoricalEntry<T> {
    private final FDate key;
    private final T value;

    private ImmutableHistoricalEntry(final FDate key, final T value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public FDate getKey() {
        return key;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(IHistoricalEntry.class, getValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof IHistoricalEntry) {
            final IHistoricalEntry<?> cObj = (IHistoricalEntry<?>) obj;
            return Objects.equals(getValue(), cObj.getValue());
        }
        return false;
    }

    public static <T> IHistoricalEntry<T> of(final FDate key, final T value) {
        return new ImmutableHistoricalEntry<T>(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> IHistoricalEntry<T> maybeExtractKey(final AHistoricalCache<T> parent, final FDate key,
            final T value) {
        if (value == null) {
            return null;
        }
        if (value instanceof IHistoricalEntry) {
            return (IHistoricalEntry<T>) value;
        } else if (value instanceof IHistoricalValue) {
            final IHistoricalValue<T> cValue = (IHistoricalValue<T>) value;
            return (IHistoricalEntry<T>) cValue.asHistoricalEntry();
        } else {
            final FDate valueKey = parent.extractKeyNoCasting(key, value);
            return ImmutableHistoricalEntry.of(valueKey, value);
        }
    }
}