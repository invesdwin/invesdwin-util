package de.invesdwin.util.collections.loadingcache.historical.query.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public enum HistoricalCacheAssertValue {
    ASSERT_VALUE_WITH_FUTURE() {
        @Override
        public <V> IHistoricalEntry<V> internalAssertValue(final IHistoricalCacheInternalMethods<V> parent,
                final FDate key, final IHistoricalEntry<V> entry) {
            return entry;
        }

    },
    ASSERT_VALUE_WITH_FUTURE_NULL() {
        @Override
        public <V> IHistoricalEntry<V> internalAssertValue(final IHistoricalCacheInternalMethods<V> parent,
                final FDate key, final IHistoricalEntry<V> entry) {
            if (entry.getKey().compareToNotNullSafe(key) >= 1) {
                return null;
            }
            return entry;
        }
    },
    ASSERT_VALUE_WITHOUT_FUTURE() {
        @Override
        public <V> IHistoricalEntry<V> internalAssertValue(final IHistoricalCacheInternalMethods<V> parent,
                final FDate key, final IHistoricalEntry<V> entry) {
            if (entry.getKey().compareToNotNullSafe(key) >= 1) {
                throw new IllegalArgumentException("Value key [" + entry.getKey() + "] is after requested key [" + key
                        + "]. Thus it comes from the future, which is not allowed!");
            }
            return entry;
        }
    };

    protected abstract <V> IHistoricalEntry<V> internalAssertValue(IHistoricalCacheInternalMethods<V> parent, FDate key,
            IHistoricalEntry<V> entry);

    public <V> IHistoricalEntry<V> assertValue(final IHistoricalCacheInternalMethods<V> parent, final FDate key,
            final IHistoricalEntry<V> entry) {
        if (entry == null) {
            return null;
        } else {
            final IHistoricalEntry<V> assertedValue = internalAssertValue(parent, key, entry);
            if (assertedValue == null || assertedValue.getValue() == null) {
                return null;
            } else {
                return assertedValue;
            }
        }
    }

    public static <V> V unwrapEntryValue(final IHistoricalEntry<V> entry) {
        if (entry == null) {
            return null;
        } else {
            return entry.getValue();
        }
    }

    public static <V> FDate unwrapEntryKey(final IHistoricalEntry<V> entry) {
        if (entry == null) {
            return null;
        } else {
            return entry.getKey(); //internalAssertValue already has made sure that the entry key is the valueKey
        }
    }

}
