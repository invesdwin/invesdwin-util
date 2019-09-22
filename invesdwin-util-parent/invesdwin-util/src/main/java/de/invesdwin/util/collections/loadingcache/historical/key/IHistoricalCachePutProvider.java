package de.invesdwin.util.collections.loadingcache.historical.key;

import java.util.Map.Entry;
import java.util.Set;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCachePutListener;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCachePutProvider<V> {

    /**
     * WARNING: Only notify put listeners when the new value can actually be retrieved right now from the providing
     * cache via a query. In all other cases use false here.
     */
    void put(FDate newKey, V newValue, FDate prevKey, V prevValue, boolean notifyPutListeners);

    /**
     * WARNING: Only notify put listeners when the new value can actually be retrieved right now from the providing
     * cache via a query. In all other cases use false here.
     */
    void put(V newValue, V prevValue, boolean notifyPutListeners);

    /**
     * WARNING: Only notify put listeners when the new value can actually be retrieved right now from the providing
     * cache via a query. In all other cases use false here.
     */
    void put(Entry<FDate, V> newEntry, Entry<FDate, V> prevEntry, boolean notifyPutListeners);

    Set<IHistoricalCachePutListener> getPutListeners();

    boolean registerPutListener(IHistoricalCachePutListener l);

    boolean unregisterPutListener(IHistoricalCachePutListener l);

    boolean isChildRefreshRequested(AHistoricalCache<?> child);

}
