
package de.invesdwin.util.collections.loadingcache.historical.query.recursive.internal;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.eviction.EvictionMode;
import de.invesdwin.util.collections.eviction.IEvictionMap;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCacheOnClearListener;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.error.ResetCacheException;
import de.invesdwin.util.collections.loadingcache.historical.query.error.ResetCacheRuntimeException;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.ARecursiveHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.IRecursiveHistoricalCacheQuery;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.fdate.FDate;

/**
 * This variation calculates the values according to recursionCount+unstableRecursionCount for each request. This makes
 * the calculation independent of the actual available history. The unstableRecursionCount should be chosen as small as
 * possible to not become a too large hit on performance.
 */
@ThreadSafe
public abstract class AUnstableRecursiveHistoricalCacheQuery<V> implements IRecursiveHistoricalCacheQuery<V> {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(AUnstableRecursiveHistoricalCacheQuery.class);

    private final AHistoricalCache<V> parent;
    private final int recursionCount;
    private final int unstableRecursionCount;

    private final IHistoricalCacheQuery<V> parentQuery;
    private final IHistoricalCacheQueryWithFuture<V> parentQueryWithFuture;

    private final AHistoricalCache<FullRecursionKeysResult> fullRecursionKeysResults;
    private final IHistoricalCacheQuery<FullRecursionKeysResult> fullRecursionKeysResultsQueryWithFutureNull;

    @GuardedBy("parent")
    private FDate fromRecursionKey;
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
    @GuardedBy("parent")
    private int countResets = 0;

    //cache separately since the parent could encounter more evictions than this internal cache
    private ALoadingCache<FDate, V> cachedResults;
    private final IEvictionMap<FDate, Optional<V>> cachedRecursionResults;

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
        this.cachedResults = new ALoadingCache<FDate, V>() {

            @Override
            protected boolean isThreadSafe() {
                return false;
            }

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
        this.cachedRecursionResults = AHistoricalCache.EVICTION_MODE
                .newMap(Math.max(recursionCount, parent.getMaximumSize()));
        Assertions.checkTrue(parent.registerOnClearListener(new IHistoricalCacheOnClearListener() {
            @Override
            public void onClear() {
                synchronized (AUnstableRecursiveHistoricalCacheQuery.this.parent) {
                    if (!recursionInProgress) {
                        clear();
                    }
                }
            }
        }));

        final int fullRecursionCount = recursionCount + unstableRecursionCount;
        this.fullRecursionKeysResults = new FullRecursionKeysCache(parent.getShiftKeyProvider().getParent(),
                fullRecursionCount);
        fullRecursionKeysResults.enableTrailingQueryCore();
        this.fullRecursionKeysResultsQueryWithFutureNull = fullRecursionKeysResults.query().withFutureNull();
        parent.increaseMaximumSize(fullRecursionCount * 2, "fullRecursionCount");
        parent.getPutProvider().registerPutListener(fullRecursionKeysResults);
    }

    @Override
    public void clear() {
        synchronized (parent) {
            resetForRetry();
            countResets = 0;
        }
    }

    private void resetForRetry() {
        cachedResults.clear();
        cachedRecursionResults.clear();
        firstAvailableKey = null;
        firstAvailableKeyRequested = false;
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
        return getPreviousValueByRecusionTry(key, previousKey, 0);
    }

