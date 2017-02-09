package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class DelegateHistoricalCacheExtractKeyProvider<V> implements IHistoricalCacheExtractKeyProvider<V> {

    private final AHistoricalCache<Object> delegate;
    private final IHistoricalCacheQueryWithFuture<Object> delegateQueryWithFuture;

    public DelegateHistoricalCacheExtractKeyProvider(final AHistoricalCache<Object> delegate) {
        this.delegate = delegate;
        this.delegateQueryWithFuture = delegate.query().withFuture();
    }

    @Override
    public FDate extractKey(final FDate key, final V value) {
        //might be a value key we got inside here which would obviouls already be adjusted
        final FDate adjKey = delegate.getAdjustKeyProvider().newAlreadyAdjustedKey(key);
        final Entry<FDate, Object> shiftKeysEntry = delegateQueryWithFuture.getEntry(adjKey);
        if (shiftKeysEntry != null) {
            return shiftKeysEntry.getKey();
        } else {
            return key;
        }
    }

}
