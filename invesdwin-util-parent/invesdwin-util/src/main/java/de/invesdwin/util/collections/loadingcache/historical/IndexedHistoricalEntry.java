package de.invesdwin.util.collections.loadingcache.historical;

import java.util.Optional;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public final class IndexedHistoricalEntry<V> implements IHistoricalEntry<V> {

    private final IHistoricalCacheInternalMethods<V> parent;
    private FDate key;
    private Optional<V> value;
    private FDate prevKey;
    private FDate nextKey;

    public IndexedHistoricalEntry(final IHistoricalCacheInternalMethods<V> parent, final FDate key) {
        this.parent = parent;
        this.key = parent.getAdjustKeyProvider().newAlreadyAdjustedKey(key);
    }

    public IndexedHistoricalEntry(final IHistoricalCacheInternalMethods<V> parent, final FDate key, final V value) {
        this.parent = parent;
        this.key = parent.getAdjustKeyProvider().newAlreadyAdjustedKey(key);
        this.value = Optional.ofNullable(value);
    }

    @Override
    public FDate getKey() {
        return key;
    }

    @Override
    public V getValue() {
        if (value == null) {
            loadValue();
        }
        return value.orElse(null);
    }

    @Override
    public boolean isValuePresent() {
        return value != null;
    }

    @Override
    public V getValueIfPresent() {
        if (value == null) {
            return null;
        } else {
            return value.orElse(null);
        }
    }

    public void setValue(final FDate key, final V value) {
        this.key = parent.getAdjustKeyProvider().newAlreadyAdjustedKey(key);
        this.value = Optional.ofNullable(value);
    }

    @SuppressWarnings("unchecked")
    private synchronized void loadValue() {
        if (value != null) {
            return;
        }
        final V v = parent.newLoadValue().evaluateGeneric(key);
        value = Optional.ofNullable(v);
        if (v == null) {
            return;
        }
        final FDate unadj;
        if (v instanceof IHistoricalEntry) {
            final IHistoricalEntry<V> cValue = (IHistoricalEntry<V>) v;
            unadj = cValue.getKey();
        } else if (v instanceof IHistoricalValue) {
            final IHistoricalValue<V> cValue = (IHistoricalValue<V>) v;
            final IHistoricalEntry<V> ccValue = (IHistoricalEntry<V>) cValue.asHistoricalEntry();
            unadj = ccValue.getKey();
        } else {
            unadj = parent.extractKey(key, v);
        }
        key = parent.getAdjustKeyProvider().newAlreadyAdjustedKey(unadj);
    }

    public FDate getPrevKey() {
        if (prevKey == null) {
            synchronized (this) {
                if (prevKey == null) {
                    parent.invokeRefreshIfRequested();
                    final FDate unadj = parent.innerCalculatePreviousKey(key);
                    prevKey = parent.getAdjustKeyProvider().newAlreadyAdjustedKey(unadj);
                }
            }
        }
        return prevKey;
    }

    public void setPrevKey(final FDate prev) {
        this.prevKey = parent.getAdjustKeyProvider().newAlreadyAdjustedKey(prev);
    }

    public FDate getNextKey() {
        if (nextKey == null) {
            synchronized (this) {
                if (nextKey == null) {
                    parent.invokeRefreshIfRequested();
                    final FDate unadj = parent.innerCalculateNextKey(key);
                    nextKey = parent.getAdjustKeyProvider().newAlreadyAdjustedKey(unadj);
                }
            }
        }
        return nextKey;
    }

    public void setNextKey(final FDate next) {
        this.nextKey = parent.getAdjustKeyProvider().newAlreadyAdjustedKey(next);
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

    @SuppressWarnings("unchecked")
    public static <T> IHistoricalEntry<T> maybeExtractKey(final IHistoricalCacheInternalMethods<T> parent,
            final FDate key, final T value) {
        if (value == null) {
            return null;
        }
        if (value instanceof IndexedHistoricalEntry) {
            final IndexedHistoricalEntry<T> cValue = (IndexedHistoricalEntry<T>) value;
            if (cValue.parent == parent) {
                return cValue;
            }
        }
        if (value instanceof IHistoricalEntry) {
            final IHistoricalEntry<T> cValue = (IHistoricalEntry<T>) value;
            return new IndexedHistoricalEntry<T>(parent, cValue.getKey(), cValue.getValue());
        } else if (value instanceof IHistoricalValue) {
            final IHistoricalValue<T> cValue = (IHistoricalValue<T>) value;
            final IHistoricalEntry<T> ccValue = (IHistoricalEntry<T>) cValue.asHistoricalEntry();
            return new IndexedHistoricalEntry<T>(parent, ccValue.getKey(), ccValue.getValue());
        } else {
            final FDate valueKey = parent.extractKey(key, value);
            return new IndexedHistoricalEntry<T>(parent, valueKey, value);
        }
    }

    public static <T> IHistoricalEntry<T> maybeExtractKey(final IHistoricalCacheInternalMethods<T> parent,
            final FDate key, final IHistoricalEntry<T> value) {
        if (value == null) {
            return null;
        }
        if (value instanceof IndexedHistoricalEntry) {
            final IndexedHistoricalEntry<T> cValue = (IndexedHistoricalEntry<T>) value;
            if (cValue.parent == parent) {
                return cValue;
            }
        }
        return new IndexedHistoricalEntry<T>(parent, value.getKey(), value.getValue());
    }

    @Override
    public String toString() {
        return getKey() + " -> " + getValue();
    }

}