package de.invesdwin.util.collections.loadingcache.historical.query.recursive.internal;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.IRecursiveHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.recursive.pushing.APushingRecursiveHistoricalResult;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
final class FullRecursionKeysResult
        extends APushingRecursiveHistoricalResult<BufferingIterator<FDate>, FDate, FullRecursionKeysResult> {

    private final IHistoricalCache<?> parent;
    private final int fullRecursionCount;
    private final IHistoricalCacheQuery<?> parentQuery;

    FullRecursionKeysResult(final IHistoricalCache<?> parent, final FDate key, final FDate previousKey,
            final IRecursiveHistoricalCacheQuery<FullRecursionKeysResult> recursiveQuery, final int fullRecursionCount,
            final IHistoricalCacheQuery<?> parentQuery) {
        super(adjustKey(parent, key), adjustKey(parent, previousKey), recursiveQuery);
        this.parent = parent;
        this.fullRecursionCount = fullRecursionCount;
        this.parentQuery = parentQuery;
    }

    protected static IndexedFDate adjustKey(final IHistoricalCache<?> parent, final FDate key) {
        if (key == null) {
            return null;
        }
        return IndexedFDate.maybeWrap(key)
                .putExtractedKey(parent.getExtractKeyProvider(), parent.getAdjustKeyProvider());
    }

    public synchronized ICloseableIterator<FDate> getFullRecursionKeys() {
        maybeInit();
        return data.snapshot().iterator();
    }

    @Override
    protected BufferingIterator<FDate> initData() {
        final BufferingIterator<FDate> data = new BufferingIterator<>();
        try (ICloseableIterator<FDate> values = parentQuery.getPreviousKeys(key, fullRecursionCount).iterator()) {
            while (true) {
                final FDate next = values.next();
                final IndexedFDate indexedTime = adjustKey(parent, next);
                data.add(indexedTime);
            }
        } catch (final NoSuchElementException e) {
            //ignore
        }
        return data;
    }

    @Override
    protected void appendEntry(final FDate nextEntry) {
        final IndexedFDate indexedTime = adjustKey(parent, nextEntry);
        data.add(indexedTime);
        //trim
        while (data.size() > fullRecursionCount) {
            data.next();
        }
    }

    @Override
    public IHistoricalEntry<? extends FullRecursionKeysResult> asHistoricalEntry() {
        return new IHistoricalEntry<FullRecursionKeysResult>() {

            @Override
            public FDate getKey() {
                return FullRecursionKeysResult.this.key;
            }

            @Override
            public FullRecursionKeysResult getValue() {
                return FullRecursionKeysResult.this;
            }

            @Override
            public String toString() {
                return getKey() + " -> " + getValue();
            }
        };
    }

    @Override
    protected FDate getEntry(final FDate key) {
        return parentQuery.getKey(key);
    }

    @Override
    protected FDate extractKey(final FDate entry) {
        return entry;
    }

    @Override
    protected FullRecursionKeysResult getGenericThis() {
        return this;
    }

    @Override
    protected FullRecursionKeysResult newResult(final FDate key, final FDate previousKey,
            final IRecursiveHistoricalCacheQuery<FullRecursionKeysResult> recursiveQuery) {
        return new FullRecursionKeysResult(parent, key, previousKey, recursiveQuery, fullRecursionCount, parentQuery);
    }

    @Override
    protected boolean isEmpty(final BufferingIterator<FDate> data) {
        return data.isEmpty();
    }

}