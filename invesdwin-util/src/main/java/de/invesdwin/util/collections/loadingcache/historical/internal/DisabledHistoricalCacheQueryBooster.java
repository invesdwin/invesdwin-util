package de.invesdwin.util.collections.loadingcache.historical.internal;

import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class DisabledHistoricalCacheQueryBooster<V> implements IHistoricalCacheQueryBooster<V> {

    @Override
    public void increaseMaximumSize(final int maximumSize) {}

    @Override
    public void clear() {}

    @Override
    public List<? extends Entry<FDate, V>> getPreviousEntries(final FDate key, final int shiftBackUnits) {
        return null;
    }

}
