package de.invesdwin.util.collections.loadingcache.historical.query.index;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.fdate.FDate;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@NotThreadSafe
public class IndexedFDate extends FDate {

    private transient Int2ObjectOpenHashMap<QueryCoreIndex> queryCoreIndexMap;

    public IndexedFDate(final FDate key) {
        super(key);
        if (key instanceof IndexedFDate) {
            final IndexedFDate indexedKey = (IndexedFDate) key;
            queryCoreIndexMap = indexedKey.getQueryCoreIndexMap();
        }
    }

    public static IndexedFDate maybeWrap(final FDate key) {
        if (key instanceof IndexedFDate) {
            return (IndexedFDate) key;
        } else {
            return new IndexedFDate(key);
        }
    }

    private synchronized Int2ObjectOpenHashMap<QueryCoreIndex> getQueryCoreIndexMap() {
        if (queryCoreIndexMap == null) {
            queryCoreIndexMap = newQueryCoreIndexMap();
        }
        return queryCoreIndexMap;
    }

    public QueryCoreIndex getQueryCoreIndex(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore<?> queryCore) {
        return getQueryCoreIndexMap().get(System.identityHashCode(queryCore));
    }

    public void putQueryCoreIndex(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore<?> queryCore,
            final QueryCoreIndex queryCoreIndex) {
        getQueryCoreIndexMap().put(System.identityHashCode(queryCore), queryCoreIndex);
    }

    private static Int2ObjectOpenHashMap<QueryCoreIndex> newQueryCoreIndexMap() {
        return new Int2ObjectOpenHashMap<>();
    }

}
