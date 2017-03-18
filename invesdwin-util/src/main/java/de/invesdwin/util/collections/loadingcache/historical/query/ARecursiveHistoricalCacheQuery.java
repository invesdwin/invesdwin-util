package de.invesdwin.util.collections.loadingcache.historical.query;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCacheOnClearListener;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.time.fdate.FDate;

/**
 * This class can be used inside of historical caches to retrieve values from itself recursively. It solves the infinite
 * recursion problem by falling back to an initial value when the maximum amount of recursions is reached.
 * 
 * For example useful when calculating an exponential moving average that needs access to its previous values.
 * 
 * @param <V>
 */
@ThreadSafe
public abstract class ARecursiveHistoricalCacheQuery<V> {

    public static final int MIN_RECURSION_COUNT = 20;

    private final AHistoricalCache<V> parent;
    private final int maxRecursionCount;
    @GuardedBy("parent")
    private boolean recursionInProgress = false;
    @GuardedBy("parent")
    private FDate firstRecursionKey;
    @GuardedBy("parent")
    private FDate lastRecursionKey;
    @GuardedBy("parent")
    private FDate firstAvailableKey;
    @GuardedBy("parent")
    //reuse the array to reduce the garbage collection overhead
    private FDate[] reusedRecursionKeys;
    @GuardedBy("parent")
    //cache separately since the parent could encounter more evictions than this internal cache
    private final ALoadingCache<FDate, V> cachedRecursiveResults;

    private final IHistoricalCacheQuery<V> parentQuery;
    private final IHistoricalCacheQueryWithFuture<V> parentQueryWithFuture;

    public ARecursiveHistoricalCacheQuery(final AHistoricalCache<V> parent, final int maxRecursionCount) {
        this.parent = parent;
        this.maxRecursionCount = Integers.max(maxRecursionCount, MIN_RECURSION_COUNT);
        this.parentQuery = parent.query();
        this.parentQueryWithFuture = parent.query().withFuture();
        this.cachedRecursiveResults = new ALoadingCache<FDate, V>() {
            @Override
            protected V loadValue(final FDate key) {
                return internalGetPreviousValueByRecursion(key);
            }

            @Override
            protected Integer getInitialMaximumSize() {
                return Math.max(MIN_RECURSION_COUNT, parent.getMaximumSize());
            }
        };
        Assertions.checkTrue(parent.getOnClearListeners().add(new IHistoricalCacheOnClearListener() {
            @Override
            public void onClear() {
                synchronized (parent) {
                    if (!recursionInProgress) {
                        cachedRecursiveResults.clear();
                        reusedRecursionKeys = null;
                    }
                }
            }
        }));
    }

    public int getMaxRecursionCount() {
        return maxRecursionCount;
    }

    public V getPreviousValue(final FDate key, final FDate previousKey) {
        final V previous;
        if (parent.containsKey(previousKey)) {
            previous = parentQuery.getValue(previousKey);
        } else {
            previous = getPreviousValueByRecursion(key, previousKey);
        }
        return previous;
    }

    private V getPreviousValueByRecursion(final FDate key, final FDate previousKey) {
        synchronized (parent) {
            if (previousKey.isBeforeOrEqualTo(getFirstAvailableKey())) {
                return getInitialValue(previousKey);
            }

            if (recursionInProgress) {
                if (cachedRecursiveResults.containsKey(previousKey)) {
                    return cachedRecursiveResults.get(previousKey);
                } else if (previousKey.isBeforeOrEqualTo(firstRecursionKey)
                        || lastRecursionKey.equals(getFirstAvailableKey()) || key.equals(previousKey)) {
                    return getInitialValue(previousKey);
                } else {
                    throw new IllegalStateException(
                            parent + ": the values between " + firstRecursionKey + " and " + lastRecursionKey
                                    + " should have been cached, maybe you are returning null values even if you should not: "
                                    + previousKey);
                }
            }
            recursionInProgress = true;
            try {
                return cachedRecursiveResults.get(previousKey);
            } finally {
                recursionInProgress = false;
            }
        }
    }

    private V internalGetPreviousValueByRecursion(final FDate previousKey) {
        try {
            lastRecursionKey = parentQueryWithFuture.getKey(previousKey);
            FDate curPreviousKey = lastRecursionKey;
            int minRecursionIdx = maxRecursionCount;
            final FDate[] recursionKeys = getRecursionKeys();
            while (minRecursionIdx > 0) {
                final FDate newPreviousKey = parentQueryWithFuture.getPreviousKey(curPreviousKey, 1);
                firstRecursionKey = newPreviousKey;
                if (newPreviousKey.isAfterOrEqualTo(curPreviousKey)) {
                    //start reached
                    break;
                } else if (parent.containsKey(newPreviousKey) || cachedRecursiveResults.containsKey(newPreviousKey)) {
                    //point to continue from reached
                    break;
                } else {
                    //search further for a match to begin from
                    minRecursionIdx--;
                    recursionKeys[minRecursionIdx] = newPreviousKey;
                    curPreviousKey = newPreviousKey;
                }
            }
            for (int i = minRecursionIdx; i < maxRecursionCount; i++) {
                //fill up the missing values
                final FDate recursiveKey = recursionKeys[i];
                final V value = parentQuery.getValue(recursiveKey);
                cachedRecursiveResults.put(recursiveKey, value);
            }
            return parentQuery.getValue(lastRecursionKey);
        } finally {
            firstRecursionKey = null;
            lastRecursionKey = null;
        }
    }

    private FDate[] getRecursionKeys() {
        if (reusedRecursionKeys == null) {
            reusedRecursionKeys = new FDate[maxRecursionCount];
        }
        return reusedRecursionKeys;
    }

    private FDate getFirstAvailableKey() {
        if (firstAvailableKey == null) {
            this.firstAvailableKey = parentQueryWithFuture.getKey(FDate.MIN_DATE);
        }
        return firstAvailableKey;
    }

    protected abstract V getInitialValue(FDate previousKey);

}
