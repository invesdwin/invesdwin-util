package de.invesdwin.util.collections.loadingcache.historical.query.recursive.pushing;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalValue;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.IRecursiveHistoricalCacheQuery;
import de.invesdwin.util.concurrent.reference.IMutableReference;
import de.invesdwin.util.concurrent.reference.MutableSoftReference;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
public abstract class APushingRecursiveHistoricalResult<D, E, R extends APushingRecursiveHistoricalResult<D, E, R>>
        implements IHistoricalValue<R> {

    public static final int MIN_RECURSION_COUNT = 10;
    //10k causes stakcoverflow errors
    public static final int MAX_RECURSION_COUNT_LIMIT = 1000;

    protected final FDate key;
    protected final FDate previousKey;
    protected final IRecursiveHistoricalCacheQuery<R> recursiveQuery;
    @GuardedBy("this")
    protected final IMutableReference<D> dataRef = new MutableSoftReference<>(null);

    public APushingRecursiveHistoricalResult(final FDate key, final FDate previousKey,
            final IRecursiveHistoricalCacheQuery<R> recursiveQuery) {
        this.key = key;
        this.previousKey = previousKey;
        this.recursiveQuery = recursiveQuery;
    }

    public final D maybeInit() {
        return maybeInit(0, getMaxRecursionCount());
    }

    protected int getMaxRecursionCount() {
        return Integers.between(recursiveQuery.getRecursionCount(), MIN_RECURSION_COUNT, MAX_RECURSION_COUNT_LIMIT);
    }

    public final synchronized D maybeInit(final int recursionCount, final int maxRecursionCount) {
        final D value = dataRef.get();
        if (value != null) {
            return value;
        }
        if (previousKey != null && !key.equalsNotNullSafe(previousKey) && recursionCount < maxRecursionCount) {
            final R previousValue = recursiveQuery.getPreviousValueIfPresent(key, previousKey);
            if (previousValue != null && previousValue != this) {
                previousValue.maybeInit(recursionCount + 1, maxRecursionCount);
                final D prevValue = previousValue.pushToNext(key, previousKey).dataRef.get();
                if (prevValue != null) {
                    dataRef.set(prevValue);
                    return prevValue;
                }
            }
        }

        final D initValue = initData();
        dataRef.set(initValue);
        return initValue;
    }

    protected abstract D initData();

    public final synchronized R pushToNext(final FDate key, final FDate previousKey) {
        if (key.equals(this.key)) {
            return getGenericThis();
        }
        if (!key.isAfterNotNullSafe(this.key)) {
            //            throw new IllegalArgumentException("key [" + key + "] should be after [" + this.key + "]");
            //might happen in blackboard engine after initialization, thus give a clean new result
            return newResult(key, previousKey, recursiveQuery);
        }
        final E nextEntry = getEntry(key);
        if (nextEntry == null) {
            //no data available yet
            return getGenericThis();
        }
        final FDate nextEntryKey = extractKey(nextEntry);
        if (nextEntryKey == null) {
            //no data available yet
            return getGenericThis();
        }
        if (nextEntryKey.isBeforeOrEqualToNotNullSafe(this.key)) {
            //most likely a closing tick
            return getGenericThis();
        }
        if (nextEntryKey.equalsNotNullSafe(this.key)) {
            return getGenericThis();
        }
        if (isEmpty()) {
            return newResult(key, this.key, recursiveQuery);
        } else {
            final D value = dataRef.get();
            if (value != null) {
                pushEntry(value, nextEntry);
                final R nextResult = newResult(key, this.key, recursiveQuery);
                nextResult.dataRef.set(value);
                dataRef.set(null);
                return nextResult;
            } else {
                return newResult(key, this.key, recursiveQuery);
            }
        }
    }

    public final boolean isEmpty() {
        final D value = dataRef.get();
        return value == null || isEmpty(value);
    }

    protected abstract void pushEntry(D data, E nextEntry);

    protected abstract E getEntry(FDate key);

    protected abstract FDate extractKey(E entry);

    protected abstract R newResult(FDate key, FDate previousKey, IRecursiveHistoricalCacheQuery<R> recursiveQuery);

    @SuppressWarnings("unchecked")
    protected R getGenericThis() {
        return (R) this;
    }

    protected abstract boolean isEmpty(D data);

    @Override
    public IHistoricalEntry<? extends R> asHistoricalEntry() {
        return new IHistoricalEntry<R>() {

            @Override
            public FDate getKey() {
                return APushingRecursiveHistoricalResult.this.key;
            }

            @Override
            public R getValue() {
                return getGenericThis();
            }

            @Override
            public String toString() {
                return getKey() + " -> " + getValue();
            }
        };
    }

}