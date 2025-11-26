package de.invesdwin.util.collections.loadingcache.historical.key;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.ANonRecursivePullingHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.ARecursivePullingHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
public abstract class APullingHistoricalCacheAdjustKeyProvider implements IHistoricalCacheAdjustKeyProvider {

    private final IHistoricalCacheAdjustKeyProvider delegate;

    public APullingHistoricalCacheAdjustKeyProvider(final AHistoricalCache<?> parent) {
        if (isPullingRecursive()) {
            this.delegate = new ARecursivePullingHistoricalCacheAdjustKeyProvider(parent) {
                @Override
                protected FDate innerGetHighestAllowedKey() {
                    return APullingHistoricalCacheAdjustKeyProvider.this.innerGetHighestAllowedKey();
                }
            };
        } else {
            this.delegate = new ANonRecursivePullingHistoricalCacheAdjustKeyProvider(parent) {
                @Override
                protected FDate innerGetHighestAllowedKey() {
                    return APullingHistoricalCacheAdjustKeyProvider.this.innerGetHighestAllowedKey();
                }
            };
        }
    }

    protected abstract FDate innerGetHighestAllowedKey();

    protected abstract boolean isPullingRecursive();

    @Override
    public FDate adjustKey(final FDate key) {
        return delegate.adjustKey(key);
    }

    @Override
    public FDate maybeAdjustKey(final FDate key) {
        return delegate.maybeAdjustKey(key);
    }

    @Override
    public boolean isAdjustedKey(final FDate key) {
        return delegate.isAdjustedKey(key);
    }

    @Override
    public FDate newAlreadyAdjustedKey(final FDate key) {
        return delegate.newAlreadyAdjustedKey(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public FDate getHighestAllowedKey(final boolean update) {
        return delegate.getHighestAllowedKey(update);
    }

    @Override
    public FDate getPreviousHighestAllowedKey() {
        return delegate.getPreviousHighestAllowedKey();
    }

    @Override
    public boolean registerHistoricalCache(final AHistoricalCache<?> historicalCache) {
        return delegate.registerHistoricalCache(historicalCache);
    }

    @Override
    public AHistoricalCache<?> getParent() {
        return delegate.getParent();
    }

    @Override
    public <T> IHistoricalCacheQuery<T> newQuery(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods<T> queryCore) {
        return delegate.newQuery(queryCore);
    }

    @Override
    public boolean isAlreadyAdjustingKey() {
        return delegate.isAlreadyAdjustingKey();
    }

    public static boolean isGlobalAlreadyAdjustingKey() {
        return ARecursivePullingHistoricalCacheAdjustKeyProvider.isGlobalAlreadyAdjustingKey();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this || delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

}
