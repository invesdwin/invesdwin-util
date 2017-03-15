package de.invesdwin.util.collections.loadingcache.historical.query;

import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.Lists;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
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

    private final IHistoricalCacheQuery<V> parentQuery;
    private final IHistoricalCacheQueryWithFuture<V> parentQueryWithFuture;
    private final IHistoricalCacheQuery<V> parentQueryWithFutureNull;

    public ARecursiveHistoricalCacheQuery(final AHistoricalCache<V> parent, final int maxRecursionCount) {
        this.parent = parent;
        this.maxRecursionCount = Integers.max(maxRecursionCount, MIN_RECURSION_COUNT);
        this.parentQuery = parent.query();
        this.parentQueryWithFuture = parent.query().withFuture();
        this.parentQueryWithFutureNull = parent.query().withFutureNull();
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
            if (recursionInProgress) {
                if (previousKey.isBeforeOrEqualTo(firstRecursionKey) || lastRecursionKey.equals(getFirstAvailableKey())
                        || key.equals(previousKey)) {
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
                return internalGetPreviousValueByRecursion(previousKey);
            } finally {
                recursionInProgress = false;
            }
        }
    }

    private V internalGetPreviousValueByRecursion(final FDate previousKey) {
        List<FDate> recursionKeys = Lists
                .toListWithoutHasNext(parentQueryWithFutureNull.getPreviousKeys(previousKey, maxRecursionCount));
        if (recursionKeys.isEmpty()) {
            return getInitialValue(previousKey);
        }
        try {
            firstRecursionKey = recursionKeys.get(0);
            lastRecursionKey = recursionKeys.get(recursionKeys.size() - 1);
            if (!lastRecursionKey.equals(previousKey)) {
                //adjustKeyProvider may not have been filled with any useful data yet and the first value might have been requested; check for that case
                final FDate firstAvailableKey = getFirstAvailableKey();
                if (!lastRecursionKey.equals(firstAvailableKey)) {
                    throw new IllegalStateException(parent + ": lastRecursionKey [" + lastRecursionKey
                            + "] != previousKey [" + previousKey + "]");
                }
            }

            for (int i = recursionKeys.size() - 1; i >= 0; i--) {
                final FDate recursionKey = recursionKeys.get(i);
                if (parent.containsKey(recursionKey)) {
                    //start at latest available recursion key
                    recursionKeys = recursionKeys.subList(i, recursionKeys.size());
                    break;
                }
            }
            for (final FDate recursionKey : recursionKeys) {
                //fill up the missing values
                parentQuery.getValue(recursionKey);
            }
            final V previous = parentQuery.getValue(previousKey);
            return previous;
        } finally {
            firstRecursionKey = null;
            lastRecursionKey = null;
        }
    }

    private FDate getFirstAvailableKey() {
        if (firstAvailableKey == null) {
            this.firstAvailableKey = parentQueryWithFuture.getKey(FDate.MIN_DATE);
        }
        return firstAvailableKey;
    }

    protected abstract V getInitialValue(FDate previousKey);

}
