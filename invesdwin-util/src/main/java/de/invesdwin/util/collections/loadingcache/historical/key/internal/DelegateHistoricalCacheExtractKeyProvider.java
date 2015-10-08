package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class DelegateHistoricalCacheExtractKeyProvider<V> implements IHistoricalCacheExtractKeyProvider<V> {

    private final AHistoricalCache<Object> delegate;

    public DelegateHistoricalCacheExtractKeyProvider(final AHistoricalCache<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public FDate extractKey(final FDate key, final V value) {
        final Object shiftKeysValue = delegate.query().withFuture().getValue(key);
        if (shiftKeysValue != null) {
            return delegate.extractKey(key, shiftKeysValue);
        } else {
            return key;
        }
    }

}
