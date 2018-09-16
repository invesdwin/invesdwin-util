package de.invesdwin.util.collections.iterable.buffer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public abstract class ABufferingRetrievalCloseableIterable<T> implements ICloseableIterable<T> {

    private final FDate fromDate;
    private final FDate toDate;
    private final Integer retrievalCount;

    public ABufferingRetrievalCloseableIterable(final FDate fromDate, final FDate toDate,
            final Integer retrievalCount) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.retrievalCount = retrievalCount;
    }

    @Override
    public ICloseableIterator<T> iterator() {
        return new ICloseableIterator<T>() {

            private FDate curDate = fromDate;
            private IBufferingIterator<? extends T> curList = queryNext(curDate, getFirstRetrievalCount());

            private IBufferingIterator<? extends T> getList() {
                if (curList == null) {
                    return null;
                }
                if (curList.isEmpty()) {
                    curList = queryNext(curDate.addMilliseconds(1), retrievalCount);
                    if (curList.isEmpty()) {
                        close();
                    }
                }
                return curList;
            }

            private IBufferingIterator<? extends T> queryNext(final FDate fromDate, final Integer retrievalCount) {
                return query(fromDate, toDate, retrievalCount);
            }

            @Override
            public boolean hasNext() {
                final IBufferingIterator<? extends T> list = getList();
                return list != null && !list.isEmpty();
            }

            @Override
            public T next() {
                final IBufferingIterator<? extends T> list = getList();
                if (list == null) {
                    throw new FastNoSuchElementException("ARedirectingCloseableIterable: list is null");
                }
                final T next = list.next();
                curDate = extractTime(next);
                return next;
            }

            @Override
            public void close() {
                curList = null;
            }
        };
    }

    protected Integer getFirstRetrievalCount() {
        return retrievalCount;
    }

    protected abstract FDate extractTime(T next);

    protected abstract IBufferingIterator<? extends T> query(FDate fromDate, FDate toDate, Integer count);
}