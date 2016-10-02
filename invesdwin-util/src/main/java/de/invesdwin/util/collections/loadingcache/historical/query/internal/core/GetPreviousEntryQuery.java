package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class GetPreviousEntryQuery<V> {

    private final IHistoricalCacheQueryCore<V> core;
    private final FDate key;
    private FDate previousKey = null;
    private Entry<FDate, V> previousEntry = null;
    private int iterations = 0;
    private final int shiftBackUnits;
    private final IHistoricalCacheQueryInternalMethods<V> query;

    public GetPreviousEntryQuery(final IHistoricalCacheQueryCore<V> core,
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftBackUnits) {
        this.core = core;
        this.query = query;
        this.key = key;
        this.previousKey = key;
        this.shiftBackUnits = shiftBackUnits;
    }

    public boolean iterationFinished() {
        if (iterations <= shiftBackUnits) {
            /*
             * if key of value == key, the same key would be returned on the next call
             * 
             * we decrement by one unit to get the previous key
             */

            final FDate previousPreviousKey;
            if (iterations == 0) {
                previousPreviousKey = previousKey;
            } else {
                previousPreviousKey = core.getParent().calculatePreviousKey(previousKey);
            }
            if (previousPreviousKey == null) {
                return true;
            }
            //the key of the value is the relevant one
            final Entry<FDate, V> previousPreviousEntry = query.getAssertValue().assertValue(core.getParent(), key,
                    previousPreviousKey,
                    core.getValue(query, previousPreviousKey, HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE));
            if (previousPreviousEntry == null) {
                if (previousKey.equals(key)) {
                    previousEntry = null;
                    return true;
                } else {
                    return true;
                }
            } else {
                final V previousValue = previousPreviousEntry.getValue();
                previousKey = core.getParent().extractKey(previousPreviousKey, previousValue);
                previousEntry = previousPreviousEntry;
            }
            iterations++;
            return false;
        }
        return true;
    }

    public Entry<FDate, V> getResult() {
        return previousEntry;
    }

    public int getIterations() {
        return iterations;
    }

}
