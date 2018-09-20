package de.invesdwin.util.collections.loadingcache.historical.query.index;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.fdate.FDate;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@NotThreadSafe
public class IndexedFDate extends FDate {

    public static final String KEY_INDEXED_FDATE = IndexedFDate.class.getSimpleName();
    private transient Int2ObjectOpenHashMap<QueryCoreIndex> queryCoreIndexMap;

    public IndexedFDate(final FDate key) {
        super(key);
        if (key instanceof IndexedFDate) {
            final IndexedFDate indexedKey = (IndexedFDate) key;
            queryCoreIndexMap = indexedKey.getQueryCoreIndexMap();
        } else {
            key.getAttributes().put(KEY_INDEXED_FDATE, this);
        }
    }

    public static IndexedFDate maybeWrap(final FDate key) {
        if (key instanceof IndexedFDate) {
            return (IndexedFDate) key;
        } else {
            final IndexedFDate indexedFDate = (IndexedFDate) key.getAttributes().get(KEY_INDEXED_FDATE);
            if (indexedFDate != null) {
                return indexedFDate;
            } else {
                return new IndexedFDate(key);
            }
        }
    }

    public static IndexedFDate maybeUnwrap(final FDate key) {
        if (key instanceof IndexedFDate) {
            return (IndexedFDate) key;
        } else {
            final IndexedFDate indexedFDate = (IndexedFDate) key.getAttributes().get(KEY_INDEXED_FDATE);
            if (indexedFDate != null) {
                return indexedFDate;
            } else {
                return null;
            }
        }
    }

    public synchronized Int2ObjectOpenHashMap<QueryCoreIndex> getQueryCoreIndexMap() {
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

    public static FDate maybeMerge(final FDate from, final FDate to, final int adjustIndex) {
        IndexedFDate fromIndexedKey = IndexedFDate.maybeUnwrap(from);
        if (fromIndexedKey == null) {
            final IndexedFDate toIndexedKey = maybeUnwrap(to);
            if (toIndexedKey != null) {
                fromIndexedKey = IndexedFDate.maybeWrap(from);
                mergeMap(toIndexedKey, fromIndexedKey, -adjustIndex);
            }
            return to;
        } else {
            final IndexedFDate toIndexedKey = IndexedFDate.maybeWrap(to);
            mergeMap(fromIndexedKey, toIndexedKey, adjustIndex);
            mergeMap(toIndexedKey, fromIndexedKey, -adjustIndex);
            return toIndexedKey;
        }
    }

    private static void mergeMap(final IndexedFDate from, final IndexedFDate to, final int adjustIndex) {
        final Int2ObjectOpenHashMap<QueryCoreIndex> fromMap = from.getQueryCoreIndexMap();
        for (final Int2ObjectMap.Entry<QueryCoreIndex> fromEntry : fromMap.int2ObjectEntrySet()) {
            final int queryCoreIdentityHashCode = fromEntry.getIntKey();
            final QueryCoreIndex fromQueryCoreIndex = fromEntry.getValue();
            final Int2ObjectOpenHashMap<QueryCoreIndex> toMap = to.getQueryCoreIndexMap();
            final QueryCoreIndex toQueryCoreIndex = toMap.get(queryCoreIdentityHashCode);
            if (toQueryCoreIndex != null) {
                if (toQueryCoreIndex.getModCount() < fromQueryCoreIndex.getModCount()) {
                    toMap.put(queryCoreIdentityHashCode, new QueryCoreIndex(fromQueryCoreIndex.getModCount(),
                            fromQueryCoreIndex.getIndex() + adjustIndex));
                }
            } else {
                toMap.put(queryCoreIdentityHashCode, new QueryCoreIndex(fromQueryCoreIndex.getModCount(),
                        fromQueryCoreIndex.getIndex() + adjustIndex));
            }
        }
    }

}
