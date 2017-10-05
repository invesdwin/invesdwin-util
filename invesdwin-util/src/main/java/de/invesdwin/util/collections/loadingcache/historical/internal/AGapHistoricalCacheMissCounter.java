package de.invesdwin.util.collections.loadingcache.historical.internal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AGapHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
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

    private Integer optimiumMaximumSize = getInitialMaximumSize();
    private Duration optimiumReadBackStepMillis = new Duration(getReadBackStepMillis(), FTimeUnit.MILLISECONDS);
    private Duration maxFutherValuesRange;

    public void checkSuccessiveCacheEvictions(final FDate key) {
        if (key.isBeforeOrEqualTo(successiveCacheEvictionsToMinKey)) {
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

    public void increaseMaximumSize(final int maximumSize) {
        this.optimiumMaximumSize = maximumSize;
    }

    private void maybeReoptimize() {
        final Duration currentReadBackStepMillis = optimiumReadBackStepMillis;
        final Integer currentMaximumSize = optimiumMaximumSize;
        final Duration newOptimalReadBackStepMillis = new Duration(determineNewOptimalReadBackStepMillis(),
                FTimeUnit.MILLISECONDS);
        boolean changed = false;
        if (newOptimalReadBackStepMillis.isGreaterThan(currentReadBackStepMillis)) {
            optimiumReadBackStepMillis = newOptimalReadBackStepMillis;
            changed = true;
        }
        if (currentMaximumSize != null && currentMaximumSize > 0) {
            final int newOptimalMaximumSize = determineNewOptimalMaximumSize();
            if (newOptimalMaximumSize > currentMaximumSize) {
                optimiumMaximumSize = newOptimalMaximumSize;
                increaseOptimalMaximumSize(newOptimalMaximumSize,
                        AGapHistoricalCacheMissCounter.class.getSimpleName() + " enountered too many cache misses");
                changed = true;
            }
        }
        if (changed && AHistoricalCache.isDebugAutomaticReoptimization()) {
            warn(currentReadBackStepMillis, currentMaximumSize);
        }
    }

    private void warn(final Duration currentReadBackStepMillis, final Integer currentMaximumSize) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(AGapHistoricalCache.class.getSimpleName() + "[" + parentToString()
                    + "]: automatically adjusting getMaximumSize[current=" + currentMaximumSize + "|newOptimum="
                    + optimiumMaximumSize + "] and getReadBackStepMillis[current=" + currentReadBackStepMillis + "/"
                    + currentReadBackStepMillis + "|newOptimum=" + optimiumReadBackStepMillis + "/"
                    + optimiumReadBackStepMillis + "] for optimal performance. Encountered " + successiveCacheEvictions
                    + " successive lookback reloads due to cache evictions between: "
                    + successiveCacheEvictionsFromMaxKey + " -> " + successiveCacheEvictionsToMinKey + " = "
                    + new Duration(successiveCacheEvictionsToMinKey, successiveCacheEvictionsFromMaxKey));
        }
    }

    protected abstract String parentToString();

    protected abstract void increaseOptimalMaximumSize(int optimalMaximumSize, String reason);

    public long getOptimalReadBackStepMillis() {
        return optimiumReadBackStepMillis.longValue(FTimeUnit.MILLISECONDS);
    }

    protected abstract Integer getInitialMaximumSize();

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

    protected abstract Iterable<? extends V> readAllValuesAscendingFrom(FDate curMaxDate);

    public void maybeLimitOptimalReadBackStepByLoadFurtherValuesRange(final Duration duration) {
        maxFutherValuesRange = Duration.max(maxFutherValuesRange, duration);
        if (optimiumReadBackStepMillis.isGreaterThan(maxFutherValuesRange)) {
            final Duration maximumReadBackStep = maxFutherValuesRange.divide(2);
            optimiumReadBackStepMillis = maximumReadBackStep;
        }
    }

}
