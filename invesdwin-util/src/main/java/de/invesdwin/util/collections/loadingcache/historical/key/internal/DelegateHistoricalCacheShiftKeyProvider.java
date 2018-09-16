package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class DelegateHistoricalCacheShiftKeyProvider implements IHistoricalCacheShiftKeyProvider {

    private final AHistoricalCache<Object> delegate;

    public DelegateHistoricalCacheShiftKeyProvider(final AHistoricalCache<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public AHistoricalCache<?> getParent() {
        return delegate;
    }

    @Override
    public FDate calculatePreviousKey(final FDate key) {
        return delegate.getShiftKeyProvider().calculatePreviousKey(key);
    }

    @Override
    public FDate calculateNextKey(final FDate key) {
        return delegate.getShiftKeyProvider().calculateNextKey(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public ILoadingCache<FDate, FDate> getPreviousKeysCache() {
        return delegate.getShiftKeyProvider().getPreviousKeysCache();
    }

    @Override
    public ILoadingCache<FDate, FDate> getNextKeysCache() {
        return delegate.getShiftKeyProvider().getNextKeysCache();
    }

    @Override
    public IHistoricalCacheQuery<?> newKeysQueryInterceptor() {
        return delegate.query();
    }

}
