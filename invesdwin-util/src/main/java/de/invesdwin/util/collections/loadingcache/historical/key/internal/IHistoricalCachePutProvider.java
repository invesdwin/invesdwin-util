package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import java.util.Map.Entry;
import java.util.Set;

import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCachePutListener;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCachePutProvider<V> {

    void put(FDate newKey, V newValue, FDate prevKey, V prevValue);

    void put(V newValue, V prevValue);

    void put(Entry<FDate, V> newEntry, Entry<FDate, V> prevEntry);

    Set<IHistoricalCachePutListener> getPutListeners();

    boolean registerPutListener(IHistoricalCachePutListener l);

    boolean unregisterPutListener(IHistoricalCachePutListener l);

}
