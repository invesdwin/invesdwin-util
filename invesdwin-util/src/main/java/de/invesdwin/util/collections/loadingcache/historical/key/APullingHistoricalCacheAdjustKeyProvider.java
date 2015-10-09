package de.invesdwin.util.collections.loadingcache.historical.key;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class APullingHistoricalCacheAdjustKeyProvider implements IHistoricalCacheAdjustKeyProvider {

    private final ThreadLocal<Boolean> alreadyAdjustingKey = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };
    private volatile FDate curHighestAllowedKey;
    private final Set<FDate> keysToRemoveOnNewHighestAllowedKey = new CopyOnWriteArraySet<FDate>();
    private final Set<AHistoricalCache<?>> historicalCaches = new CopyOnWriteArraySet<AHistoricalCache<?>>();

    @Override
    public FDate adjustKey(final FDate key) {
        if (!alreadyAdjustingKey.get()) {
            alreadyAdjustingKey.set(true);
            try {
                final FDate newHighestAllowedKey = getHighestAllowedKeyUpdateCached();
                if (newHighestAllowedKey != null) {
                    if (key.isAfter(newHighestAllowedKey)) {
                        return newHighestAllowedKey;
                    }
                }
            } finally {
                alreadyAdjustingKey.set(false);
            }
        } else {
            rememberKeyToRemove(key);
        }
        return key;
    }

    private FDate getHighestAllowedKeyUpdateCached() {
        final FDate newHighestAllowedKey = innerGetHighestAllowedKey();
        if (newHighestAllowedKey != null) {
            final FDate curHighestAllowedKeyCopy = curHighestAllowedKey;
            final boolean purge = curHighestAllowedKeyCopy == null;
            if (purge) {
                //purge maybe already remembered keys above curHighestAllowedKey
                for (final AHistoricalCache<?> c : historicalCaches) {
                    c.clear();
                }
            }

            if (purge || curHighestAllowedKeyCopy.isBefore(newHighestAllowedKey)) {
                for (final FDate keyToRemove : keysToRemoveOnNewHighestAllowedKey) {
                    for (final AHistoricalCache<?> c : historicalCaches) {
                        c.remove(keyToRemove);
                    }
                }
                curHighestAllowedKey = newHighestAllowedKey;
                keysToRemoveOnNewHighestAllowedKey.clear();
            }
        }
        return newHighestAllowedKey;
    }

    @Override
    public FDate getHighestAllowedKey() {
        return curHighestAllowedKey;
    }

    private void rememberKeyToRemove(final FDate key) {
        if (curHighestAllowedKey != null && key.isAfter(curHighestAllowedKey)) {
            keysToRemoveOnNewHighestAllowedKey.add(key);
        }
    }

    protected abstract FDate innerGetHighestAllowedKey();

    @Override
    public void clear() {
        keysToRemoveOnNewHighestAllowedKey.clear();
        curHighestAllowedKey = null;
    }

    @Override
    public boolean registerHistoricalCache(final AHistoricalCache<?> historicalCache) {
        return historicalCaches.add(historicalCache);
    }

}
