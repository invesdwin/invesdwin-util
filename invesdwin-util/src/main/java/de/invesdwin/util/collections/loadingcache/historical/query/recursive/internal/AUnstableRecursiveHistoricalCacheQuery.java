package de.invesdwin.util.collections.loadingcache.historical.query.recursive.internal;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.IRecursiveHistoricalCacheQuery;
import de.invesdwin.util.time.fdate.FDate;

/**
 * This variation calculates the values accoring to recursionCount+unstableRecursionCount for each request. This makes
 * the calculation independent of the actual available history. The unstableRecursionCount should be chosen as small as
 * possible to not become a too large hit on performance.
 */
@ThreadSafe
public abstract class AUnstableRecursiveHistoricalCacheQuery<V> implements IRecursiveHistoricalCacheQuery<V> {

    private final AHistoricalCache<V> parent;
    private final int recursionCount;
    private final int unstableRecursionCount;

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
    }

    @Override
    public void clear() {}

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
        return null;
    }

    protected abstract V getInitialValue(FDate previousKey);

    protected boolean shouldUseInitialValueInsteadOfFullRecursion() {
        return false;
    }

}
