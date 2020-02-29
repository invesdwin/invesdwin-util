package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.ImmutableHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheShiftKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class DelegateHistoricalCacheShiftKeyProvider<V> implements IHistoricalCacheShiftKeyProvider<V> {

    private final de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods<V> parent;
    private final AHistoricalCache<V> delegateCache;
    private final IHistoricalCacheShiftKeyProvider<V> delegate;
    private final IHistoricalCacheQuery<?> delegateQuery;

    private DelegateHistoricalCacheShiftKeyProvider(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods<V> parent,
            final AHistoricalCache<V> delegateCache, final IHistoricalCacheShiftKeyProvider<V> delegate) {
        this.parent = parent;
        this.delegateCache = delegateCache;
        this.delegate = delegate;
        this.delegateQuery = delegateCache.query();
    }

    @Override
    public AHistoricalCache<?> getParent() {
        return delegateCache;
    }

    @Override
    public FDate calculatePreviousKey(final FDate key) {
        final FDate prevKey = delegate.calculatePreviousKey(key);
        final FDate adjKey = delegateCache.getAdjustKeyProvider().newAlreadyAdjustedKey(prevKey);
        return adjKey;
    }

    @Override
    public FDate calculateNextKey(final FDate key) {
        final FDate nextKey = delegate.calculateNextKey(key);
        final FDate adjKey = delegateCache.getAdjustKeyProvider().newAlreadyAdjustedKey(nextKey);
        return adjKey;
    }

    @Override
    public void clear() {
        delegateCache.clear();
    }

    @Override
    public IHistoricalCacheQuery<?> newKeysQueryInterceptor() {
        return delegateQuery;
    }

    @Override
    public IHistoricalEntry<V> put(final FDate previousKey, final FDate valueKey, final V value,
            final IHistoricalEntry<V> shiftKeyValueEntry, final FDate nextKey) {
        return delegate.put(previousKey, valueKey, null, shiftKeyValueEntry, nextKey);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> DelegateHistoricalCacheShiftKeyProvider<T> maybeWrap(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods<T> parent,
            final AHistoricalCache delegate) {
        if (delegate.getShiftKeyProvider() instanceof DelegateHistoricalCacheShiftKeyProvider) {
            final DelegateHistoricalCacheShiftKeyProvider provider = (DelegateHistoricalCacheShiftKeyProvider) delegate
                    .getShiftKeyProvider();
            //delegate directly without going the multiple layers, though remember which parent we were delegating so that traversion works properly
            return new DelegateHistoricalCacheShiftKeyProvider(parent, delegate, provider.delegate);
        } else {
            return new DelegateHistoricalCacheShiftKeyProvider(parent, delegate, delegate.getShiftKeyProvider());
        }
    }

    @Override
    public IHistoricalEntry<V> maybeWrap(final FDate key, final V value) {
        //don't use index here
        return ImmutableHistoricalEntry.maybeExtractKey(delegateCache, key, value);
    }

    @Override
    public IHistoricalEntry<V> maybeWrap(final FDate key, final IHistoricalEntry<V> value) {
        return value;
    }

    @Override
    public IHistoricalEntry<V> put(final FDate key, final IHistoricalEntry<V> value) {
        final IHistoricalEntry<V> wrapped = maybeWrap(key, value);
        parent.putDirectly(key, wrapped);
        return wrapped;
    }

    @Override
    public IHistoricalEntry<V> put(final FDate key, final V value) {
        final IHistoricalEntry<V> wrapped = maybeWrap(key, value);
        parent.putDirectly(key, wrapped);
        return wrapped;
    }

}
