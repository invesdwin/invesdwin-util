package de.invesdwin.util.collections.loadingcache.historical.query.index;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class IndexedFDate extends FDate {

    private int queryCoreIdentityHashCode = Integer.MIN_VALUE;
    private QueryCoreIndex queryCoreIndex;

    public IndexedFDate(final FDate key) {
        super(key);
        if (key instanceof IndexedFDate) {
            final IndexedFDate indexedKey = (IndexedFDate) key;
            this.queryCoreIdentityHashCode = indexedKey.queryCoreIdentityHashCode;
            this.queryCoreIndex = indexedKey.queryCoreIndex;
        }
    }

    public static IndexedFDate maybeWrap(final FDate key) {
        if (key instanceof IndexedFDate) {
            return (IndexedFDate) key;
        } else {
            return new IndexedFDate(key);
        }
    }

    public QueryCoreIndex getQueryCoreIndex(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore<?> queryCore) {
        final int identityHashCode = System.identityHashCode(queryCore);
        if (identityHashCode != queryCoreIdentityHashCode) {
            return null;
        } else {
            return queryCoreIndex;
        }
    }

    public void putQueryCoreIndex(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore<?> queryCore,
            final QueryCoreIndex queryCoreIndex) {
        this.queryCoreIdentityHashCode = System.identityHashCode(queryCore);
        this.queryCoreIndex = queryCoreIndex;
    }

}
