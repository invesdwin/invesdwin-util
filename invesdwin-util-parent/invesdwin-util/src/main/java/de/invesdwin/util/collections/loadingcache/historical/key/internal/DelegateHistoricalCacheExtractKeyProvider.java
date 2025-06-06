package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.IHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.IFDateProvider;

@Immutable
public final class DelegateHistoricalCacheExtractKeyProvider<V> implements IHistoricalCacheExtractKeyProvider<V> {

    private final IHistoricalCache<Object> delegate;
    private final IHistoricalCacheQueryWithFuture<Object> delegateQueryWithFuture;
    private final int hashCode;

    private DelegateHistoricalCacheExtractKeyProvider(final IHistoricalCache<Object> delegate) {
        this.delegate = delegate;
        this.delegateQueryWithFuture = delegate.query().setFutureEnabled();
        this.hashCode = delegate.getExtractKeyProvider().hashCode();
    }

    public IHistoricalCache<Object> getDelegate() {
        return delegate;
    }

    @Override
    public FDate extractKey(final IFDateProvider pKey, final V value) {
        if (value == null) {
            return null;
        }
        final FDate key = pKey.asFDate();
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
    public static <T> DelegateHistoricalCacheExtractKeyProvider<T> maybeWrap(final IHistoricalCache shiftKeyDelegate) {
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
        return hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        return delegate.equals(obj);
    }

}
