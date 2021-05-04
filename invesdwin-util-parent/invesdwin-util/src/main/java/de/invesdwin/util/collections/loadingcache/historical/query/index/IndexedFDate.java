package de.invesdwin.util.collections.loadingcache.historical.query.index;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.IFDateProvider;

@NotThreadSafe
public class IndexedFDate extends FDate {

    private transient IdentityQueryCoreIndex indexFalse;
    private transient IdentityQueryCoreIndex indexTrue;
    private transient boolean indexesRoundRobin;
    private transient int extractKeyProviderHashCode;
    private transient FDate extractKeyProviderExtractedKey;

    @SuppressWarnings("deprecation")
    public IndexedFDate(final FDate key) {
        super(key);
        if (key instanceof IndexedFDate) {
            final IndexedFDate indexedKey = (IndexedFDate) key;
            this.indexFalse = indexedKey.indexFalse;
            this.indexTrue = indexedKey.indexTrue;
            this.indexesRoundRobin = indexedKey.indexesRoundRobin;
            this.extractKeyProviderHashCode = indexedKey.extractKeyProviderHashCode;
            this.extractKeyProviderExtractedKey = indexedKey.extractKeyProviderExtractedKey;
        } else {
            final Object existingExtension = key.getExtension();
            //don't replace an existing extension
            if (existingExtension == null || existingExtension instanceof IndexedFDate) {
                key.setExtension(this);
            } else {
                setExtension(existingExtension);
            }
            this.indexesRoundRobin = true;
        }
    }

    public static IndexedFDate maybeWrap(final IFDateProvider pKey) {
        if (pKey instanceof IndexedFDate) {
            return (IndexedFDate) pKey;
        } else {
            final FDate key = pKey.asFDate();
            if (key instanceof IndexedFDate) {
                return (IndexedFDate) key;
            } else {
                final IndexedFDate indexedFDate = getExtension(key);
                if (indexedFDate != null) {
                    return indexedFDate;
                } else {
                    return new IndexedFDate(key);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static IndexedFDate getExtension(final FDate key) {
        final Object extension = key.getExtension();
        if (extension instanceof IndexedFDate) {
            return (IndexedFDate) extension;
        } else {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public static IndexedFDate maybeUnwrap(final FDate key) {
        if (key instanceof IndexedFDate) {
            return (IndexedFDate) key;
        } else {
            final IndexedFDate indexedFDate = getExtension(key);
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
        if (value == null) {
            return null;
        }
        final int identityHashCode = extractKeyProvider.hashCode();
        if (identityHashCode != extractKeyProviderHashCode) {
            extractKeyProviderExtractedKey = extractKeyProvider.extractKey(this, value);
            if (extractKeyProviderExtractedKey == null) {
                throw new IllegalStateException("extractKeyProviderExtractedKey should not be null");
            }
            extractKeyProviderHashCode = identityHashCode;
        }
        return extractKeyProviderExtractedKey;
    }

    public IndexedFDate putExtractedKey(
            final de.invesdwin.util.collections.loadingcache.historical.key.internal.IHistoricalCacheExtractKeyProvider<?> extractKeyProvider,
            final IHistoricalCacheAdjustKeyProvider adjustKeyProvider) {
        final int identityHashCode = extractKeyProvider.hashCode();
        if (extractKeyProviderHashCode != identityHashCode) {
            extractKeyProviderExtractedKey = adjustKeyProvider.newAlreadyAdjustedKey(this);
            if (extractKeyProviderExtractedKey == null) {
                throw new IllegalStateException("extractKeyProviderExtractedKey should not be null");
            }
            extractKeyProviderHashCode = identityHashCode;
        }
        return this;
    }

}
