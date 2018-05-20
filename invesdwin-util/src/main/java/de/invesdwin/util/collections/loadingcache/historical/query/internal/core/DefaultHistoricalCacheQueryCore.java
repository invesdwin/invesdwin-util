package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.impl.GetNextEntryQueryImpl;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.impl.GetPreviousEntryQueryImpl;
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
        if (value != null) {
            final IHistoricalCacheQueryElementFilter<V> elementFilter = query.getElementFilter();
            if (elementFilter != null) {
                FDate valueKey = parent.extractKey(key, value);
                while (!elementFilter.isValid(valueKey, value)) {
                    value = null;
                    //try earlier dates to find a valid element
                    final FDate previousKey = parent.calculatePreviousKey(valueKey);
                    final V previousValue = parent.getValuesMap().get(previousKey);
                    if (previousValue == null) {
                        break;
                    }
                    final FDate previousValueKey = parent.extractKey(previousKey, previousValue);
                    if (previousValueKey.equals(valueKey)) {
                        break;
                    }
                    valueKey = previousValueKey;
                    value = previousValue;
                }
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
    public Entry<FDate, V> getPreviousEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftBackUnits) {
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
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
    public ICloseableIterable<Entry<FDate, V>> getPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftBackUnits) {
        //This has to work with lists internally to support FilterDuplicateKeys option
        final List<Entry<FDate, V>> trailing = query.newEntriesList(shiftBackUnits);
        //With 10 units this iterates from 9 to 0
        //to go from past to future so that queries get minimized
        //only on the first call we risk multiple queries
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        for (int unitsBack = shiftBackUnits - 1; unitsBack >= 0 && !impl.iterationFinished(); unitsBack--) {
            final Entry<FDate, V> value = impl.getResult();
            if (value != null) {
                if (!trailing.add(value)) {
                    break;
                }
            } else {
                break;
            }
        }
        Collections.reverse(trailing);
        return WrapperCloseableIterable.maybeWrap(trailing);
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getNextEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftForwardUnits) {
        //This has to work with lists internally to support FilterDuplicateKeys option
        final List<Entry<FDate, V>> trailing = query.newEntriesList(shiftForwardUnits);
        //With 10 units this iterates from 9 to 0
        //to go from past to future so that queries get minimized
        //only on the first call we risk multiple queries
        final GetNextEntryQueryImpl<V> impl = new GetNextEntryQueryImpl<V>(this, query, key, shiftForwardUnits);
        for (int unitsBack = shiftForwardUnits - 1; unitsBack >= 0 && !impl.iterationFinished(); unitsBack--) {
            final Entry<FDate, V> value = impl.getResult();
            if (value != null) {
                if (!trailing.add(value)) {
                    break;
                }
            } else {
                break;
            }
        }
        return WrapperCloseableIterable.maybeWrap(trailing);
    }

    @Override
    public Entry<FDate, V> getNextEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftForwardUnits) {
        final GetNextEntryQueryImpl<V> impl = new GetNextEntryQueryImpl<V>(this, query, key, shiftForwardUnits);
        //CHECKSTYLE:OFF
        while (!impl.iterationFinished()) {
            //noop
        }
        //CHECKSTYLE:ON
        return impl.getResult();
    }

    @Override
    public void clear() {
        //noop since not caching anything
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        //noop since not caching anything
    }

}
