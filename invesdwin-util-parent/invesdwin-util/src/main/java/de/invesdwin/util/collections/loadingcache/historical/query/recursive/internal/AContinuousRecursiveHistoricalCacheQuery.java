package de.invesdwin.util.collections.loadingcache.historical.query.recursive.internal;

import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.eviction.EvictionMode;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.iterable.buffer.IBufferingIterator;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCacheOnClearListener;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.error.ResetCacheRuntimeException;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.ARecursiveHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.IRecursiveHistoricalCacheQuery;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FTimeUnit;
import de.invesdwin.util.time.range.TimeRange;

/**
 * This variation calculates the values continuously when possible by using the previous value from the cache thus
 * making the queries faster than the unstable variation.
 */
@ThreadSafe
public abstract class AContinuousRecursiveHistoricalCacheQuery<V> implements IRecursiveHistoricalCacheQuery<V> {

    public static final int COUNT_RESETS_BEFORE_WARNING = 10;
    /**
     * we should use 10 times the lookback period (bars count) in order to get 7 decimal points of accuracy against
     * calculating from the beginning of history (measured on lowpass indicator)
     */
    private static final int RECURSION_COUNT_LOOKBACK_MULTIPLICATOR = 10;

    /**
     * Zorro has UnstablePeriod at a default of 40
     * 
     * http://zorro-trader.com/manual/en/lookback.htm
     * 
     * talib suggests 100 for most indicators
     * 
     * https://ta-lib.org/d_api/ta_setunstableperiod.html
     */
    private static final int MIN_RECURSION_LOOKBACK = 100;
    private static final int LARGE_RECALCULATION_WARNING_THRESHOLD = 10;

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(AContinuousRecursiveHistoricalCacheQuery.class);

    private final AHistoricalCache<V> parent;
    private final int recursionCount;
    @GuardedBy("parent")
    private boolean recursionInProgress = false;
    @GuardedBy("parent")
    private FDate firstRecursionKey;
    @GuardedBy("parent")
    private FDate lastRecursionKey;
    //BTreeMap has problems with removing first entry so we use TreeMap
    @GuardedBy("parent")
    private final NavigableMap<FDate, V> highestRecursionResultsAsc = new TreeMap<FDate, V>(FDate.COMPARATOR);
    private final int maxHighestRecursionResultsCount;
    @GuardedBy("parent")
    private boolean shouldAppendHighestRecursionResults;
    @GuardedBy("parent")
    private FDate firstAvailableKey;
    @GuardedBy("parent")
    private boolean firstAvailableKeyRequested;
    @GuardedBy("parent")
    private int countResets = 0;
    //cache separately since the parent could encounter more evictions than this internal cache
    @GuardedBy("parent")
    private final ALoadingCache<FDate, V> cachedRecursionResults;
    @GuardedBy("parent")
    private int largeRecalculationsCount = 0;

    private final IHistoricalCacheQuery<V> parentQuery;
    private final IHistoricalCacheQueryWithFuture<V> parentQueryWithFuture;

    public AContinuousRecursiveHistoricalCacheQuery(final AHistoricalCache<V> parent, final int recursionCount) {
        this.parent = parent;
        if (recursionCount <= 0) {
            throw new IllegalArgumentException("recursionCount should be greater than zero: " + recursionCount);
        }
        this.recursionCount = newContinuousUnstablePeriod(recursionCount);
        this.maxHighestRecursionResultsCount = Integer.max(recursionCount, MIN_RECURSION_LOOKBACK);
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
        Assertions.checkTrue(parent.registerOnClearListener(new IHistoricalCacheOnClearListener() {
            @Override
            public void onClear() {
                synchronized (AContinuousRecursiveHistoricalCacheQuery.this.parent) {
                    if (!recursionInProgress) {
                        clear();
                    }
                }
            }
        }));
        parent.increaseMaximumSize(this.recursionCount, "recursionCount");
    }

    public static int newContinuousUnstablePeriod(final int recursionCount) {
        return Integers.max(recursionCount * RECURSION_COUNT_LOOKBACK_MULTIPLICATOR, MIN_RECURSION_LOOKBACK);
    }

    @Override
    public void clear() {
        synchronized (parent) {
            resetForRetry();
            countResets = 0;
        }
    }

    private void resetForRetry() {
        cachedRecursionResults.clear();
        highestRecursionResultsAsc.clear();
        firstAvailableKey = null;
        firstAvailableKeyRequested = false;
        shouldAppendHighestRecursionResults = false;
    }

    @Override
    public int getRecursionCount() {
        return recursionCount;
    }

    @Override
    public Integer getUnstableRecursionCount() {
        return null;
    }

