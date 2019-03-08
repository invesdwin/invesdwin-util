package de.invesdwin.util.collections.loadingcache.historical.query.recursive.internal;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
final class FullRecursionKeysCache extends AHistoricalCache<FullRecursionKeysResult> {
    private final AHistoricalCache<?> parent;
    private final int fullRecursionCount;
    private final IHistoricalCacheQuery<?> parentQueryWithFutureNull;
    private final AContinuousRecursiveHistoricalCacheQuery<FullRecursionKeysResult> recursiveQuery;

    FullRecursionKeysCache(final AHistoricalCache<?> parent, final int fullRecursionCount) {
        this.parent = parent;
        this.parentQueryWithFutureNull = parent.query().withFutureNull();
        this.fullRecursionCount = fullRecursionCount;
        setShiftKeyDelegate(parent, true);
        setAdjustKeyProvider(parent.getAdjustKeyProvider());
        this.recursiveQuery = new AContinuousRecursiveHistoricalCacheQuery<FullRecursionKeysResult>(this,
                fullRecursionCount) {

            @Override
            protected FullRecursionKeysResult getInitialValue(final FDate previousKey) {
                return new FullRecursionKeysResult(previousKey, fullRecursionCount, parent, parentQueryWithFutureNull)
                        .maybeInit();
            }

            @Override
            protected boolean shouldUseInitialValueInsteadOfFullRecursion() {
                //calculate the initial value
                return true;
            }
        };
    }

    @Override
    protected FullRecursionKeysResult loadValue(final FDate key) {
        final FDate previousKey = parentQueryWithFutureNull.getPreviousKey(key, 1);
        if (previousKey == null) {
            return new FullRecursionKeysResult(key, fullRecursionCount, parent, parentQueryWithFutureNull);
        } else {
            final FullRecursionKeysResult previousValue = recursiveQuery.getPreviousValue(key, previousKey);
            return previousValue.pushToNext(key);
        }
    }

}
