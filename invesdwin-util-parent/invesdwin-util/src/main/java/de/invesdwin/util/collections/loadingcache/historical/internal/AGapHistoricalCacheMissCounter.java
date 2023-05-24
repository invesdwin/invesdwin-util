package de.invesdwin.util.collections.loadingcache.historical.internal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AGapHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.math.stream.doubl.DoubleStreamAvg;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public abstract class AGapHistoricalCacheMissCounter<V> {

    private static final int MIN_SAMPLES_FOR_AVG_ELEMENTS_DISTANCE = AGapHistoricalCache.DEFAULT_READ_BACK_STEP_ELEMENTS;
    private static final int MAX_SAMPLES_FOR_AVG_ELEMENTS_DISTANCE = 100_000;
    private static final int OPTIMAL_MULTIPLICATOR = 2;
    private static final int MAX_SUCCESSIVE_CACHE_EVICTIONS = 2;
    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(AGapHistoricalCacheMissCounter.class);

    private int successiveCacheEvictions = 0;
    private FDate successiveCacheEvictionsToMinKey = FDates.MAX_DATE;
    private FDate successiveCacheEvictionsFromMaxKey;
    private int maxSuccessiveCacheEvictions = 1;

    private Integer optimiumMaximumSize = getInitialMaximumSize();
    private Duration optimumReadBackStepMillis = new Duration(getInitialReadBackStepMillis(), FTimeUnit.MILLISECONDS);
    private Duration maxFutherValuesRange;
    private final DoubleStreamAvg avgElementDistance = new DoubleStreamAvg();

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
        final Duration currentReadBackStepMillis = optimumReadBackStepMillis;
        final Integer currentMaximumSize = optimiumMaximumSize;
        final Duration newOptimalReadBackStepMillis = new Duration(determineNewOptimalReadBackStepMillis(),
                FTimeUnit.MILLISECONDS);
        boolean changed = false;
        if (!newOptimalReadBackStepMillis.isZero()
                && newOptimalReadBackStepMillis.isGreaterThan(currentReadBackStepMillis)) {
            optimumReadBackStepMillis = newOptimalReadBackStepMillis;
            changed = true;
        }
        if (currentMaximumSize != null && currentMaximumSize > 0) {
            final int newOptimalMaximumSize = currentMaximumSize * OPTIMAL_MULTIPLICATOR;
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
                    + currentReadBackStepMillis + "|newOptimum=" + optimumReadBackStepMillis + "/"
                    + optimumReadBackStepMillis + "] for optimal performance. Encountered " + successiveCacheEvictions
                    + " successive lookback reloads due to cache evictions between: "
                    + successiveCacheEvictionsFromMaxKey + " -> " + successiveCacheEvictionsToMinKey + " = "
                    + new Duration(successiveCacheEvictionsToMinKey, successiveCacheEvictionsFromMaxKey));
        }
    }

    protected abstract String parentToString();

    protected abstract void increaseOptimalMaximumSize(int optimalMaximumSize, String reason);

    public long getOptimalReadBackStepMillis() {
        return optimumReadBackStepMillis.longValue(FTimeUnit.MILLISECONDS);
    }

    protected abstract Integer getInitialMaximumSize();

    protected abstract long getInitialReadBackStepMillis();

    private long determineNewOptimalReadBackStepMillis() {
        final long readBackStepMillis;
        if (avgElementDistance.getCount() >= MIN_SAMPLES_FOR_AVG_ELEMENTS_DISTANCE) {
            readBackStepMillis = (long) (avgElementDistance.getAvg()
                    * AGapHistoricalCache.DEFAULT_READ_BACK_STEP_ELEMENTS);
        } else {
            readBackStepMillis = getInitialReadBackStepMillis() * OPTIMAL_MULTIPLICATOR;
        }
        return readBackStepMillis * maxSuccessiveCacheEvictions;
    }

    public void maybeLimitOptimalReadBackStepByLoadFurtherValuesRange(final Duration duration) {
        if (duration.isZero()) {
            return;
        }
        maxFutherValuesRange = Duration.max(maxFutherValuesRange, duration);
        if (optimumReadBackStepMillis.isGreaterThan(maxFutherValuesRange)) {
            final Duration maximumReadBackStep = maxFutherValuesRange.divide(2);
            optimumReadBackStepMillis = maximumReadBackStep;
        }
    }

    public void recordElementDistance(final FDate prev, final FDate next) {
        if (avgElementDistance.getCount() < MAX_SAMPLES_FOR_AVG_ELEMENTS_DISTANCE) {
            avgElementDistance.process(next.millisValue() - prev.millisValue());
        }
    }

}
