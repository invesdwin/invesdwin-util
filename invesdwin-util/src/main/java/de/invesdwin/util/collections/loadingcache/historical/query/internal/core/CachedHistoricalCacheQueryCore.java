package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.impl.GetPreviousEntryQueryImpl;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class CachedHistoricalCacheQueryCore<V> implements IHistoricalCacheQueryCore<V> {

    private final DefaultHistoricalCacheQueryCore<V> delegate;
    @GuardedBy("this")
    private Integer maximumSize;
    @GuardedBy("this")
    private final List<Entry<FDate, V>> cachedEntries = new ArrayList<Entry<FDate, V>>();
    @GuardedBy("this")
    private FDate cachedEntriesKey = null;

    public CachedHistoricalCacheQueryCore(final IHistoricalCacheInternalMethods<V> parent) {
        this.delegate = new DefaultHistoricalCacheQueryCore<V>(parent);
        this.maximumSize = parent.getMaximumSize();
    }

    @Override
    public IHistoricalCacheInternalMethods<V> getParent() {
        return delegate.getParent();
    }

    @Override
    public Entry<FDate, V> getPreviousEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftBackUnits) {
        //TODO use cache here
        //TODO increase maximum size if shiftbackunits is larger than it
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, key, shiftBackUnits);
        //CHECKSTYLE:OFF
        while (!impl.iterationFinished()) {
            //noop
        }
        //CHECKSTYLE:ON
        return impl.getResult();
    }

    @Override
    public synchronized ICloseableIterable<Entry<FDate, V>> getPreviousEntries(
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        //TODO increase maximum size if shiftbackunits is larger than it
        final FDate adjKey = getParent().adjustKey(key);
        final List<Entry<FDate, V>> result;
        if (!cachedEntries.isEmpty()) {
            result = cachedGetPreviousEntries(query, shiftBackUnits, adjKey);
        } else {
            result = defaultGetPreviousEntries(query, shiftBackUnits, adjKey);
        }
        return WrapperCloseableIterable.maybeWrap(result);
    }

    private List<Entry<FDate, V>> cachedGetPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate adjKey) {
        final List<Entry<FDate, V>> trailing = query.newEntriesList();
        final int skipFirstValueIncrement = 1;
        int unitsBack = shiftBackUnits - skipFirstValueIncrement;
        if (Objects.equals(adjKey, cachedEntriesKey) || Objects.equals(adjKey, getLastCachedEntry().getKey())) {
            unitsBack = fillFromCacheAsFarAsPossible(trailing, unitsBack);
            if (unitsBack == -1) {
                //we could satisfy the query completely with cached values
                Collections.reverse(trailing);
                //cached values don't have to be updated
                //                System.out.println("complete");
                return trailing;
            } else {
                if (unitsBack < 0) {
                    throw new IllegalStateException("unitsBack should not become smaller than -1: " + unitsBack);
                }
                final FDate lastTrailingKey = trailing.get(trailing.size() - skipFirstValueIncrement).getKey();
                //we need to load further values from the map
                final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, lastTrailingKey,
                        unitsBack + skipFirstValueIncrement);
                impl.setIterations(skipFirstValueIncrement);
                while (unitsBack >= 0 && !impl.iterationFinished()) {
                    final Entry<FDate, V> value = impl.getResult();
                    if (value != null) {
                        trailing.add(value);
                    } else {
                        break;
                    }
                    unitsBack--;
                }
                Collections.reverse(trailing);
                replaceCachedEntries(adjKey, trailing);
                //                System.out.println("trailing");
                return trailing;
            }
        }
        //        System.out.println("default");
        return defaultGetPreviousEntries(query, shiftBackUnits, adjKey);
    }

    private List<Entry<FDate, V>> defaultGetPreviousEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final int shiftBackUnits, final FDate adjKey) {
        final List<Entry<FDate, V>> trailing = query.newEntriesList();
        final GetPreviousEntryQueryImpl<V> impl = new GetPreviousEntryQueryImpl<V>(this, query, adjKey, shiftBackUnits);
        for (int unitsBack = shiftBackUnits - 1; unitsBack >= 0 && !impl.iterationFinished(); unitsBack--) {
            final Entry<FDate, V> value = impl.getResult();
            if (value != null) {
                trailing.add(value);
            } else {
                break;
            }
        }
        Collections.reverse(trailing);
        replaceCachedEntries(adjKey, trailing);
        return trailing;
    }

    private void replaceCachedEntries(final FDate adjKey, final List<Entry<FDate, V>> trailing) {
        cachedEntries.clear();
        cachedEntries.addAll(trailing);
        cachedEntriesKey = adjKey;
    }

    private int fillFromCacheAsFarAsPossible(final List<Entry<FDate, V>> trailing, final int unitsBack) {
        //prefill what is possible and add suffixes by query as needed
        int cachedIndex = cachedEntries.size() - 1;
        int newUnitsBack = unitsBack;
        while (newUnitsBack >= 0 && cachedIndex >= 0) {
            trailing.add(cachedEntries.get(cachedIndex));
            cachedIndex--;
            newUnitsBack--;
        }
        return newUnitsBack;
    }

    private Entry<FDate, V> getLastCachedEntry() {
        return cachedEntries.get(cachedEntries.size() - 1);
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getNextEntries(final IHistoricalCacheQueryInternalMethods<V> query,
            final FDate key, final int shiftForwardUnits) {
        return delegate.getNextEntries(query, key, shiftForwardUnits);
    }

    @Override
    public Entry<FDate, V> getNextEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final int shiftForwardUnits) {
        return delegate.getNextEntry(query, key, shiftForwardUnits);
    }

    @Override
    public synchronized void clear() {
        delegate.clear();
        cachedEntries.clear();
        cachedEntriesKey = null;
    }

    @Override
    public synchronized void increaseMaximumSize(final int maximumSize) {
        this.maximumSize = maximumSize;
    }

    @Override
    public V getValue(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return delegate.getValue(query, key, assertValue);
    }

    @Override
    public Entry<FDate, V> getEntry(final IHistoricalCacheQueryInternalMethods<V> query, final FDate key,
            final HistoricalCacheAssertValue assertValue) {
        return delegate.getEntry(query, key, assertValue);
    }

    @Override
    public HistoricalCacheQuery<V> newQuery() {
        return new HistoricalCacheQuery<V>(this);
    }

}
