package de.invesdwin.util.collections.loadingcache.historical.query.recursive.internal;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.historical.IHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.IRecursiveHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.pushing.APushingRecursiveHistoricalCache;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
final class FullRecursionKeysCache extends APushingRecursiveHistoricalCache<FullRecursionKeysResult> {

    FullRecursionKeysCache(final IHistoricalCache<?> parent, final int fullRecursionCount) {
        super(parent, fullRecursionCount);
    }

    @Override
    protected FullRecursionKeysResult newResult(final FDate key, final FDate previousKey,
            final IRecursiveHistoricalCacheQuery<FullRecursionKeysResult> recursiveQuery) {
        return new FullRecursionKeysResult(parent, key, previousKey, recursiveQuery, fullRecursionCount, parentQuery);
    }

}
