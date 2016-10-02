package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

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
    public final List<Entry<FDate, V>> getPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits) {
        //This has to work with lists internally to support FilterDuplicateKeys option
        final List<Entry<FDate, V>> trailing = new ArrayList<Entry<FDate, V>>();
        //With 10 units this iterates from 9 to 0
        //to go from past to future so that queries get minimized
        //only on the first call we risk multiple queries
        for (int unitsBack = shiftBackUnits - 1; unitsBack >= 0; unitsBack--) {
            final Entry<FDate, V> previousEntry = getPreviousEntry(query, key, unitsBack);
            if (previousEntry != null) {
                trailing.add(previousEntry);
            }
        }
        return trailing;
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
