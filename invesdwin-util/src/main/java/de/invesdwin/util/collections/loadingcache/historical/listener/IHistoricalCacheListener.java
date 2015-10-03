package de.invesdwin.util.collections.loadingcache.historical.listener;

import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheListener<V> {

    void onBeforeGet(FDate key);

    void onValueLoaded(FDate key, V value);

}
