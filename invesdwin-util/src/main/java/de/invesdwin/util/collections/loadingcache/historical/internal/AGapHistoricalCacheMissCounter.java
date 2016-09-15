package de.invesdwin.util.collections.loadingcache.historical.internal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AGapHistoricalCache;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public abstract class AGapHistoricalCacheMissCounter<V> {

    private static final int OPTIMAL_MULTIPLICATOR = 2;
    private static final int MAX_SUCCESSIVE_CACHE_EVICTIONS = 2;
    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(AGapHistoricalCacheMissCounter.class);

    private int successiveCacheEvictions = 0;
    private FDate successiveCacheEvictionsToMinKey = FDate.MAX_DATE;
    private FDate successiveCacheEvictionsFromMaxKey;
    private int maxSuccessiveCacheEvictions = 1;

    private Integer optimiumMaximumSize;
    private long optimiumReadBackStepMillis;

    public AGapHistoricalCacheMissCounter() {
        optimiumMaximumSize = getMaximumSize();
        final long readBackStepMillis = getReadBackStepMillis();
        if (readBackStepMillis <= 0) {
            throw new IllegalStateException(
                    "getReadBackStepMillis needs to return a positive value: " + readBackStepMillis);
        }
        optimiumReadBackStepMillis = readBackStepMillis;
    }

    public void checkSuccessiveCacheEvictions(final FDate key) {
        if (key.isBeforeOrEqual(successiveCacheEvictionsToMinKey)) {
            if (successiveCacheEvictionsFromMaxKey == null) {
                successiveCacheEvictionsFromMaxKey = key;
            }
            successiveCacheEvictions++;
        } else {
            maxSuccessiveCacheEvictions = Math.max(maxSuccessiveCacheEvictions, successiveCacheEvictions);
            if (successiveCacheEvictions >= MAX_SUCCESSIVE_CACHE_EVICTIONS) {
                maybeReoptimize();
            }
            successiveCacheEvictions = 0;
            successiveCacheEvictionsFromMaxKey = key;
        }
        successiveCacheEvictionsToMinKey = key;
    }

    private void maybeReoptimize() {
        final long currentReadBackStepMillis = optimiumReadBackStepMillis;
        final Integer currentMaximumSize = optimiumMaximumSize;
        final long newOptimalReadBackStepMillis = determineNewOptimalReadBackStepMillis();
        boolean changed = false;
        if (newOptimalReadBackStepMillis > currentReadBackStepMillis) {
            optimiumReadBackStepMillis = newOptimalReadBackStepMillis;
            changed = true;
        }
        if (currentMaximumSize != null && currentMaximumSize > 0) {
            final int newOptimalMaximumSize = determineNewOptimalMaximumSize();
            if (newOptimalMaximumSize > currentMaximumSize) {
                optimiumMaximumSize = newOptimalMaximumSize;
                increaseOptimalMaximumSize(newOptimalMaximumSize);
                changed = true;
            }
        }
        if (changed && isDebugAutomaticReoptimization()) {
            warn(currentReadBackStepMillis, currentMaximumSize);
        }
    }

    protected abstract boolean isDebugAutomaticReoptimization();

    private void warn(final long currentReadBackStepMillis, final Integer currentMaximumSize) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(AGapHistoricalCache.class.getSimpleName() + "[" + parentToString()
                    + "]: automatically adjusting getMaximumSize[current=" + currentMaximumSize + "|newOptimum="
                    + optimiumMaximumSize + "] and getReadBackStepMillis[current=" + currentReadBackStepMillis + "/"
                    + new Duration(currentReadBackStepMillis, FTimeUnit.MILLISECONDS) + "|newOptimum="
                    + optimiumReadBackStepMillis + "/"
                    + new Duration(optimiumReadBackStepMillis, FTimeUnit.MILLISECONDS)
                    + "] for optimal performance. Encountered " + successiveCacheEvictions
                    + " successive lookback reloads due to cache evictions between: "
                    + successiveCacheEvictionsFromMaxKey + " -> " + successiveCacheEvictionsToMinKey + " = "
                    + new Duration(successiveCacheEvictionsToMinKey, successiveCacheEvictionsFromMaxKey));
        }
    }

    protected abstract String parentToString();

    protected abstract void increaseOptimalMaximumSize(int optimalMaximumSize);

    public long getOptimalReadBackStepMillis() {
        return optimiumReadBackStepMillis;
    }

    protected abstract Integer getMaximumSize();

    protected abstract long getReadBackStepMillis();

    private int determineNewOptimalMaximumSize() {
        FDate curMaxDate = successiveCacheEvictionsToMinKey;
        int newOptimalMaximumSize = 0;
        while (curMaxDate.isBefore(successiveCacheEvictionsFromMaxKey)) {
            final Iterable<? extends V> readAllValues = readAllValuesAscendingFrom(curMaxDate);
            final FDate prevMaxDate = curMaxDate;
            for (final V v : readAllValues) {
                newOptimalMaximumSize++;
                curMaxDate = extractKey(v);
            }
            if (prevMaxDate.equals(curMaxDate)) {
                break;
            }
        }
        return newOptimalMaximumSize * OPTIMAL_MULTIPLICATOR;
    }

    private long determineNewOptimalReadBackStepMillis() {
        return getReadBackStepMillis() * maxSuccessiveCacheEvictions * OPTIMAL_MULTIPLICATOR;
    }

    protected abstract FDate extractKey(V v);

    protected abstract Iterable<? extends V> readAllValuesAscendingFrom(final FDate curMaxDate);

}
