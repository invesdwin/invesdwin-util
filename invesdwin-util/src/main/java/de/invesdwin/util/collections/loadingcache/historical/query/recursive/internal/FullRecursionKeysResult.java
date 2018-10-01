package de.invesdwin.util.collections.loadingcache.historical.query.recursive.internal;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
final class FullRecursionKeysResult {

    private final FDate key;
    private BufferingIterator<FDate> fullRecursionKeys;
    private final int fullRecursionCount;
    private final AHistoricalCache<?> parent;
    private final IHistoricalCacheQuery<?> parentQueryWithFutureNull;

    FullRecursionKeysResult(final FDate key, final int fullRecursionCount, final AHistoricalCache<?> parent,
            final IHistoricalCacheQuery<?> parentQueryWithFutureNull) {
        this.key = key;
        this.fullRecursionCount = fullRecursionCount;
        this.parent = parent;
        this.parentQueryWithFutureNull = parentQueryWithFutureNull;
    }

    public ICloseableIterator<FDate> getFullRecursionKeys() {
        maybeInit();
        return fullRecursionKeys.iterator();
    }

    public FullRecursionKeysResult maybeInit() {
        if (fullRecursionKeys == null) {
            fullRecursionKeys = new BufferingIterator<>();
            try (ICloseableIterator<FDate> values = parentQueryWithFutureNull.getPreviousKeys(key, fullRecursionCount)
                    .iterator()) {
                while (true) {
                    final FDate next = values.next();
                    final IndexedFDate indexedTime = IndexedFDate.maybeWrap(next);
                    indexedTime.putExtractedKey(parent.getExtractKeyProvider(), parent.getAdjustKeyProvider());
                    fullRecursionKeys.add(indexedTime);
                }
            } catch (final NoSuchElementException e) {
                //ignore
            }
        }
        return this;
    }

    public FullRecursionKeysResult pushToNext(final FDate key) {
        if (key.equals(this.key)) {
            return this;
        }
        if (!key.isAfter(this.key)) {
            throw new IllegalArgumentException("key [" + key + "] should be after [" + this.key + "]");
        }
        final FDate nextEntry = parentQueryWithFutureNull.getKey(key);
        if (!nextEntry.isAfter(this.key)) {
            throw new IllegalArgumentException("entry.key [" + key + "] should be after [" + this.key + "]");
        }
        if (nextEntry.equals(this.key)) {
            return this;
        }
        if (fullRecursionKeys == null || fullRecursionKeys.isEmpty()) {
            return new FullRecursionKeysResult(key, fullRecursionCount, parent, parentQueryWithFutureNull);
        } else {
            final IndexedFDate indexedTime = IndexedFDate.maybeWrap(nextEntry);
            indexedTime.putExtractedKey(parent.getExtractKeyProvider(), parent.getAdjustKeyProvider());
            fullRecursionKeys.add(indexedTime);
            //trim
            while (fullRecursionKeys.size() > fullRecursionCount) {
                fullRecursionKeys.next();
            }
            final FullRecursionKeysResult nextResult = new FullRecursionKeysResult(key, fullRecursionCount, parent,
                    parentQueryWithFutureNull);
            nextResult.fullRecursionKeys = fullRecursionKeys;
            fullRecursionKeys = null;
            return nextResult;
        }
    }

}