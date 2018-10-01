package de.invesdwin.util.collections.loadingcache.historical.query.index;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class IndexedFDate extends FDate {

    private IdentityQueryCoreIndex indexFalse;
    private IdentityQueryCoreIndex indexTrue;
    private boolean indexesRoundRobin;
    private int extractKeyProviderHashCode = 0;
    private FDate extractKeyProviderExtractedKey;

    @SuppressWarnings("deprecation")
    public IndexedFDate(final FDate key) {
        super(key);
        if (key instanceof IndexedFDate) {
            final IndexedFDate indexedKey = (IndexedFDate) key;
            this.indexFalse = indexedKey.indexFalse;
            this.indexTrue = indexedKey.indexTrue;
            this.indexesRoundRobin = indexedKey.indexesRoundRobin;
        } else {
            key.setExtension(this);
            this.indexesRoundRobin = true;
        }
    }

    @SuppressWarnings("deprecation")
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

    @SuppressWarnings("deprecation")
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

    public synchronized QueryCoreIndex getQueryCoreIndex(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore<?> queryCore) {
        if (indexFalse == null) {
            return null;
        }
        final int identityHashCode = queryCore.hashCode();
        if (indexFalse.getQueryCoreIdentityHashCode() == identityHashCode) {
            return indexFalse;
        }
        if (indexTrue == null) {
            return null;
        }
        if (indexTrue.getQueryCoreIdentityHashCode() == identityHashCode) {
            return indexTrue;
        }
        return null;
    }

    public synchronized void putQueryCoreIndex(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore<?> queryCore,
            final QueryCoreIndex queryCoreIndex) {
        final int identityHashCode = queryCore.hashCode();
        final IdentityQueryCoreIndex identityQueryCoreIndex = new IdentityQueryCoreIndex(identityHashCode,
                queryCoreIndex);
        put(identityQueryCoreIndex);
    }

    private void put(final IdentityQueryCoreIndex identityQueryCoreIndex) {
        if (!update(identityQueryCoreIndex)) {
            if (indexesRoundRobin) {
                indexFalse = identityQueryCoreIndex;
                indexesRoundRobin = false;
            } else {
                indexTrue = identityQueryCoreIndex;
                indexesRoundRobin = true;
            }
        }
    }

    private boolean update(final IdentityQueryCoreIndex identityQueryCoreIndex) {
        if (indexFalse == null) {
            return false;
        }
        if (indexFalse.getQueryCoreIdentityHashCode() == identityQueryCoreIndex.getQueryCoreIdentityHashCode()) {
            indexFalse = identityQueryCoreIndex;
            indexesRoundRobin = false;
            return true;
        }
        if (indexTrue == null) {
            return false;
        }
        if (indexTrue.getQueryCoreIdentityHashCode() == identityQueryCoreIndex.getQueryCoreIdentityHashCode()) {
            indexTrue = identityQueryCoreIndex;
            indexesRoundRobin = true;
            return true;
        }
        return false;
    }

    public <V> FDate maybeExtractKey(
            final de.invesdwin.util.collections.loadingcache.historical.key.internal.IHistoricalCacheExtractKeyProvider<V> extractKeyProvider,
            final IHistoricalCacheAdjustKeyProvider adjustKeyProvider, final V value) {

        final int identityHashCode = extractKeyProvider.hashCode();
        if (identityHashCode != extractKeyProviderHashCode) {
            if (value instanceof IHistoricalEntry) {
                final IHistoricalEntry<?> cValue = (IHistoricalEntry<?>) value;
                extractKeyProviderExtractedKey = cValue.getKey();
            } else if (value instanceof IHistoricalValue) {
                final IHistoricalValue<?> cValue = (IHistoricalValue<?>) value;
                extractKeyProviderExtractedKey = cValue.asHistoricalEntry().getKey();
            } else {
                extractKeyProviderExtractedKey = extractKeyProvider.extractKey(this, value);
            }
            if (extractKeyProviderExtractedKey.equalsNotNullSafe(this)) {
                extractKeyProviderExtractedKey = this;
            }
            extractKeyProviderExtractedKey = adjustKeyProvider.maybeAdjustKey(extractKeyProviderExtractedKey);
            extractKeyProviderHashCode = identityHashCode;
        }
        return extractKeyProviderExtractedKey;
    }

}
