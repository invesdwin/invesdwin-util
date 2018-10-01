package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
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
        FDate extractedKey;
        if (value instanceof IHistoricalEntry) {
            final IHistoricalEntry<?> cValue = (IHistoricalEntry<?>) value;
            extractedKey = cValue.getKey();
        } else if (value instanceof IHistoricalValue) {
            final IHistoricalValue<?> cValue = (IHistoricalValue<?>) value;
            extractedKey = cValue.asHistoricalEntry().getKey();
        } else {
            //might be a value key we got inside here which would obviously already be adjusted
            final FDate adjKey = delegate.getAdjustKeyProvider().newAlreadyAdjustedKey(key);
            final Entry<FDate, Object> shiftKeysEntry = delegateQueryWithFuture.getEntry(adjKey);
            if (shiftKeysEntry != null) {
                extractedKey = shiftKeysEntry.getKey();
            } else {
                return adjKey;
            }
        }
        if (extractedKey.equalsNotNullSafe(key)) {
            extractedKey = key;
        }
        extractedKey = delegate.getAdjustKeyProvider().newAlreadyAdjustedKey(extractedKey);
        return extractedKey;
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
