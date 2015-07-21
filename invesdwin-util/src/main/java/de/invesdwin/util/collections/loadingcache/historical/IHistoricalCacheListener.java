package de.invesdwin.util.collections.loadingcache.historical;

import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheListener<V> {

    void onValueLoaded(FDate key, V value);

}
