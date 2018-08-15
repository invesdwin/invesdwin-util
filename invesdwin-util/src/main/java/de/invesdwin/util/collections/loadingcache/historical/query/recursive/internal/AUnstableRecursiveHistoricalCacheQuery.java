package de.invesdwin.util.collections.loadingcache.historical.query.recursive.internal;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.eviction.EvictionMode;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCacheOnClearListener;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.ARecursiveHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.IRecursiveHistoricalCacheQuery;
import de.invesdwin.util.time.fdate.FDate;

/**
 * This variation calculates the values according to recursionCount+unstableRecursionCount for each request. This makes
 * the calculation independent of the actual available history. The unstableRecursionCount should be chosen as small as
 * possible to not become a too large hit on performance.
 */
@ThreadSafe
public abstract class AUnstableRecursiveHistoricalCacheQuery<V> implements IRecursiveHistoricalCacheQuery<V> {

    private final AHistoricalCache<V> parent;
    private final int recursionCount;
    private final int unstableRecursionCount;

    private final IHistoricalCacheQuery<V> parentQuery;
    private final IHistoricalCacheQueryWithFuture<V> parentQueryWithFuture;

    @GuardedBy("parent")
    private FDate firstAvailableKey;
    @GuardedBy("parent")
    private boolean firstAvailableKeyRequested;
    @GuardedBy("parent")
    private boolean recursionInProgress = false;
    @GuardedBy("parent")
    private FDate firstRecursionKey;
    @GuardedBy("parent")
    private FDate lastRecursionKey;

    //cache separately since the parent could encounter more evictions than this internal cache
    private final ALoadingCache<FDate, V> cachedRecursionResults;

    public AUnstableRecursiveHistoricalCacheQuery(final AHistoricalCache<V> parent, final int recursionCount,
            final int unstableRecursionCount) {
        this.parent = parent;
        if (recursionCount <= 0) {
            throw new IllegalArgumentException("recursionCount should be greater than zero: " + recursionCount);
        }
        this.recursionCount = recursionCount;
        if (unstableRecursionCount < 0) {
            throw new IllegalArgumentException(
                    "unstableRecursionCount should not be negative: " + unstableRecursionCount);
        }
        this.unstableRecursionCount = unstableRecursionCount;
        this.parentQuery = parent.query();
        this.parentQueryWithFuture = parent.query().withFuture();
        this.cachedRecursionResults = new ALoadingCache<FDate, V>() {
            @Override
            protected V loadValue(final FDate key) {
                return internalGetPreviousValueByRecursion(key);
            }

            @Override
            protected Integer getInitialMaximumSize() {
                return Math.max(recursionCount, parent.getMaximumSize());
            }

            @Override
            protected EvictionMode getEvictionMode() {
                return AHistoricalCache.EVICTION_MODE;
            }
        };
        Assertions.checkTrue(parent.getOnClearListeners().add(new IHistoricalCacheOnClearListener() {
            @Override
            public void onClear() {
                synchronized (AUnstableRecursiveHistoricalCacheQuery.this.parent) {
                    if (!recursionInProgress) {
                        clear();
                    }
                }
            }

        }));
    }

    @Override
    public void clear() {
        synchronized (parent) {
            cachedRecursionResults.clear();
            firstAvailableKey = null;
            firstAvailableKeyRequested = false;
        }
    }

    @Override
    public int getRecursionCount() {
        return recursionCount;
    }

    @Override
    public Integer getUnstableRecursionCount() {
        return unstableRecursionCount;
    }

    @Override
    public V getPreviousValue(final FDate key, final FDate previousKey) {
        final V previous = getPreviousValueByRecursion(key, previousKey);
        return previous;
    }

    private V getPreviousValueByRecursion(final FDate key, final FDate previousKey) {
        synchronized (parent) {
            final FDate firstAvailableKey = getFirstAvailableKey();
            if (firstAvailableKey == null) {
                //no data found
                return null;
            }
            if (previousKey.isBeforeOrEqualTo(firstAvailableKey)) {
                return getInitialValue(previousKey);
            }

            if (recursionInProgress) {
                if (cachedRecursionResults.containsKey(previousKey)) {
                    return cachedRecursionResults.get(previousKey);
                } else if (previousKey.isBeforeOrEqualTo(firstRecursionKey)
                        || lastRecursionKey.equals(firstAvailableKey) || key.equals(previousKey)) {
                    return getInitialValue(previousKey);
                } else {
                    throw new IllegalStateException(parent + ": the values between " + firstRecursionKey + " and "
                            + lastRecursionKey
                            + " should have been cached, maybe you are returning null values even if you should not: "
                            + previousKey);
                }
            }
            recursionInProgress = true;
            try {
                return cachedRecursionResults.get(parentQueryWithFuture.getKey(previousKey));
            } finally {
                cachedRecursionResults.clear();
                recursionInProgress = false;
            }
        }
    }

    private V internalGetPreviousValueByRecursion(final FDate previousKey) {
        try {
            lastRecursionKey = previousKey;
            final Iterator<FDate> recursionKeysIterator = getFullRecursionKeysIterator(previousKey);
            if (firstRecursionKey == null || firstRecursionKey.isAfterOrEqualTo(previousKey)) {
                return getInitialValue(previousKey);
            }
            try {
                while (true) {
                    //fill up the missing values
                    final FDate recursiveKey = recursionKeysIterator.next();
                    final V value = parentQuery.computeValue(recursiveKey);
                    cachedRecursionResults.put(recursiveKey, value);
                }
            } catch (final NoSuchElementException e) {
                //ignore
            }
            final V lastRecursionResult = parentQuery.computeValue(lastRecursionKey);
            return lastRecursionResult;
        } finally {
            firstRecursionKey = null;
            lastRecursionKey = null;
        }
    }

    protected FDate getFirstAvailableKey() {
        if (firstAvailableKey == null && !firstAvailableKeyRequested) {
            this.firstAvailableKey = parentQueryWithFuture.getKey(FDate.MIN_DATE);
            firstAvailableKeyRequested = true;
        }
        return firstAvailableKey;
    }

    private Iterator<FDate> getFullRecursionKeysIterator(final FDate from) {
        final Iterator<FDate> iterator = newFullRecursionKeysIterator(from);
        if (iterator == null) {
            firstRecursionKey = null;
            return null;
        }
        final PeekingIterator<FDate> peekingIterator = Iterators.peekingIterator(iterator);
        try {
            firstRecursionKey = peekingIterator.peek();
            return peekingIterator;
        } catch (final NoSuchElementException e) {
            firstRecursionKey = null;
            return null;
        }
    }

    protected Iterator<FDate> newFullRecursionKeysIterator(final FDate from) {
        if (shouldUseInitialValueInsteadOfFullRecursion()) {
            return null;
        } else {
            return parentQueryWithFuture.getPreviousKeys(from, recursionCount + unstableRecursionCount).iterator();
        }
    }

    protected abstract V getInitialValue(FDate previousKey);

    protected boolean shouldUseInitialValueInsteadOfFullRecursion() {
        return ARecursiveHistoricalCacheQuery.DEFAULT_SHOULD_USE_INITIAL_VALUE_INSTEAD_OF_FULL_RECURSION;
    }

}
