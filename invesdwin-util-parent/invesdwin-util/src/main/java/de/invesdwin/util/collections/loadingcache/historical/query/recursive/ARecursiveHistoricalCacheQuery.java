package de.invesdwin.util.collections.loadingcache.historical.query.recursive;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.internal.AContinuousRecursiveHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.internal.AUnstableRecursiveHistoricalCacheQuery;
import de.invesdwin.util.time.fdate.FDate;

/**
 * This class can be used inside of historical caches to retrieve values from itself recursively. It solves the infinite
 * recursion problem by falling back to an initial value when the maximum amount of recursions is reached.
 * 
 * For example useful when calculating an exponential moving average that needs access to its previous values.
 * 
 * When using unstableRecursionCount == null, a continuous and fast algorithm will be used to get values from the cache
 * where possible.
 * 
 * When using unstableRecursionCount != null, an unstable algorithm will be used that is more independent of the
 * available history but slower because all values are computed each time for the lookback of
 * recursionCount+unstableRecursionCount. One should choose an unstableRecursionCount as small as possible in order to
 * not get a too large performance hit.
 * 
 * @param <V>
 */
@ThreadSafe
public abstract class ARecursiveHistoricalCacheQuery<V> implements IRecursiveHistoricalCacheQuery<V> {

    public static final boolean DEFAULT_SHOULD_USE_INITIAL_VALUE_INSTEAD_OF_FULL_RECURSION = false;
    private IRecursiveHistoricalCacheQuery<V> delegate;

    public ARecursiveHistoricalCacheQuery(final AHistoricalCache<V> parent, final int recursionCount,
            final Integer unstableRecursionCount) {
        if (unstableRecursionCount == null) {
            this.delegate = new AContinuousRecursiveHistoricalCacheQuery<V>(parent, recursionCount) {
                @Override
                protected V getInitialValue(final FDate previousKey) {
                    return ARecursiveHistoricalCacheQuery.this.getInitialValue(previousKey);
                }

                @Override
                protected boolean shouldUseInitialValueInsteadOfFullRecursion() {
                    return ARecursiveHistoricalCacheQuery.this.shouldUseInitialValueInsteadOfFullRecursion();
                }
            };
        } else {
            this.delegate = new AUnstableRecursiveHistoricalCacheQuery<V>(parent, recursionCount,
                    unstableRecursionCount) {

                @Override
                protected V getInitialValue(final FDate previousKey) {
                    return ARecursiveHistoricalCacheQuery.this.getInitialValue(previousKey);
                }

                @Override
                protected boolean shouldUseInitialValueInsteadOfFullRecursion() {
                    return ARecursiveHistoricalCacheQuery.this.shouldUseInitialValueInsteadOfFullRecursion();
                }
            };
        }
    }

    protected boolean shouldUseInitialValueInsteadOfFullRecursion() {
        return DEFAULT_SHOULD_USE_INITIAL_VALUE_INSTEAD_OF_FULL_RECURSION;
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int getRecursionCount() {
        return delegate.getRecursionCount();
    }

    @Override
    public Integer getUnstableRecursionCount() {
        return delegate.getUnstableRecursionCount();
    }

    @Override
    public V getPreviousValue(final FDate key, final FDate previousKey) {
        return delegate.getPreviousValue(key, previousKey);
    }

    @Override
    public V getPreviousValueIfPresent(final FDate key, final FDate previousKey) {
        return delegate.getPreviousValueIfPresent(key, previousKey);
    }

    protected abstract V getInitialValue(FDate previousKey);

    public static int newContinuousUnstablePeriod(final int recursionCount,
            final boolean shouldUseInitialValueInsteadOfFullRecursion) {
        return AContinuousRecursiveHistoricalCacheQuery.newContinuousUnstablePeriod(recursionCount,
                shouldUseInitialValueInsteadOfFullRecursion);
    }

    @Override
    public FDate getKey(final FDate key) {
        return delegate.getKey(key);
    }

}
