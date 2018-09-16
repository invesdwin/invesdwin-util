package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCachePutListener;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class DelegateHistoricalCachePutProvider<V> implements IHistoricalCachePutProvider<V> {

    private final AHistoricalCache<V> delegate;

    @SuppressWarnings("unchecked")
    public DelegateHistoricalCachePutProvider(final AHistoricalCache<? extends V> delegate) {
        this.delegate = (AHistoricalCache<V>) delegate;
    }

    @Override
    public void put(final FDate newKey, final V newValue, final FDate prevKey, final V prevValue) {
        delegate.getPutProvider().put(newKey, newValue, prevKey, prevValue);
    }

    @Override
    public void put(final V newValue, final V prevValue) {
        delegate.getPutProvider().put(newValue, prevValue);
    }

    @Override
    public void put(final Entry<FDate, V> newEntry, final Entry<FDate, V> prevEntry) {
        delegate.getPutProvider().put(newEntry, prevEntry);
    }

    @Override
    public Set<IHistoricalCachePutListener> getPutListeners() {
        return delegate.getPutProvider().getPutListeners();
    }

    @Override
    public boolean registerPutListener(final IHistoricalCachePutListener l) {
        return delegate.getPutProvider().registerPutListener(l);
    }

    @Override
    public boolean unregisterPutListener(final IHistoricalCachePutListener l) {
        return delegate.getPutProvider().unregisterPutListener(l);
    }

}
