package de.invesdwin.util.collections.loadingcache.historical.listener;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class HistoricalCacheOnValueLoadedListenerSupport<V> implements IHistoricalCacheOnValueLoadedListener<V> {

    @Override
    public void onValueLoaded(final FDate key, final V value) {}

}
