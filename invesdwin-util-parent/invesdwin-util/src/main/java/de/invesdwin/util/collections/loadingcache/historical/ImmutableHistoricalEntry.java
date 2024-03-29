package de.invesdwin.util.collections.loadingcache.historical;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.IFDateProvider;

@Immutable
public final class ImmutableHistoricalEntry<T> implements IHistoricalEntry<T> {
    private final FDate key;
    private final T value;

    private ImmutableHistoricalEntry(final FDate key, final T value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean isValuePresent() {
        return value != null;
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

    public static <T> IHistoricalEntry<T> of(final IFDateProvider key, final T value) {
        return new ImmutableHistoricalEntry<T>(key.asFDate(), value);
    }

    @SuppressWarnings("unchecked")
    public static <T> IHistoricalEntry<T> maybeExtractKey(final AHistoricalCache<T> parent, final IFDateProvider key,
            final T value) {
        if (value == null) {
            //null entries are valid and we still need to be able to query further back in time
            return ImmutableHistoricalEntry.of(key, null);
        } else if (value instanceof IHistoricalEntry) {
            return (IHistoricalEntry<T>) value;
        } else if (value instanceof IHistoricalValue) {
            final IHistoricalValue<T> cValue = (IHistoricalValue<T>) value;
            return (IHistoricalEntry<T>) cValue.asHistoricalEntry();
        } else {
            final FDate valueKey = parent.extractKey(key, value);
            return ImmutableHistoricalEntry.of(valueKey, value);
        }
    }

    @Override
    public String toString() {
        return getKey() + " -> " + getValue();
    }

}