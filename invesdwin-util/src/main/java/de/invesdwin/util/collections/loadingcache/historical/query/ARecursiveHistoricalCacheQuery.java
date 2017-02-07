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
    @GuardedBy("this")
    private boolean recursionInProgress = false;
    @GuardedBy("this")
    private FDate firstRecursionKey;
    @GuardedBy("this")
    private FDate lastRecursionKey;

    public ARecursiveHistoricalCacheQuery(final AHistoricalCache<V> parent, final int maxRecursionCount) {
        this.parent = parent;
        this.maxRecursionCount = Integers.max(maxRecursionCount, MIN_RECURSION_COUNT);
        parent.increaseMaximumSize(newSuggestedMaximumSizeForParent(parent.getMaximumSize(), maxRecursionCount));
    }

    public int getMaxRecursionCount() {
        return maxRecursionCount;
    }

    public static int newSuggestedMaximumSizeForParent(final int maximumSize, final int maxRecursionCount) {
        return Integers.max(maximumSize, maxRecursionCount * 2);
    }

    public V getPreviousValue(final FDate key, final FDate previousKey) {
        final V previous;
        if (parent.containsKey(previousKey)) {
            previous = newQuery(parent).getValue(previousKey);
        } else {
            previous = getPreviousValueByRecursion(key, previousKey);
        }
        return previous;
    }

    private synchronized V getPreviousValueByRecursion(final FDate key, final FDate previousKey) {
        if (recursionInProgress) {
            if (previousKey.isBeforeOrEqualTo(firstRecursionKey)) {
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

    private V internalGetPreviousValueByRecursion(final FDate previousKey) {
        try {
            List<FDate> recursionKeys = Lists
                    .toListWithoutHasNext(newQuery(parent).getPreviousKeys(previousKey, maxRecursionCount));
            if (recursionKeys.isEmpty()) {
                return null;
            }
            firstRecursionKey = recursionKeys.get(0);
            lastRecursionKey = recursionKeys.get(recursionKeys.size() - 1);
            if (!lastRecursionKey.equals(previousKey)) {
                throw new IllegalStateException(
                        parent + ": lastRecursionKey [" + lastRecursionKey + "] != previousKey [" + previousKey + "]");
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
                newQuery(parent).getValue(recursionKey);
            }
            final V previous = newQuery(parent).getValue(previousKey);
            return previous;
        } finally {
            firstRecursionKey = null;
            lastRecursionKey = null;
        }
    }

    /**
     * can be overridden to change the future data handling
     */
    protected <T> IHistoricalCacheQuery<T> newQuery(final AHistoricalCache<T> parent) {
        return parent.query();
    }

    protected abstract V getInitialValue(FDate previousKey);

}
