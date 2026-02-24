package de.invesdwin.util.collections.loadingcache.historical;

import java.util.Optional;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
public final class IndexedHistoricalEntry<V> implements IHistoricalEntry<V> {

    private final int parentIdentityHashCode;
    private IHistoricalCacheInternalMethods<V> loadValueInternalMethods;
    private volatile FDate key;
    private volatile Optional<V> value;
    private volatile FDate prevKey;
    private volatile FDate nextKey;

    public IndexedHistoricalEntry(final IHistoricalCacheInternalMethods<V> internalMethods, final FDate key) {
        this.parentIdentityHashCode = internalMethods.getParentIdentityHashCode();
        this.key = internalMethods.getAdjustKeyProvider().newAlreadyAdjustedKey(key);
        this.loadValueInternalMethods = internalMethods;
    }

    public IndexedHistoricalEntry(final IHistoricalCacheInternalMethods<V> internalMethods, final FDate key,
            final V value) {
        this.parentIdentityHashCode = internalMethods.getParentIdentityHashCode();
        this.key = internalMethods.getAdjustKeyProvider().newAlreadyAdjustedKey(key);
        this.value = Optional.ofNullable(value);
    }

    @Override
    public FDate getKey() {
        return key;
    }

    @Override
    public V getValue() {
        final IHistoricalCacheInternalMethods<V> loadValueInternalMethodsCopy = loadValueInternalMethods;
        if (loadValueInternalMethodsCopy != null) {
            //better stay lock-free here to avoid deadlocks (unconfirmed)
            loadValue(loadValueInternalMethodsCopy);
            //prevent memory leak due to reference leak
            loadValueInternalMethods = null;
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

    public void setValue(final IHistoricalCacheInternalMethods<V> internalMethods, final FDate key, final V value) {
        this.key = internalMethods.getAdjustKeyProvider().newAlreadyAdjustedKey(key);
        this.value = Optional.ofNullable(value);
    }

    @SuppressWarnings("unchecked")
    private synchronized void loadValue(final IHistoricalCacheInternalMethods<V> internalMethods) {
        if (value != null) {
            return;
        }
        final V v = internalMethods.newLoadValue().evaluateGeneric(key);
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
            unadj = internalMethods.extractKey(key, v);
        }
        key = internalMethods.getAdjustKeyProvider().newAlreadyAdjustedKey(unadj);
    }

    public FDate getPrevKey(final IHistoricalCacheInternalMethods<V> internalMethods) {
        if (prevKey == null) {
            synchronized (this) {
                if (prevKey == null) {
                    internalMethods.invokeRefreshIfRequested();
                    final FDate unadj = internalMethods.innerCalculatePreviousKey(key);
                    if (unadj == null) {
                        return null;
                    }
                    if (unadj.isAfterNotNullSafe(key)) {
                        return null;
                    }
                    final FDate adj = internalMethods.getAdjustKeyProvider().newAlreadyAdjustedKey(unadj);
                    if (adj.isAfterNotNullSafe(key)) {
                        return null;
                    }
                    prevKey = adj;
                }
            }
        }
        return prevKey;
    }

    public void setPrevKey(final IHistoricalCacheInternalMethods<V> internalMethods, final FDate prev) {
        if (prev == null) {
            this.prevKey = null;
            return;
        }
        if (prev.isAfterNotNullSafe(key)) {
            this.prevKey = null;
            return;
        }
        final FDate adj = internalMethods.getAdjustKeyProvider().newAlreadyAdjustedKey(prev);
        if (adj.isAfterNotNullSafe(key)) {
            this.prevKey = null;
            return;
        }
        this.prevKey = adj;
    }

    public FDate getNextKey(final IHistoricalCacheInternalMethods<V> internalMethods) {
        if (nextKey == null) {
            synchronized (this) {
                if (nextKey == null) {
                    internalMethods.invokeRefreshIfRequested();
                    final FDate unadj = internalMethods.innerCalculateNextKey(key);
                    if (unadj == null) {
                        return null;
                    }
                    if (unadj.isBeforeOrEqualToNotNullSafe(key)) {
                        return null;
                    }
                    final FDate adj = internalMethods.getAdjustKeyProvider().newAlreadyAdjustedKey(unadj);
                    if (adj.isBeforeOrEqualToNotNullSafe(key)) {
                        return null;
                    }
                    nextKey = adj;
                }
            }
        }
        return nextKey;
    }

    public void setNextKey(final IHistoricalCacheInternalMethods<V> internalMethods, final FDate next) {
        if (next == null) {
            this.nextKey = null;
            return;
        }
        if (next.isBeforeOrEqualToNotNullSafe(key)) {
            this.nextKey = null;
            return;
        }
        final FDate adj = internalMethods.getAdjustKeyProvider().newAlreadyAdjustedKey(next);
        if (adj.isBeforeOrEqualToNotNullSafe(key)) {
            this.nextKey = null;
            return;
        }
        this.nextKey = adj;
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
    public static <T> IHistoricalEntry<T> maybeExtractKey(final IHistoricalCacheInternalMethods<T> internalMethods,
            final FDate key, final T value) {
        if (value == null) {
            return null;
        }
        if (value instanceof IndexedHistoricalEntry) {
            final IndexedHistoricalEntry<T> cValue = (IndexedHistoricalEntry<T>) value;
            if (cValue.parentIdentityHashCode == internalMethods.getParentIdentityHashCode()) {
                return cValue;
            }
        }
        if (value instanceof IHistoricalEntry) {
            final IHistoricalEntry<T> cValue = (IHistoricalEntry<T>) value;
            return new IndexedHistoricalEntry<T>(internalMethods, cValue.getKey(), cValue.getValue());
        } else if (value instanceof IHistoricalValue) {
            final IHistoricalValue<T> cValue = (IHistoricalValue<T>) value;
            final IHistoricalEntry<T> ccValue = (IHistoricalEntry<T>) cValue.asHistoricalEntry();
            return new IndexedHistoricalEntry<T>(internalMethods, ccValue.getKey(), ccValue.getValue());
        } else {
            final FDate valueKey = internalMethods.extractKey(key, value);
            return new IndexedHistoricalEntry<T>(internalMethods, valueKey, value);
        }
    }

    public static <T> IHistoricalEntry<T> maybeExtractKey(final IHistoricalCacheInternalMethods<T> internalMethods,
            final FDate key, final IHistoricalEntry<T> value) {
        if (value == null) {
            return null;
        }
        if (value instanceof IndexedHistoricalEntry) {
            final IndexedHistoricalEntry<T> cValue = (IndexedHistoricalEntry<T>) value;
            if (cValue.parentIdentityHashCode == internalMethods.getParentIdentityHashCode()) {
                return cValue;
            }
        }
        return new IndexedHistoricalEntry<T>(internalMethods, value.getKey(), value.getValue());
    }

    @Override
    public String toString() {
        return getKey() + " -> " + getValueIfPresent();
    }

}