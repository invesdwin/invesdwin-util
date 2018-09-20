package de.invesdwin.util.collections.loadingcache.historical.query.index;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.mutable.MutableInt;

import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class IndexedFDate extends FDate {

    private static final int INDEXES_SIZE = 2;
    private final IdentityQueryCoreIndex[] indexes;
    private final MutableInt indexesRoundRobin;

    public IndexedFDate(final FDate key) {
        super(key);
        if (key instanceof IndexedFDate) {
            final IndexedFDate indexedKey = (IndexedFDate) key;
            this.indexes = indexedKey.indexes;
            this.indexesRoundRobin = indexedKey.indexesRoundRobin;
        } else {
            key.setExtension(this);
            this.indexes = new IdentityQueryCoreIndex[INDEXES_SIZE];
            this.indexesRoundRobin = new MutableInt(-1);
        }
    }

    public static IndexedFDate maybeWrap(final FDate key) {
        if (key instanceof IndexedFDate) {
            return (IndexedFDate) key;
        } else {
            final IndexedFDate indexedFDate = (IndexedFDate) key.getExtension();
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
            final IndexedFDate indexedFDate = (IndexedFDate) key.getExtension();
            if (indexedFDate != null) {
                return indexedFDate;
            } else {
                return null;
            }
        }
    }

    public QueryCoreIndex getQueryCoreIndex(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore<?> queryCore) {
        final int identityHashCode = System.identityHashCode(queryCore);
        synchronized (indexes) {
            for (int i = 0; i < indexes.length; i++) {
                final IdentityQueryCoreIndex index = indexes[i];
                if (index == null) {
                    break;
                }
                if (index.getQueryCoreIdentityHashCode() == identityHashCode) {
                    return index;
                }
            }
        }
        return null;
    }

    public synchronized void putQueryCoreIndex(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore<?> queryCore,
            final QueryCoreIndex queryCoreIndex) {
        synchronized (indexes) {
            final int identityHashCode = System.identityHashCode(queryCore);
            final IdentityQueryCoreIndex identityQueryCoreIndex = new IdentityQueryCoreIndex(identityHashCode,
                    queryCoreIndex);
            putIdentityQueryCoreIndex(identityQueryCoreIndex);
        }
    }

    private void putIdentityQueryCoreIndex(final IdentityQueryCoreIndex identityQueryCoreIndex) {
        for (int i = 0; i < indexes.length; i++) {
            final IdentityQueryCoreIndex index = indexes[i];
            if (index == null) {
                break;
            }
            if (index.getQueryCoreIdentityHashCode() == identityQueryCoreIndex.getQueryCoreIdentityHashCode()) {
                indexes[i] = identityQueryCoreIndex;
                indexesRoundRobin.setValue(i);
                return;
            }
        }
        int curRoundRobin = indexesRoundRobin.intValue();
        if (curRoundRobin >= INDEXES_SIZE - 1) {
            curRoundRobin = 0;
        } else {
            curRoundRobin++;
        }
        indexes[curRoundRobin] = identityQueryCoreIndex;
        indexesRoundRobin.setValue(curRoundRobin);
    }

}
