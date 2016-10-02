package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class DefaultHistoricalCacheQueryCore<V> implements IHistoricalCacheQueryCore<V> {

    private final IHistoricalCacheInternalMethods<V> parent;

    public DefaultHistoricalCacheQueryCore(final IHistoricalCacheInternalMethods<V> parent) {
        this.parent = parent;
    }

    @Override
    public IHistoricalCacheInternalMethods<V> getParent() {
        return parent;
    }

    @Override
    public final Entry<FDate, V> getEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        V value = parent.getValuesMap().get(key);
        if (!query.isRememberNullValue() && value == null) {
            final FDate adjKey = parent.getAdjustKeyProvider().adjustKey(key);
            parent.remove(adjKey);
        }
        if (value != null && query.getElementFilter() != null) {
            FDate curKey = parent.extractKey(key, value);
            while (!query.getElementFilter().isValid(curKey, value)) {
                value = null;
                //try earlier dates to find a valid element
                final FDate previousKey = parent.calculatePreviousKey(curKey);
                final V previousValue = parent.getValuesMap().get(previousKey);
                if (previousValue == null) {
                    break;
                }
                final FDate previousValueKey = parent.extractKey(previousKey, previousValue);
                if (previousValueKey.equals(curKey)) {
                    break;
                }
                curKey = previousKey;
                value = previousValue;
            }
        }
        return assertValue.assertValue(parent, key, key, value);
    }

    @Override
    public final V getValue(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        if (key == null) {
            return (V) null;
        }
        return HistoricalCacheAssertValue.unwrapEntryValue(getEntry(query, key, assertValue));
    }

    @Override
    public final Entry<FDate, V> getPreviousEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftBackUnits) {
        final GetPreviousEntryQuery<V> impl = new GetPreviousEntryQuery<V>(this, query, key, shiftBackUnits);
        //CHECKSTYLE:OFF
        while (!impl.iterationFinished()) {
            //noop
        }
        //CHECKSTYLE:ON
        return impl.getResult();
    }

    /**
     * Skips null values for keys.
     * 
     * Fills the list with values from the past.
     */
    @Override
    public final ICloseableIterable<Entry<FDate, V>> getPreviousEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        //        //This has to work with lists internally to support FilterDuplicateKeys option
        //        final List<Entry<FDate, V>> trailing = query.newEntriesList();
        //        //With 10 units this iterates from 9 to 0
        //        //to go from past to future so that queries get minimized
        //        //only on the first call we risk multiple queries
        //        final GetPreviousEntryQuery<V> impl = new GetPreviousEntryQuery<V>(this, query, key, shiftBackUnits);
        //        while (!impl.iterationFinished()) {
        //            final Entry<FDate, V> value = impl.getResult();
        //            if (value != null) {
        //                trailing.add(value);
        //            } else {
        //                break;
        //            }
        //        }
        //        Collections.reverse(trailing);
        //        return WrapperCloseableIterable.maybeWrap(trailing);

        //This has to work with lists internally to support FilterDuplicateKeys option
        final List<Entry<FDate, V>> trailing = query.newEntriesList();
        //With 10 units this iterates from 9 to 0
        //to go from past to future so that queries get minimized
        //only on the first call we risk multiple queries
        for (int unitsBack = shiftBackUnits - 1; unitsBack >= 0; unitsBack--) {
            final Entry<FDate, V> previousEntry = getPreviousEntry(query, key, unitsBack);
            if (previousEntry != null) {
                trailing.add(previousEntry);
            }
        }
        return WrapperCloseableIterable.maybeWrap(trailing);
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getNextEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftForwardUnits) {
        //This has to work with lists internally to support FilterDuplicateKeys option
        final List<Entry<FDate, V>> trailing = query.newEntriesList();
        //With 10 units this iterates from 9 to 0
        //to go from past to future so that queries get minimized
        //only on the first call we risk multiple queries
        for (int unitsBack = shiftForwardUnits - 1; unitsBack >= 0; unitsBack--) {
            final Entry<FDate, V> nextEntry = getNextEntry(query, key, unitsBack);
            if (nextEntry != null) {
                trailing.add(nextEntry);
            }
        }
        return WrapperCloseableIterable.maybeWrap(trailing);
    }

    @Override
    public Entry<FDate, V> getNextEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftForwardUnits) {
        FDate nextKey = key;
        Entry<FDate, V> nextEntry = null;
        for (int i = 0; i <= shiftForwardUnits; i++) {
            /*
             * if key of value == key, the same key would be returned on the next call
             * 
             * we decrement by one unit to get the next key
             */

            final FDate nextNextKey;
            if (i == 0) {
                nextNextKey = nextKey;
            } else {
                nextNextKey = getParent().calculateNextKey(nextKey);
                if (nextNextKey == null) {
                    break;
                }
            }
            //the key of the value is the relevant one
            final Entry<FDate, V> nextNextEntry = query.getAssertValue().assertValue(getParent(), key, nextNextKey,
                    getValue(query, nextNextKey, HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE));
            if (nextNextEntry == null) {
                return null;
            } else {
                final V nextValue = nextNextEntry.getValue();
                nextKey = getParent().extractKey(nextNextKey, nextValue);
                nextEntry = nextNextEntry;
            }
        }
        return nextEntry;
    }

    @Override
    public void clear() {
        //noop since not caching anything
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        //noop since not caching anything
    }

    @Override
    public HistoricalCacheQuery<V> newQuery() {
        return new HistoricalCacheQuery<V>(this);
    }

}
