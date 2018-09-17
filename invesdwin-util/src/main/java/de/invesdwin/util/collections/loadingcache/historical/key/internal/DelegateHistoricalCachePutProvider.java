package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCachePutProvider;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCachePutListener;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class DelegateHistoricalCachePutProvider<V> implements IHistoricalCachePutProvider<V> {

    private final IHistoricalCachePutProvider<V> delegate;

    private DelegateHistoricalCachePutProvider(final IHistoricalCachePutProvider<V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void put(final FDate newKey, final V newValue, final FDate prevKey, final V prevValue) {
        delegate.put(newKey, newValue, prevKey, prevValue);
    }

    @Override
    public void put(final V newValue, final V prevValue) {
        delegate.put(newValue, prevValue);
    }

    @Override
    public void put(final Entry<FDate, V> newEntry, final Entry<FDate, V> prevEntry) {
        delegate.put(newEntry, prevEntry);
    }

    @Override
    public Set<IHistoricalCachePutListener> getPutListeners() {
        return delegate.getPutListeners();
    }

    @Override
    public boolean registerPutListener(final IHistoricalCachePutListener l) {
        return delegate.registerPutListener(l);
    }

    @Override
    public boolean unregisterPutListener(final IHistoricalCachePutListener l) {
        return delegate.unregisterPutListener(l);
    }

    @SuppressWarnings("unchecked")
    public static <T> DelegateHistoricalCachePutProvider<T> maybeWrap(
            final IHistoricalCachePutProvider<? extends T> delegate) {
        if (delegate instanceof DelegateHistoricalCachePutProvider) {
            return (DelegateHistoricalCachePutProvider<T>) delegate;
        } else {
            return new DelegateHistoricalCachePutProvider<T>((IHistoricalCachePutProvider<T>) delegate);
        }
    }

}
