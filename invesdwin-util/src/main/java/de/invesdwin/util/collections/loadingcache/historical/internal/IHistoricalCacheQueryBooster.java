package de.invesdwin.util.collections.loadingcache.historical.internal;

import java.util.List;
import java.util.Map.Entry;

import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheQueryBooster<V> {

    void increaseMaximumSize(int maximumSize);

    void clear();

    List<? extends Entry<FDate, V>> getPreviousEntries(FDate key, int shiftBackUnits);

}