    private V getPreviousValueByRecusionTry(final FDate key, final FDate previousKey, final int tries) {
        try {
            synchronized (parent) {
                final FDate firstAvailableKey = getFirstAvailableKey();
                if (firstAvailableKey == null) {
                    //no data found
                    return null;
                }
                if (previousKey == null || previousKey.isBeforeOrEqualToNotNullSafe(firstAvailableKey)
                        && key.equalsNotNullSafe(previousKey)) {
                    return getInitialValue(previousKey);
                }

                if (recursionInProgress) {
                    return duringRecursion(key, previousKey, firstAvailableKey);
                } else {
                    recursionInProgress = true;
                    try {
                        fromRecursionKey = key;
                        return retryGetPreviousValueByRecursion(previousKey);
                    } finally {
                        fromRecursionKey = null;
                        recursionInProgress = false;
                        cachedRecursionResults.clear();
                    }
                }
            }
        } catch (final ResetCacheException e) {
            final int newTries = tries + 1;
            if (newTries <= AContinuousRecursiveHistoricalCacheQuery.MAX_TRIES) {
                incrementResets(e);
                //CHECKSTYLE:OFF
                LOG.warn("{}: Trying " + newTries + ". recovery from: {}", parent.toString(), e.toString());
                //CHECKSTYLE:ON
                try {
                    //give it some time, might be initializing
                    AContinuousRecursiveHistoricalCacheQuery.RETRY_SLEEP.sleep();
                } catch (final InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
                return getPreviousValueByRecusionTry(key, previousKey, newTries);
            } else {
                throw new RuntimeException(
                        parent.toString() + ": Unable to recover after " + newTries + " tries, giving up", e);
            }
        }
    }

    private void incrementResets(final Throwable e) {
        countResets++;
        if (countResets % AContinuousRecursiveHistoricalCacheQuery.COUNT_RESETS_BEFORE_WARNING == 0
                || AHistoricalCache.isDebugAutomaticReoptimization()) {
            if (LOG.isWarnEnabled()) {
                //CHECKSTYLE:OFF
                LOG.warn(
                        "{}: resetting {} for the {}. time now and retrying after exception [{}: {}], if this happens too often we might encounter bad performance due to inefficient caching",
                        parent, getClass().getSimpleName(), countResets, e.getClass().getSimpleName(), e.getMessage());
                //CHECKSTYLE:ON
            }
        }
    }

    private V retryGetPreviousValueByRecursion(final FDate previousKey) throws ResetCacheException {
        try {
            //need to fetch adj previous key inside retry, since that is sometimes wrong
            final FDate adjPreviousKey = parentQueryWithFuture.getKey(previousKey);
            return cachedResults.get(adjPreviousKey);
        } catch (final Throwable t) {
            if (Throwables.isCausedByType(t, ResetCacheRuntimeException.class)) {
                incrementResets(t);
                resetForRetry();
                /*
                 * also clear parent so that correct adjPreviousKey can be determined, sometimes it returns a non
                 * existent value for weekends
                 */
                parent.clear();
                try {
                    final FDate adjPreviousKey = parentQueryWithFuture.getKey(previousKey);
                    return cachedResults.get(adjPreviousKey);
                } catch (final Throwable t1) {
                    throw new ResetCacheException("Follow up " + ResetCacheRuntimeException.class.getSimpleName()
                            + " on retry after:" + t.toString(), t1);
                }
            } else {
                throw t;
            }
        }
    }

    private V duringRecursion(final FDate key, final FDate previousKey, final FDate firstAvailableKey) {
        final Optional<V> cachedResult = cachedRecursionResults.get(previousKey);
        if (cachedResult != null) {
            return cachedResult.orElse(null);
        } else if (previousKey.isBeforeOrEqualTo(firstRecursionKey) || lastRecursionKey.equals(firstAvailableKey)
                || key.equals(previousKey)) {
            return getInitialValue(previousKey);
        } else {
            throw new ResetCacheRuntimeException(parent + ": the values between " + firstRecursionKey + " and "
                    + lastRecursionKey + " should have been cached: " + previousKey);
        }
    }

    private V internalGetPreviousValueByRecursion(final FDate previousKey) {
        try {
            lastRecursionKey = previousKey;
            final Iterator<FDate> recursionKeysIterator = getFullRecursionKeysIterator(previousKey);
            if (firstRecursionKey == null || (firstRecursionKey.isAfterOrEqualToNotNullSafe(previousKey)
                    && previousKey.equalsNotNullSafe(fromRecursionKey))) {
                return getInitialValue(previousKey);
            }
            FDate curRecursionKey = null;
            V value = null;
            try {
                while (true) {
                    //fill up the missing values
                    curRecursionKey = recursionKeysIterator.next();
                    value = parentQuery.computeValue(curRecursionKey);
                    cachedRecursionResults.put(curRecursionKey, Optional.ofNullable(value));
                }
            } catch (final NoSuchElementException e) {
                //ignore
            }
            if (!lastRecursionKey.equalsNotNullSafe(curRecursionKey)) {
                throw new ResetCacheRuntimeException("lastRecursionKey[" + lastRecursionKey
                        + "] should be equal to curRecursionKey[" + curRecursionKey + "]");
            }
            return value;
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
            return fullRecursionKeysResultsQueryWithFutureNull.getValue(from).getFullRecursionKeys();
        }
    }

    protected abstract V getInitialValue(FDate previousKey);

    protected boolean shouldUseInitialValueInsteadOfFullRecursion() {
        return ARecursiveHistoricalCacheQuery.DEFAULT_SHOULD_USE_INITIAL_VALUE_INSTEAD_OF_FULL_RECURSION;
    }

}
