package de.invesdwin.util.collections.iterable.buffer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
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
        Assertions.checkNotNull(fromDate);
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.retrievalCount = retrievalCount;
    }

    @Override
    public ICloseableIterator<T> iterator() {
        return new ICloseableIterator<T>() {

            private FDate curDate = fromDate;
            private IBufferingIterator<? extends T> curList = new ADelegateBufferingIterator<T>() {
                @Override
                protected IBufferingIterator<? extends T> newDelegate() {
                    return queryNext(curDate, getFirstRetrievalCount());
                }
            };
            /*
             * If we got more than requested, then we might have gotten the whole dataset (or something is buggy
             * downstream). In that case it is ok to not request another list.
             */
            private boolean wasFullResponse = curList.size() == getFirstRetrievalCount();

            private IBufferingIterator<? extends T> getList() {
                if (curList == null) {
                    return null;
                }
                if (curList.isEmpty()) {
                    final FDate nextDate = curDate.addMilliseconds(1);
                    if (!wasFullResponse || nextDate.isAfter(toDate)) {
                        close(); // end reached
                    } else {
                        curList = queryNext(nextDate, retrievalCount);
                        if (curList.isEmpty()) {
                            wasFullResponse = false;
                            close();
                        } else {
                            wasFullResponse = curList.size() == retrievalCount;
                        }
                    }
                }
                return curList;
            }

            private IBufferingIterator<? extends T> queryNext(final FDate fromDate, final Integer retrievalCount) {
                final IBufferingIterator<? extends T> list = query(fromDate, toDate, retrievalCount);
                if (retrievalCount != null && list.size() > retrievalCount) {
                    throw new IllegalStateException(
                            "Got more results [" + list.size() + "] than requested [" + retrievalCount + "]");
                }
                return list;
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
                    throw new FastNoSuchElementException("ABufferingRetrievalCloseableIterable: list is null");
                }
                final T next = list.next();
                final FDate nextDate = extractTime(next);
                if (nextDate == null) {
                    throw new IllegalStateException("nextDate is null for [" + next + "]");
                }
                if (!curDate.equals(fromDate) && curDate.equals(nextDate)) {
                    close();
                    throw new FastNoSuchElementException(
                            "ABufferingRetrievalCloseableIterable: nextDate is same as curDate");
                } else {
                    curDate = nextDate;
                    return next;
                }
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