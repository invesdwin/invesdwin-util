package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import java.util.Map.Entry;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.collections.iterable.ATransformingCloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class AHistoricalCacheQueryInterceptor<V> implements IHistoricalCacheQueryInterceptor<V> {

    private final AHistoricalCache<V> parent;

    public AHistoricalCacheQueryInterceptor(final AHistoricalCache<V> parent) {
        this.parent = parent;
    }

    @Override
    public ICloseableIterable<FDate> getKeys(final FDate from, final FDate to) {
        return new ICloseableIterable<FDate>() {
            @Override
            public ACloseableIterator<FDate> iterator() {
                return new ACloseableIterator<FDate>() {
                    private final ACloseableIterator<Entry<FDate, V>> entriesIterator = getEntries(from, to).iterator();

                    @Override
                    protected boolean innerHasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    protected FDate innerNext() {
                        final Entry<FDate, V> next = entriesIterator.next();
                        if (next == null) {
                            return null;
                        } else {
                            return parent.extractKey(next.getKey(), next.getValue());
                        }
                    }

                    @Override
                    protected void innerClose() {
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