    @Override
    public V getPreviousValue(final FDate key, final FDate previousKey) {
        if (parent.containsKey(previousKey)) {
            final V value = parentQuery.getEntry(previousKey).getValueIfPresent();
            if (value != null) {
                return value;
            }
        }
        return getPreviousValueByRecursion(key, previousKey);
    }

    private V getPreviousValueByRecursion(final FDate key, final FDate previousKey) {
        synchronized (parent) {
            final FDate firstAvailableKey = getFirstAvailableKey();
            if (firstAvailableKey == null) {
                //no data found
                return null;
            }
            if (previousKey == null || previousKey.isBeforeOrEqualTo(firstAvailableKey)) {
                return getInitialValue(previousKey);
            }

            if (recursionInProgress) {
                final V highestRecursionResult = highestRecursionResultsAsc.get(previousKey);
                if (highestRecursionResult != null) {
                    return highestRecursionResult;
                } else {
                    final V cachedResult = cachedRecursionResults.getIfPresent(previousKey);
                    if (cachedResult != null) {
                        return cachedResult;
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
            }
            recursionInProgress = true;
            try {
                return retryGetPreviousValueByRecursion(previousKey);
            } finally {
                recursionInProgress = false;
            }
        }
    }

    private V retryGetPreviousValueByRecursion(final FDate previousKey) {
        try {
            //need to fetch adj previous key inside retry, since that is sometimes wrong
            final FDate adjPreviousKey = parentQueryWithFuture.getKey(previousKey);
            return cachedRecursionResults.get(adjPreviousKey);
        } catch (final Throwable t) {
            if (Throwables.isCausedByType(t, ResetCacheRuntimeException.class)) {
                countResets++;
                if (countResets % COUNT_RESETS_BEFORE_WARNING == 0
                        || AHistoricalCache.isDebugAutomaticReoptimization()) {
                    if (LOG.isWarnEnabled()) {
                        //CHECKSTYLE:OFF
                        LOG.warn(
                                "{}: resetting {} for the {}. time now and retrying after exception [{}: {}], if this happens too often we might encounter bad performance due to inefficient caching",
                                parent, getClass().getSimpleName(), countResets, t.getClass().getSimpleName(),
                                t.getMessage());
                        //CHECKSTYLE:ON
                    }
                }
                resetForRetry();
                /*
                 * also clear parent so that correct adjPreviousKey can be determined, sometimes it returns a non
                 * existent value for weekends
                 */
                parent.clear();
                try {
                    final FDate adjPreviousKey = parentQueryWithFuture.getKey(previousKey);
                    return cachedRecursionResults.get(adjPreviousKey);
                } catch (final Throwable t1) {
                    throw new RuntimeException("Follow up " + ResetCacheRuntimeException.class.getSimpleName()
                            + " on retry after:" + t.toString(), t1);
                }
            } else {
                throw t;
            }
        }
    }

    private V internalGetPreviousValueByRecursion(final FDate previousKey) {
        try {
            lastRecursionKey = previousKey;
            //check again if lastRecursionKey is already available as cached value
            final V highestRecursionResult = highestRecursionResultsAsc.get(lastRecursionKey);
            if (highestRecursionResult != null) {
                return highestRecursionResult;
            }
            final IBufferingIterator<FDate> recursionKeysIterator = newRecursionKeysIterator(previousKey);
            if (firstRecursionKey == null || firstRecursionKey.isAfterOrEqualTo(previousKey)) {
                return getInitialValue(previousKey);
            }
            FDate curRecursionKey = null;
            V value = null;
            try {
                while (true) {
                    //fill up the missing values
                    curRecursionKey = recursionKeysIterator.next();
                    value = parentQuery.getValue(curRecursionKey);
                    appendHighestRecursionResult(curRecursionKey, value);
                    cachedRecursionResults.put(curRecursionKey, value);
                }
            } catch (final NoSuchElementException e) {
                //ignore
            }
            if (!lastRecursionKey.equals(curRecursionKey)) {
                throw new ResetCacheRuntimeException("lastRecursionKey[" + lastRecursionKey
                        + "] should be equal to curRecursionKey[" + curRecursionKey + "]");
            }
            return value;
        } finally {
            firstRecursionKey = null;
            lastRecursionKey = null;
            shouldAppendHighestRecursionResults = false;
        }
    }

    private void appendHighestRecursionResult(final FDate key, final V value) {
        if (shouldAppendHighestRecursionResults) {
            if (!highestRecursionResultsAsc.isEmpty()) {
                final FDate highestRecursionKey = highestRecursionResultsAsc.lastKey();
                if (highestRecursionKey != null && highestRecursionKey.isAfter(key)) {
                    return;
                }
            }
            highestRecursionResultsAsc.put(key, value);
            while (highestRecursionResultsAsc.size() > maxHighestRecursionResultsCount) {
                highestRecursionResultsAsc.pollFirstEntry();
            }
        }
    }

    //CHECKSTYLE:OFF
    private IBufferingIterator<FDate> newRecursionKeysIterator(final FDate previousKey) {
        //CHECKSTYLE:ON
        if (highestRecursionResultsAsc.isEmpty()) {
            shouldAppendHighestRecursionResults = true;
        }
        if (cachedRecursionResults.isEmpty()) {
            //nothing here yet, have to go the full range
            return getFullRecursionKeysIterator(previousKey);
        }
        //we seem to have started somewhere in the middle, thus try to continue from somewhere we left off before
        FDate curPreviousKey = lastRecursionKey;
        int minRecursionIdx = recursionCount;
        final IBufferingIterator<FDate> recursionKeys = new BufferingIterator<FDate>();
        while (minRecursionIdx > 0) {
            final FDate newPreviousKey = parentQueryWithFuture.getPreviousKey(curPreviousKey, 1);
            firstRecursionKey = newPreviousKey;
            if (recursionKeys.isEmpty() && !previousKey.equalsNotNullSafe(newPreviousKey)) {
                recursionKeys.add(previousKey);
            }
            if (newPreviousKey.isAfterOrEqualTo(previousKey)) {
                //start or end reached
                break;
            } else if (highestRecursionResultsAsc.containsKey(newPreviousKey)) {
                shouldAppendHighestRecursionResults = true;
                //point to continue from reached
                break;
            } else if (parent.containsKey(newPreviousKey) || cachedRecursionResults.containsKey(newPreviousKey)) {
                //point to continue from reached
                recursionKeys.prepend(newPreviousKey);
                break;
            } else {
                //search further for a match to begin from
                minRecursionIdx--;
                recursionKeys.prepend(newPreviousKey);
                curPreviousKey = newPreviousKey;
                /*
                 * checking highest key and going with range query could lead to more values being recalculated than
                 * allowed by maxRecursionCount. Also going with the range query directly against the file system might
                 * be slower than continuing with the search here
                 */
            }
        }
        if (minRecursionIdx < 0 || recursionKeys.isEmpty()) {
            //we did not find any previous value to continue from, so start over from scratch
            return getFullRecursionKeysIterator(previousKey);
        } else {
            final FDate tailRecursionKey = recursionKeys.getTail();
            if (!lastRecursionKey.equals(tailRecursionKey)) {
                throw new IllegalStateException("lastRecursionKey[" + lastRecursionKey
                        + "] should be equal to tailRecursionKey[" + tailRecursionKey + "]");
            }
            return recursionKeys;
        }
    }

    private IBufferingIterator<FDate> getFullRecursionKeysIterator(final FDate from) {
        final IBufferingIterator<FDate> iterator = newFullRecursionKeysIterator(from);
        if (iterator == null) {
            firstRecursionKey = null;
            return null;
        }
        try {
            firstRecursionKey = iterator.getHead();
            final TimeRange timeRange = new TimeRange(firstRecursionKey, from);
            if (timeRange.getDuration().intValue(FTimeUnit.YEARS) > 1) {
                largeRecalculationsCount++;
                if (largeRecalculationsCount % LARGE_RECALCULATION_WARNING_THRESHOLD == 0) {
                    //CHECKSTYLE:OFF
                    LOG.warn(
                            "{}: Recalculating recursively for the {}. time over more than a year [{}]. If this happens too often this might have a negative impact on performance.",
                            parent, timeRange, largeRecalculationsCount);
                    //CHECKSTYLE:ON
                }
            }
            return iterator;
        } catch (final NoSuchElementException e) {
            firstRecursionKey = null;
            return null;
        }
    }

    protected BufferingIterator<FDate> newFullRecursionKeysIterator(final FDate from) {
        if (shouldUseInitialValueInsteadOfFullRecursion()) {
            return null;
        } else {
            //keep lock open as short as possible
            return new BufferingIterator<FDate>(parentQueryWithFuture.getPreviousKeys(from, recursionCount));
        }
    }

    protected FDate getFirstAvailableKey() {
        if (firstAvailableKey == null && !firstAvailableKeyRequested) {
            this.firstAvailableKey = parentQueryWithFuture.getKey(FDate.MIN_DATE);
            firstAvailableKeyRequested = true;
        }
        return firstAvailableKey;
    }

    protected boolean shouldUseInitialValueInsteadOfFullRecursion() {
        return ARecursiveHistoricalCacheQuery.DEFAULT_SHOULD_USE_INITIAL_VALUE_INSTEAD_OF_FULL_RECURSION;
    }

    protected abstract V getInitialValue(FDate previousKey);

}
