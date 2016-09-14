package de.invesdwin.util.collections.loadingcache.historical.internal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AGapHistoricalCache;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public abstract class AGapHistoricalCacheMissCounter<V> {

    private static final int OPTIMUM_MULTIPLICATOR = 2;
    private static final int MAX_SUCCESSIVE_CACHE_EVICTIONS = 2;
    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(AGapHistoricalCacheMissCounter.class);

    private int successiveCacheEvictions = 0;
    private FDate successiveCacheEvictionsToMinKey = FDate.MAX_DATE;
    private FDate successiveCacheEvictionsFromMaxKey;
    private int maxSuccessiveCacheEvictions = 1;

    private int optimiumMaximumSize = getMaximumSize();
    private long optimiumReadBackStepMillis = getReadBackStepMillis();

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
        final long newOptimumReadBackStepMillis = determineNewOptimiumReadBackStepMillis();
        boolean changed = false;
        if (newOptimumReadBackStepMillis > currentReadBackStepMillis) {
            optimiumReadBackStepMillis = newOptimumReadBackStepMillis;
            changed = true;
        }
        final int newOptimumMaximumSize = determineNewOptimiumMaximumSize();
        if (newOptimumMaximumSize > currentMaximumSize) {
            optimiumMaximumSize = newOptimumMaximumSize;
            increaseOptimumMaximumSize(newOptimumMaximumSize);
            changed = true;
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

    protected abstract void increaseOptimumMaximumSize(int optimumMaximumSize);

    public long getOptimiumReadBackStepMillis() {
        return optimiumReadBackStepMillis;
    }

    protected abstract Integer getMaximumSize();

    protected abstract long getReadBackStepMillis();

    private int determineNewOptimiumMaximumSize() {
        FDate curMaxDate = successiveCacheEvictionsToMinKey;
        int newOptimumMaximumSize = 0;
        while (curMaxDate.isBefore(successiveCacheEvictionsFromMaxKey)) {
            final Iterable<? extends V> readAllValues = readAllValuesAscendingFrom(curMaxDate);
            for (final V v : readAllValues) {
                newOptimumMaximumSize++;
                curMaxDate = extractKey(v);
            }
        }
        return newOptimumMaximumSize * OPTIMUM_MULTIPLICATOR;
    }

    private long determineNewOptimiumReadBackStepMillis() {
        return getReadBackStepMillis() * maxSuccessiveCacheEvictions * OPTIMUM_MULTIPLICATOR;
    }

    protected abstract FDate extractKey(V v);

    protected abstract Iterable<? extends V> readAllValuesAscendingFrom(final FDate curMaxDate);

}
