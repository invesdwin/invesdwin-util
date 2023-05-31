package de.invesdwin.util.collections.loadingcache.historical.query.internal.core.impl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryInternalMethods;
import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public class GetNextEntryQueryLoop<V> {

    private final IHistoricalCacheQueryCore<V> core;
    private final FDate key;
    private FDate nextKey = null;
    private IHistoricalEntry<V> nextEntry = null;
    private int iterations = 0;
    private final int shiftForwardUnits;
    private final IHistoricalCacheQueryInternalMethods<V> query;
    private boolean duplicateEncountered = false;

    public GetNextEntryQueryLoop(final IHistoricalCacheQueryCore<V> core,
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
                final IHistoricalEntry<V> potentialNextNextEntry = core.getEntry(query, nextNextKey,
                        HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE);
                final IHistoricalEntry<V> nextNextEntry;
                if (potentialNextNextEntry != null) {
                    nextNextEntry = query.getAssertValue().assertValue(core.getParent(), key, potentialNextNextEntry);
                } else {
                    nextNextEntry = null;
                }
                if (nextNextEntry == null) {
                    nextEntry = null;
                    return true;
                } else {
                    final FDate actualNextNextKey = nextNextEntry.getKey();
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

    public IHistoricalEntry<V> getResult() {
        return nextEntry;
    }

    public int getIterations() {
        return iterations;
    }

}
