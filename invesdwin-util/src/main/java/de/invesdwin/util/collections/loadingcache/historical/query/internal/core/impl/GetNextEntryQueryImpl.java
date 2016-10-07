package de.invesdwin.util.collections.loadingcache.historical.query.internal.core.impl;

import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryInternalMethods;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class GetNextEntryQueryImpl<V> {

    private final IHistoricalCacheQueryCore<V> core;
    private final FDate key;
    private FDate nextKey = null;
    private Entry<FDate, V> nextEntry = null;
    private int iterations = 0;
    private final int shiftForwardUnits;
    private final IHistoricalCacheQueryInternalMethods<V> query;
    private boolean duplicateEncountered = false;

    public GetNextEntryQueryImpl(final IHistoricalCacheQueryCore<V> core,
            final IHistoricalCacheQueryInternalMethods<V> query, final FDate key, final int shiftForwardUnits) {
        this.core = core;
        this.query = query;
        this.key = key;
        this.nextKey = key;
        this.shiftForwardUnits = shiftForwardUnits;
    }

    public boolean iterationFinished() {
        if (iterations <= shiftForwardUnits) {
            if (!duplicateEncountered) {
                /*
                 * if key of value == key, the same key would be returned on the next call
                 * 
                 * we decrement by one unit to get the next key
                 */

                final FDate nextNextKey;
                if (iterations == 0) {
                    nextNextKey = nextKey;
                } else {
                    nextNextKey = core.getParent().calculateNextKey(nextKey);
                    if (nextNextKey == null) {
                        return true;
                    }
                }
                //the key of the value is the relevant one
                final Entry<FDate, V> nextNextEntry = query.getAssertValue().assertValue(core.getParent(), key,
                        nextNextKey,
                        core.getValue(query, nextNextKey, HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE));
                if (nextNextEntry == null) {
                    nextEntry = null;
                    return true;
                } else {
                    final V nextValue = nextNextEntry.getValue();
                    final FDate actualNextNextKey = core.getParent().extractKey(nextNextKey, nextValue);
                    if (iterations > 0 && actualNextNextKey.equals(nextKey)) {
                        duplicateEncountered = true;
                    }
                    nextKey = actualNextNextKey;
                    nextEntry = nextNextEntry;
                }
            }
            iterations++;
            return false;
        }
        return true;
    }

    public Entry<FDate, V> getResult() {
        return nextEntry;
    }

    public int getIterations() {
        return iterations;
    }

}
