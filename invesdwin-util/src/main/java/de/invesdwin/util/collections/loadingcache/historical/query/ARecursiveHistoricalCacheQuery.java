package de.invesdwin.util.collections.loadingcache.historical.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
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

    /**
     * we should use 10 times the lookback period (bars count) in order to get 7 decimal points of accuracy against
     * calculating from the beginning of history (measured on lowpass indicator)
     */
    public static final int RECOMMENDED_LOOKBACK_MULTIPLICATOR_FOR_RECURSION = 10;

    /**
     * Zorro has UnstablePeriod at a default of 40
     * 
     * http://zorro-trader.com/manual/en/lookback.htm
     */
    private static final int MIN_RECURSION_COUNT = 40;

    /**
     * If we go beyong this in gaps to the previous value, we should start from scratch since searching for the first
     * key would take too long then
     */
    private static final int MAX_CONTINUE_SEARCH_COUNT = 100;

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
            final Iterator<FDate> recursionKeysIterator = newRecursionKeysIterator(previousKey);
            try {
                while (true) {
                    //fill up the missing values
                    final FDate recursiveKey = recursionKeysIterator.next();
                    final V value = parentQuery.getValue(recursiveKey);
                    cachedRecursiveResults.put(recursiveKey, value);
                }
            } catch (final NoSuchElementException e) {
                //ignore
            }
            return parentQuery.getValue(lastRecursionKey);
        } finally {
            firstRecursionKey = null;
            lastRecursionKey = null;
        }
    }

    private Iterator<FDate> newRecursionKeysIterator(final FDate previousKey) {
        lastRecursionKey = parentQueryWithFuture.getKey(previousKey);
        if (cachedRecursiveResults.isEmpty()) {
            return newFullRecursionKeysIterator(previousKey);
        }
        FDate curPreviousKey = lastRecursionKey;
        int minRecursionIdx = maxRecursionCount;
        final List<FDate> recursionKeys = new ArrayList<FDate>();
        int continueSearchCount = 0;
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
                recursionKeys.add(0, newPreviousKey);
                curPreviousKey = newPreviousKey;
                continueSearchCount++;
                if (continueSearchCount >= MAX_CONTINUE_SEARCH_COUNT) {
                    //we hit the maximum search in previous values, thus we should go from the start
                    return newFullRecursionKeysIterator(previousKey);
                }
            }
        }
        final ICloseableIterator<FDate> recursionKeysIterator = WrapperCloseableIterable.maybeWrap(recursionKeys)
                .iterator();
        return recursionKeysIterator;
    }

    private Iterator<FDate> newFullRecursionKeysIterator(final FDate previousKey) {
        final PeekingIterator<FDate> peekingIterator = Iterators
                .peekingIterator(parentQueryWithFuture.getPreviousKeys(previousKey, maxRecursionCount).iterator());
        firstRecursionKey = peekingIterator.peek();
        return peekingIterator;
    }

    private FDate getFirstAvailableKey() {
        if (firstAvailableKey == null) {
            this.firstAvailableKey = parentQueryWithFuture.getKey(FDate.MIN_DATE);
        }
        return firstAvailableKey;
    }

    protected abstract V getInitialValue(FDate previousKey);

}
