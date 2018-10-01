package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class DelegateHistoricalCacheExtractKeyProvider<V> implements IHistoricalCacheExtractKeyProvider<V> {

    private final AHistoricalCache<Object> delegate;
    private final IHistoricalCacheQueryWithFuture<Object> delegateQueryWithFuture;

    private DelegateHistoricalCacheExtractKeyProvider(final AHistoricalCache<Object> delegate) {
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> DelegateHistoricalCacheExtractKeyProvider<T> maybeWrap(final AHistoricalCache shiftKeyDelegate) {
        if (shiftKeyDelegate.getExtractKeyProvider() instanceof DelegateHistoricalCacheExtractKeyProvider) {
            final DelegateHistoricalCacheExtractKeyProvider extractKeyProvider = (DelegateHistoricalCacheExtractKeyProvider) shiftKeyDelegate
                    .getExtractKeyProvider();
            if (extractKeyProvider.delegate.getAdjustKeyProvider()
                    .getParent() == shiftKeyDelegate.getAdjustKeyProvider().getParent()) {
                return extractKeyProvider;
            } else {
                return new DelegateHistoricalCacheExtractKeyProvider<T>(shiftKeyDelegate);
            }
        } else {
            return new DelegateHistoricalCacheExtractKeyProvider<T>(shiftKeyDelegate);
        }
    }

    @Override
    public int hashCode() {
        return delegate.getExtractKeyProvider().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return delegate.equals(obj);
    }

}
