package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.impl.ShiftForwardUnitsLoop;
import de.invesdwin.util.collections.loadingcache.historical.query.impl.ShiftForwardUnitsLoopIterator;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@Immutable
public class DefaultHistoricalCacheNextQueryInterceptor<V> implements IHistoricalCacheNextQueryInterceptor<V> {

    private final IHistoricalCacheQuery<V> parentQuery;

    public DefaultHistoricalCacheNextQueryInterceptor(final IHistoricalCacheQuery<V> parentQuery) {
        this.parentQuery = parentQuery;
    }

    @Override
    public Optional<? extends IHistoricalEntry<V>> getNextEntry(final FDate key, final int shiftForwardUnits) {
        final FDate to = innerGetNextKey(key, shiftForwardUnits);
        final ICloseableIterable<? extends IHistoricalEntry<V>> delegate = parentQuery.getEntries(key, to);
        final ShiftForwardUnitsLoop<IHistoricalEntry<V>> loop = new ShiftForwardUnitsLoop<IHistoricalEntry<V>>(key,
                shiftForwardUnits, IHistoricalEntry::getKey);
        loop.loop(delegate);
        return Optional.ofNullable(loop.getNextValue());
    }

    @Override
    public ICloseableIterable<? extends IHistoricalEntry<V>> getNextEntries(final FDate key,
            final int shiftForwardUnits) {
        return new ICloseableIterable<IHistoricalEntry<V>>() {
            @Override
            public ICloseableIterator<IHistoricalEntry<V>> iterator() {
                final FDate to = innerGetNextKey(key, shiftForwardUnits);
                final ICloseableIterable<? extends IHistoricalEntry<V>> delegate = parentQuery.getEntries(key, to);
                return new ShiftForwardUnitsLoopIterator<IHistoricalEntry<V>>(key, shiftForwardUnits,
                        IHistoricalEntry::getKey, delegate.iterator());
            }
        };
    }

    protected FDate innerGetNextKey(final FDate key, final int shiftForwardUnits) {
        return FDates.MAX_DATE;
    }
}
