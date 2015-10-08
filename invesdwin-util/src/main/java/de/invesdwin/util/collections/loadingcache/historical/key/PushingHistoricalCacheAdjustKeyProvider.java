package de.invesdwin.util.collections.loadingcache.historical.key;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class PushingHistoricalCacheAdjustKeyProvider implements IHistoricalCacheAdjustKeyProvider {

    private volatile FDate curHighestAllowedKey;

    @Override
    public FDate adjustKey(final FDate key) {
        final FDate highestAllowedKey = getHighestAllowedKey();
        if (highestAllowedKey != null && key.isAfter(highestAllowedKey)) {
            return highestAllowedKey;
        } else {
            return key;
        }
    }

    public void pushHighestAllowedKey(final FDate highestAllowedKey) {
        this.curHighestAllowedKey = highestAllowedKey;
    }

    @Override
    public FDate getHighestAllowedKey() {
        return curHighestAllowedKey;
    }

    @Override
    public void clear() {
        curHighestAllowedKey = null;
    }

}
