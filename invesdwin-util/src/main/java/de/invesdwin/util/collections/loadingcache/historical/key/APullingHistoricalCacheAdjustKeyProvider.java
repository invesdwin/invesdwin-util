package de.invesdwin.util.collections.loadingcache.historical.key;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
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
    @GuardedBy("this")
    private FDate curHighestAllowedKey;
    @GuardedBy("this")
    private final Set<FDate> keysToRemoveOnNewHighestAllowedKey = new HashSet<FDate>();
    private final AHistoricalCache<?> parent;

    public APullingHistoricalCacheAdjustKeyProvider(final AHistoricalCache<?> parent) {
        this.parent = parent;
    }

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

    public AHistoricalCache<?> getParent() {
        return parent;
    }

    private FDate getHighestAllowedKeyUpdateCached() {
        final FDate newHighestAllowedKey = innerGetHighestAllowedKey();
        if (newHighestAllowedKey != null) {
            synchronized (this) {
                final boolean purge = curHighestAllowedKey == null;
                if (purge) {
                    //purge maybe already remembered keys above curHighestAllowedKey
                    clear();
                }
                if (purge || curHighestAllowedKey.isBefore(newHighestAllowedKey)) {
                    for (final FDate keyToRemove : keysToRemoveOnNewHighestAllowedKey) {
                        parent.remove(keyToRemove);
                    }
                    curHighestAllowedKey = newHighestAllowedKey;
                    keysToRemoveOnNewHighestAllowedKey.clear();
                }
            }
        }
        return newHighestAllowedKey;
    }

    @Override
    public synchronized FDate getHighestAllowedKey() {
        return curHighestAllowedKey;
    }

    private synchronized void rememberKeyToRemove(final FDate key) {
        if (curHighestAllowedKey != null && key.isAfter(curHighestAllowedKey)) {
            keysToRemoveOnNewHighestAllowedKey.add(key);
        }
    }

    protected abstract FDate innerGetHighestAllowedKey();

    @Override
    public synchronized void clear() {
        keysToRemoveOnNewHighestAllowedKey.clear();
        curHighestAllowedKey = null;
    }

}
