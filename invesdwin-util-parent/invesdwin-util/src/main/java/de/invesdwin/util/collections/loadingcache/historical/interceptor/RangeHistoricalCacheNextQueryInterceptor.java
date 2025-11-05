package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.impl.ShiftForwardUnitsLoop;
import de.invesdwin.util.collections.loadingcache.historical.query.impl.ShiftForwardUnitsLoopIterator;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@Immutable
public class RangeHistoricalCacheNextQueryInterceptor<V> implements IHistoricalCacheNextQueryInterceptor<V> {

    private final IHistoricalCacheRangeQueryInterceptor<V> rangeQueryInterceptor;

    public RangeHistoricalCacheNextQueryInterceptor(
            final IHistoricalCacheRangeQueryInterceptor<V> rangeQueryInterceptor) {
        this.rangeQueryInterceptor = rangeQueryInterceptor;
    }

    @Override
    public Optional<? extends IHistoricalEntry<V>> getNextEntry(final FDate key, final int shiftForwardUnits) {
        final FDate to = getNextKeyForRangeTo(key, shiftForwardUnits);
        final ICloseableIterable<? extends IHistoricalEntry<V>> delegate = rangeQueryInterceptor.getEntries(key, to);
        if (delegate == null) {
            return null;
        }
        final ShiftForwardUnitsLoop<IHistoricalEntry<V>> loop = new ShiftForwardUnitsLoop<IHistoricalEntry<V>>(key,
                shiftForwardUnits, IHistoricalEntry::getKey);
        loop.loop(delegate);
        return Optional.ofNullable(loop.getNextValue());
    }

    @Override
    public ICloseableIterable<? extends IHistoricalEntry<V>> getNextEntries(final FDate key,
            final int shiftForwardUnits) {
        //it would be better if the iterator call would do the to range query check, but we can not return null in there
        final FDate to = getNextKeyForRangeTo(key, shiftForwardUnits);
        final ICloseableIterable<? extends IHistoricalEntry<V>> delegate = rangeQueryInterceptor.getEntries(key, to);
        if (delegate == null) {
            return null;
        }
        return new ICloseableIterable<IHistoricalEntry<V>>() {
            @Override
            public ICloseableIterator<IHistoricalEntry<V>> iterator() {
                return new ShiftForwardUnitsLoopIterator<IHistoricalEntry<V>>(key, shiftForwardUnits,
                        IHistoricalEntry::getKey, delegate.iterator());
            }
        };
    }

    /**
     * Override this to provide a more sensible range to. Though FDates.MAX should be ok in all cases and doing another
     * query for this here would just cause additional overhead. Thus it is not recommended to override this method.
     */
    protected FDate getNextKeyForRangeTo(final FDate key, final int shiftForwardUnits) {
        return FDates.MAX_DATE;
    }
}
