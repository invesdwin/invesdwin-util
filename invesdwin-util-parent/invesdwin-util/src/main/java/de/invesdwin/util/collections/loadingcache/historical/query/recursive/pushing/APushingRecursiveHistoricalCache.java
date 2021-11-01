package de.invesdwin.util.collections.loadingcache.historical.query.recursive.pushing;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.ARecursiveHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.IRecursiveHistoricalCacheQuery;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
public abstract class APushingRecursiveHistoricalCache<R extends APushingRecursiveHistoricalResult<?, ?, R>>
        extends AHistoricalCache<R> {
    protected final AHistoricalCache<?> parent;
    protected final int fullRecursionCount;
    protected final IHistoricalCacheQuery<?> parentQuery;
    protected final IRecursiveHistoricalCacheQuery<R> recursiveQuery;

    public APushingRecursiveHistoricalCache(final AHistoricalCache<?> parent, final int fullRecursionCount) {
        this.parent = parent;
        this.fullRecursionCount = fullRecursionCount;
        this.parentQuery = parent.query().setFutureNullEnabled();
        setShiftKeyDelegate(parent, true);
        setAdjustKeyProvider(parent.getAdjustKeyProvider());
        this.recursiveQuery = new ARecursiveHistoricalCacheQuery<R>(this, fullRecursionCount, null) {

            @Override
            protected R getInitialValue(final FDate previousKey) {
                return newResult(previousKey, null, recursiveQuery).maybeInit();
            }

            @Override
            protected boolean shouldUseInitialValueInsteadOfFullRecursion() {
                //calculate the initial value
                return true;
            }
        };
    }

    @Override
    public boolean isThreadSafe() {
        return parent.isThreadSafe();
    }

    protected abstract R newResult(FDate key, FDate previousKey, IRecursiveHistoricalCacheQuery<R> recursiveQuery);

    @Override
    protected IEvaluateGenericFDate<R> newLoadValue() {
        return pKey -> {
            final FDate key = pKey.asFDate();
            final FDate previousKey = parentQuery.getPreviousKey(key, 1);
            if (previousKey == null) {
                return newResult(key, previousKey, recursiveQuery);
            } else {
                final R previousValue = recursiveQuery.getPreviousValue(key, previousKey);
                return previousValue.pushToNext(key);
            }
        };
    }

}
