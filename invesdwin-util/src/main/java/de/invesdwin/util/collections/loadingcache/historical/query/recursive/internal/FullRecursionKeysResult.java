package de.invesdwin.util.collections.loadingcache.historical.query.recursive.internal;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
final class FullRecursionKeysResult {

    private final FDate key;
    private BufferingIterator<FDate> fullRecursionKeys;
    private final int fullRecursionCount;
    private final IHistoricalCacheQuery<?> parentQueryWithFutureNull;

    FullRecursionKeysResult(final FDate key, final int fullRecursionCount,
            final IHistoricalCacheQuery<?> parentQueryWithFutureNull) {
        this.key = key;
        this.fullRecursionCount = fullRecursionCount;
        this.parentQueryWithFutureNull = parentQueryWithFutureNull;
    }

    public ICloseableIterator<FDate> getFullRecursionKeys() {
        maybeInit();
        return fullRecursionKeys.iterator();
    }

    public FullRecursionKeysResult maybeInit() {
        if (fullRecursionKeys == null) {
            fullRecursionKeys = new BufferingIterator<>();
            final ICloseableIterator<FDate> values = parentQueryWithFutureNull.getPreviousKeys(key, fullRecursionCount)
                    .iterator();
            try {
                while (true) {
                    final FDate next = values.next();
                    fullRecursionKeys.add(next);
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
            return new FullRecursionKeysResult(key, fullRecursionCount, parentQueryWithFutureNull);
        } else {
            fullRecursionKeys.add(nextEntry);
            //trim
            while (fullRecursionKeys.size() > fullRecursionCount) {
                fullRecursionKeys.next();
            }
            final FullRecursionKeysResult nextResult = new FullRecursionKeysResult(key, fullRecursionCount,
                    parentQueryWithFutureNull);
            nextResult.fullRecursionKeys = fullRecursionKeys;
            fullRecursionKeys = null;
            return nextResult;
        }
    }

}