package de.invesdwin.util.collections.loadingcache.historical.query.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class IndexedFDate extends FDate {

    private transient Map<Integer, QueryCoreIndex> queryCoreIndexMap;

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

    public synchronized Map<Integer, QueryCoreIndex> getQueryCoreIndexMap() {
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

    private static Map<Integer, QueryCoreIndex> newQueryCoreIndexMap() {
        return Collections.synchronizedMap(new HashMap<>());
    }

}
