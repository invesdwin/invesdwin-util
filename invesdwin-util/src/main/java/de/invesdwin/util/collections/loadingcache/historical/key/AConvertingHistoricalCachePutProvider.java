package de.invesdwin.util.collections.loadingcache.historical.key;

import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.tuple.ImmutableEntry;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCachePutListener;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public abstract class AConvertingHistoricalCachePutProvider<FROM, TO> implements IHistoricalCachePutProvider<TO> {

    private final IHistoricalCachePutProvider<FROM> delegate;

    @SuppressWarnings("unchecked")
    public AConvertingHistoricalCachePutProvider(final AHistoricalCache<? extends FROM> parent) {
        this.delegate = (IHistoricalCachePutProvider<FROM>) parent.getPutProvider();
    }

    private Entry<FDate, FROM> nullSafeConvertEntry(final Entry<FDate, TO> entry) {
        if (entry == null) {
            return null;
        } else {
            return ImmutableEntry.of(entry.getKey(), nullSafeConvertValue(entry.getValue()));
        }
    }

    private FROM nullSafeConvertValue(final TO value) {
        if (value == null) {
            return null;
        } else {
            return convertValue(value);
        }
    }

    protected abstract FROM convertValue(TO value);

    @Override
    public void put(final FDate newKey, final TO newValue, final FDate prevKey, final TO prevValue) {
        delegate.put(newKey, nullSafeConvertValue(newValue), prevKey, nullSafeConvertValue(prevValue));
    }

    @Override
    public void put(final TO newValue, final TO prevValue) {
        delegate.put(nullSafeConvertValue(newValue), nullSafeConvertValue(prevValue));
    }

    @Override
    public void put(final Entry<FDate, TO> newEntry, final Entry<FDate, TO> prevEntry) {
        delegate.put(nullSafeConvertEntry(newEntry), nullSafeConvertEntry(prevEntry));
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

}
