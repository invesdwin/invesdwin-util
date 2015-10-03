package de.invesdwin.util.collections.loadingcache.historical.listener;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AGapHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class AHighWaterMarkHistoricalCacheListener<V> implements IHistoricalCacheListener<V> {

    private final AHistoricalCache<V> parent;

    @GuardedBy("this")
    private FDate curHighWaterMark;
    @GuardedBy("this")
    private final Set<FDate> removeKeysOnNewHighWaterMark = new LinkedHashSet<FDate>();

    @SuppressWarnings("deprecation")
    public AHighWaterMarkHistoricalCacheListener(final AHistoricalCache<V> parent) {
        this.parent = parent;
        if (parent.getMaximumSize() < 1) {
            throw new IllegalArgumentException("This should only be added to caches that actually cache values!");
        }
        if (parent instanceof AGapHistoricalCache) {
            final AGapHistoricalCache<V> cParent = (AGapHistoricalCache<V>) parent;
            cParent.setHighWaterMarkProvider(this);
        }
    }

    public FDate getCurHighWaterMark() {
        return curHighWaterMark;
    }

    @Override
    public void onBeforeGet(final FDate key) {
        final FDate newHighWaterMark = getHighWaterMark();
        if (curHighWaterMark == null || newHighWaterMark.isAfter(curHighWaterMark)) {
            for (final FDate removeKey : removeKeysOnNewHighWaterMark) {
                parent.remove(removeKey);
            }
            removeKeysOnNewHighWaterMark.clear();
            curHighWaterMark = newHighWaterMark;
        }
    }

    protected abstract FDate getHighWaterMark();

    @Override
    public synchronized void onValueLoaded(final FDate key, final V value) {
        if (key.isAfter(curHighWaterMark)) {
            removeKeysOnNewHighWaterMark.add(key);
        }

    }

}
