package de.invesdwin.util.collections.loadingcache.historical.query;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
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

    private final AHistoricalCache<V> parent;
    private final ThreadLocal<AtomicInteger> curRecursionCountHolder = new ThreadLocal<AtomicInteger>() {
        @Override
        protected AtomicInteger initialValue() {
            return new AtomicInteger();
        }
    };
    private final int maxRecursionCount;

    public ARecursiveHistoricalCacheQuery(final AHistoricalCache<V> parent, final int maxRecursionCount) {
        this.parent = parent;
        this.maxRecursionCount = maxRecursionCount;
    }

    public int getMaxRecursionCount() {
        return maxRecursionCount;
    }

    public int getSuggestedMaximumSizeForParent(final int maximumSize) {
        return Integers.max(maximumSize, maxRecursionCount * 2);
    }

    public V getPreviousValue(final FDate key, final FDate previousKey) {
        final V previous;
        if (parent.containsKey(previousKey)) {
            previous = newQuery(parent).getValue(previousKey);
        } else {
            final AtomicInteger curRecursionCount = curRecursionCountHolder.get();
            if (curRecursionCount.incrementAndGet() >= maxRecursionCount || key.equals(previousKey)) {
                //Start with SMA
                previous = getInitialValue(previousKey);
            } else {
                //use recursion up to the allowed limit
                previous = newQuery(parent).getValue(previousKey);
            }
            if (curRecursionCount.decrementAndGet() <= 0) {
                curRecursionCountHolder.remove();
            }
        }
        return previous;
    }

    /**
     * can be overridden to change the future data handling
     */
    protected IHistoricalCacheQuery<V> newQuery(final AHistoricalCache<V> parent) {
        return parent.query();
    }

    protected abstract V getInitialValue(FDate previousKey);

}
