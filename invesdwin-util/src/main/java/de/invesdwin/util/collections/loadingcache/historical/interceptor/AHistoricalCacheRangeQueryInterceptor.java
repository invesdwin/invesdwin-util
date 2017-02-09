package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import java.util.Map.Entry;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ATransformingCloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.time.fdate.FDate;

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
                    private final ICloseableIterator<Entry<FDate, V>> entriesIterator = getEntries(from, to).iterator();

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public FDate next() {
                        final Entry<FDate, V> next = entriesIterator.next();
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

    @Override
    public final ICloseableIterable<Entry<FDate, V>> getEntries(final FDate from, final FDate to) {
        final ICloseableIterable<Entry<FDate, V>> innerGetEntries = innerGetEntries(from, to);
        return new ATransformingCloseableIterable<Entry<FDate, V>, Entry<FDate, V>>(innerGetEntries) {

            private Entry<FDate, V> prevEntry;

            @Override
            protected Entry<FDate, V> transform(final Entry<FDate, V> newEntry) {
                //fill cache for faster prev/next lookups
                parent.put(newEntry, prevEntry);
                prevEntry = newEntry;
                return newEntry;
            }
        };
    }

    protected abstract ICloseableIterable<Entry<FDate, V>> innerGetEntries(final FDate from, final FDate to);

}
