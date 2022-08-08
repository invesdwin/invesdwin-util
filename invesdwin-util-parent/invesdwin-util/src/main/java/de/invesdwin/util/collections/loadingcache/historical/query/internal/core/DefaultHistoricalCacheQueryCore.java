package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.DisabledHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.impl.GetNextEntryQueryImpl;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.impl.GetPreviousEntryQueryImpl;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.time.date.FDate;

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
    public final IHistoricalEntry<V> getEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        IHistoricalEntry<V> entry = parent.getValuesMap().get(key);
        if (entry != null) {
            final IHistoricalCacheQueryElementFilter<V> elementFilter = query.getElementFilter();
            if (elementFilter != null && !(elementFilter instanceof DisabledHistoricalCacheQueryElementFilter)) {
                FDate entryKey = entry.getKey();
                while (!elementFilter.isValid(entryKey, entry.getValue())) {
                    entry = null;
                    //try earlier dates to find a valid element
                    final FDate previousKey = parent.calculatePreviousKey(entryKey);
                    final IHistoricalEntry<V> previousEntry = parent.getValuesMap().get(previousKey);
                    if (previousEntry == null) {
                        break;
                    }
                    if (previousEntry.getKey().equals(entryKey)) {
                        break;
                    }
                    entry = previousEntry;
                    entryKey = previousEntry.getKey();
                }
            }
        }
        return assertValue.assertValue(parent, key, entry);
    }

    @Override
    public final V getValue(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        if (key == null) {
            return null;
        }
        return IHistoricalEntry.unwrapEntryValue(getEntry(query, key, assertValue));
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
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
    public ICloseableIterable<IHistoricalEntry<V>> getPreviousEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        //This has to work with lists internally to support FilterDuplicateKeys option
        final List<IHistoricalEntry<V>> trailing = newEntriesList(shiftBackUnits);
        //With 10 units this iterates from 9 to 0
        //to go from past to future so that queries get minimized
        //only on the first call we risk multiple queries
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        for (int unitsBack = shiftBackUnits - 1; unitsBack >= 0 && !impl.iterationFinished(); unitsBack--) {
            final IHistoricalEntry<V> value = impl.getResult();
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
    public ICloseableIterable<IHistoricalEntry<V>> getNextEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftForwardUnits) {
        //This has to work with lists internally to support FilterDuplicateKeys option
        final List<IHistoricalEntry<V>> trailing = newEntriesList(shiftForwardUnits);
        //With 10 units this iterates from 9 to 0
        //to go from past to future so that queries get minimized
        //only on the first call we risk multiple queries
        final GetNextEntryQueryImpl<V> impl = new GetNextEntryQueryImpl<V>(this, query, key, shiftForwardUnits);
        for (int unitsBack = shiftForwardUnits - 1; unitsBack >= 0 && !impl.iterationFinished(); unitsBack--) {
            final IHistoricalEntry<V> value = impl.getResult();
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
    public IHistoricalEntry<V> getNextEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
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

    @Override
    public IHistoricalEntry<V> computeEntry(final HistoricalCacheQuery<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        final IEvaluateGenericFDate<IHistoricalEntry<V>> computeEntryF = parent.newComputeEntry();
        IHistoricalEntry<V> entry = computeEntryF.evaluateGeneric(key);
        if (entry != null) {
            final IHistoricalCacheQueryElementFilter<V> elementFilter = query.getElementFilter();
            if (elementFilter != null && !(elementFilter instanceof DisabledHistoricalCacheQueryElementFilter)) {
                FDate entryKey = entry.getKey();
                while (!elementFilter.isValid(entryKey, entry.getValue())) {
                    entry = null;
                    //try earlier dates to find a valid element
                    final FDate previousKey = parent.calculatePreviousKey(entryKey);
                    final IHistoricalEntry<V> previousEntry = computeEntryF.evaluateGeneric(previousKey);
                    if (previousEntry == null) {
                        break;
                    }
                    if (previousEntry.getKey().equals(entryKey)) {
                        break;
                    }
                    entry = previousEntry;
                    entryKey = previousEntry.getKey();
                }
            }
        }
        return assertValue.assertValue(parent, key, entry);
    }

    @Override
    public void putPrevious(final FDate previousKey, final V value, final FDate valueKey) {
        //ignore
    }

    @Override
    public void putPreviousKey(final FDate previousKey, final FDate valueKey) {
        //ignore
    }

}
