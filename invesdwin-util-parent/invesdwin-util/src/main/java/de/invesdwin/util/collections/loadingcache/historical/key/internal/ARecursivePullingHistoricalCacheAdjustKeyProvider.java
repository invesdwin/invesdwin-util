package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.BooleanUtils;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.key.AdjustedFDate;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.concurrent.reference.WeakThreadLocalReference;
import de.invesdwin.util.time.date.FDate;
import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public abstract class ARecursivePullingHistoricalCacheAdjustKeyProvider implements IHistoricalCacheAdjustKeyProvider {

    private static final FastThreadLocal<Boolean> GLOBAL_ALREADY_ADJUSTING_KEY = new FastThreadLocal<Boolean>();
    private final WeakThreadLocalReference<Boolean> alreadyAdjustingKey = new WeakThreadLocalReference<Boolean>();

    private volatile FDate curHighestAllowedKey;
    private volatile FDate prevHighestAllowedKey;
    private final Set<FDate> keysToRemoveOnNewHighestAllowedKey = Collections.synchronizedSet(new HashSet<FDate>());
    private final Set<HistoricalCacheForClear> historicalCachesForClear = Collections
            .synchronizedSet(new HashSet<HistoricalCacheForClear>());
    private final AHistoricalCache<?> parent;
    private final int hashCode = super.hashCode();

    public ARecursivePullingHistoricalCacheAdjustKeyProvider(final AHistoricalCache<?> parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public AHistoricalCache<?> getParent() {
        return parent;
    }

    @Override
    public FDate adjustKey(final FDate key) {
        if (key == null) {
            return null;
        }
        if (key.isBeforeOrEqualTo(curHighestAllowedKey)) {
            return key;
        }
        if (BooleanUtils.isNotTrue(alreadyAdjustingKey.get())) {
            final Boolean prevGlobalAlreadyAdjustingKey = GLOBAL_ALREADY_ADJUSTING_KEY.get();
            GLOBAL_ALREADY_ADJUSTING_KEY.set(true);
            alreadyAdjustingKey.set(true);
            try {
                final FDate newHighestAllowedKey = getHighestAllowedKeyUpdateCached();
                if (newHighestAllowedKey != null) {
                    if (key.millisValue() > newHighestAllowedKey.millisValue()) {
                        return newHighestAllowedKey;
                    }
                }
            } finally {
                GLOBAL_ALREADY_ADJUSTING_KEY.set(prevGlobalAlreadyAdjustingKey);
                alreadyAdjustingKey.remove();
            }
        } else {
            rememberKeyToRemove(key);
        }
        return key;
    }

    @SuppressWarnings("deprecation")
    private FDate getHighestAllowedKeyUpdateCached() {
        final FDate newHighestAllowedKey = innerGetHighestAllowedKey();
        if (newHighestAllowedKey != null) {
            final FDate curHighestAllowedKeyCopy = curHighestAllowedKey;
            final boolean purge = curHighestAllowedKeyCopy == null;
            if (purge) {
                //purge maybe already remembered keys above curHighestAllowedKey
                clear();
            }

            if (purge || curHighestAllowedKeyCopy.isBefore(newHighestAllowedKey)) {
                for (final FDate keyToRemove : keysToRemoveOnNewHighestAllowedKey) {
                    //only parent will actually be used to search without being adjusted
                    //and we don't want to keep references to all those others using this properly
                    parent.remove(keyToRemove);
                }
                prevHighestAllowedKey = curHighestAllowedKeyCopy;
                curHighestAllowedKey = newHighestAllowedKey;
                keysToRemoveOnNewHighestAllowedKey.clear();
            }
        }
        return newHighestAllowedKey;
    }

    @Override
    public FDate getHighestAllowedKey() {
        if (curHighestAllowedKey == null) {
            if (BooleanUtils.isNotTrue(alreadyAdjustingKey.get())) {
                final Boolean prevGlobalAlreadyAdjustingKey = GLOBAL_ALREADY_ADJUSTING_KEY.get();
                GLOBAL_ALREADY_ADJUSTING_KEY.set(true);
                alreadyAdjustingKey.set(true);
                try {
                    getHighestAllowedKeyUpdateCached();
                } finally {
                    GLOBAL_ALREADY_ADJUSTING_KEY.set(prevGlobalAlreadyAdjustingKey);
                    alreadyAdjustingKey.remove();
                }
            }
        }
        return curHighestAllowedKey;
    }

    @Override
    public FDate getPreviousHighestAllowedKey() {
        return prevHighestAllowedKey;
    }

    public static boolean isGlobalAlreadyAdjustingKey() {
        return BooleanUtils.isTrue(GLOBAL_ALREADY_ADJUSTING_KEY.get());
    }

    @Override
    public boolean isAlreadyAdjustingKey() {
        return isGlobalAlreadyAdjustingKey();
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
        //        curHighestAllowedKey = null; // dont clear highestallowedkey or else backtests might get confused
        if (!historicalCachesForClear.isEmpty()) {
            //make copy to prevent recusion
            final List<HistoricalCacheForClear> historicalCachesForClearCopy = new ArrayList<HistoricalCacheForClear>(
                    historicalCachesForClear);
            //remove references to prevent memory leaks
            historicalCachesForClear.clear();
            for (final HistoricalCacheForClear c : historicalCachesForClearCopy) {
                c.clear();
            }
        }
    }

    @Override
    public boolean registerHistoricalCache(final AHistoricalCache<?> historicalCache) {
        if (curHighestAllowedKey == null) {
            return historicalCachesForClear.add(new HistoricalCacheForClear(historicalCache));
        } else {
            historicalCache.clear();
            return true;
        }
    }

    @Override
    public FDate newAlreadyAdjustedKey(final FDate key) {
        return AdjustedFDate.newAdjustedKey(this, key);
    }

    @Override
    public FDate maybeAdjustKey(final FDate key) {
        return AdjustedFDate.maybeAdjustKey(this, key);
    }

    @Override
    public boolean isAdjustedKey(final FDate key) {
        return AdjustedFDate.isAdjustedKey(this, key);
    }

    @Override
    public final <T> IHistoricalCacheQuery<T> newQuery(
            final de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods<T> internalMethods) {
        return new de.invesdwin.util.collections.loadingcache.historical.query.internal.adjust.AdjustingHistoricalCacheQuery<T>(
                internalMethods);
    }

}
