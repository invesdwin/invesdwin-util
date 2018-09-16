package de.invesdwin.util.collections.loadingcache.historical.listener;

import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCachePutListener {

    void putPreviousKey(FDate previousKey, FDate valueKey);

}
