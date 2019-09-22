package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheShiftKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class DelegateHistoricalCacheShiftKeyProvider implements IHistoricalCacheShiftKeyProvider {

    private final AHistoricalCache<Object> parent;
    private final IHistoricalCacheShiftKeyProvider delegate;
    private final IHistoricalCacheQuery<?> parentQuery;

    private DelegateHistoricalCacheShiftKeyProvider(final AHistoricalCache<Object> parent,
            final IHistoricalCacheShiftKeyProvider delegate) {
        this.parent = parent;
        this.delegate = delegate;
        this.parentQuery = parent.query();
    }

    @Override
    public AHistoricalCache<?> getParent() {
        return parent;
    }

    @Override
    public FDate calculatePreviousKey(final FDate key) {
        return delegate.calculatePreviousKey(key);
    }

    @Override
    public FDate calculateNextKey(final FDate key) {
        return delegate.calculateNextKey(key);
    }

    @Override
    public void clear() {
        parent.clear();
    }

    @Override
    public ILoadingCache<FDate, FDate> getPreviousKeysCache() {
        return delegate.getPreviousKeysCache();
    }

    @Override
    public ILoadingCache<FDate, FDate> getNextKeysCache() {
        return delegate.getNextKeysCache();
    }

    @Override
    public IHistoricalCacheQuery<?> newKeysQueryInterceptor() {
        return parentQuery;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> DelegateHistoricalCacheShiftKeyProvider maybeWrap(final AHistoricalCache delegate) {
        if (delegate.getShiftKeyProvider() instanceof DelegateHistoricalCacheShiftKeyProvider) {
            final DelegateHistoricalCacheShiftKeyProvider provider = (DelegateHistoricalCacheShiftKeyProvider) delegate
                    .getShiftKeyProvider();
            //delegate directly without going the multiple layers, though remember which parent we were delegating so that traversion works properly
            return new DelegateHistoricalCacheShiftKeyProvider(delegate, provider.delegate);
        } else {
            return new DelegateHistoricalCacheShiftKeyProvider(delegate, delegate.getShiftKeyProvider());
        }
    }

    @Override
    public boolean isChildRefreshRequested(final AHistoricalCache<?> child) {
        return delegate.isChildRefreshRequested(child);
    }

}
