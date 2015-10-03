package de.invesdwin.util.collections.loadingcache.historical.listener;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class HistoricalCacheListenerSupport<V> implements IHistoricalCacheListener<V> {

    @Override
    public void onBeforeGet(final FDate key) {}

    @Override
    public void onValueLoaded(final FDate key, final V value) {}

}
