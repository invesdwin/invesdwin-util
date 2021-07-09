package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
public abstract class AHistoricalCacheRangeQueryInterceptor<V> implements IHistoricalCacheRangeQueryInterceptor<V> {

    private final AHistoricalCache<V> parent;

    public AHistoricalCacheRangeQueryInterceptor(final AHistoricalCache<V> parent) {
        this.parent = parent;
    }

    @Override
    public ICloseableIterable<FDate> getKeys(final FDate from, final FDate to) {
        return new ICloseableIterable<FDate>() {
            @Override
            public ICloseableIterator<FDate> iterator() {
                return new ICloseableIterator<FDate>() {
                    private final ICloseableIterator<? extends IHistoricalEntry<V>> entriesIterator = getEntries(from,
                            to).iterator();

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public FDate next() {
                        final IHistoricalEntry<V> next = entriesIterator.next();
                        if (next == null) {
                            return null;
                        } else {
                            return next.getKey();
                        }
                    }

                    @Override
                    public void close() {
                        entriesIterator.close();
                    }
                };
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public final ICloseableIterable<IHistoricalEntry<V>> getEntries(final FDate from, final FDate to) {
        return (ICloseableIterable<IHistoricalEntry<V>>) innerGetEntries(from, to);
    }

    protected abstract ICloseableIterable<? extends IHistoricalEntry<V>> innerGetEntries(FDate from, FDate to);

}
