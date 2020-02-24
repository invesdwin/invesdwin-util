package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.range.TimeRange;

@NotThreadSafe
public abstract class ATimeRangeSkippingIterable<E> implements ICloseableIterable<E> {

    protected final ICloseableIterable<? extends E> delegate;
    protected final FDate from;
    protected final FDate to;

    public ATimeRangeSkippingIterable(final TimeRange timeRange, final ICloseableIterable<? extends E> delegate) {
        this(timeRange.getFrom(), timeRange.getTo(), delegate);
    }

    public ATimeRangeSkippingIterable(final FDate from, final FDate to,
            final ICloseableIterable<? extends E> delegate) {
        this.from = from;
        this.to = to;
        this.delegate = delegate;
    }

    protected abstract FDate extractEndTime(E element);

    protected abstract boolean isReverse();

    @Override
    public ICloseableIterator<E> iterator() {
        if (isReverse()) {
            return newIteratorReverse();
        } else {
            return newIterator();
        }
    }

    @SuppressWarnings("unchecked")
    private ICloseableIterator<E> newIterator() {
        if (from == null && to == null) {
            return (ICloseableIterator<E>) delegate.iterator();
        } else if (from != null && to != null) {
            return new ASkippingIterator<E>(delegate.iterator()) {
                @Override
                protected boolean skip(final E element) {
                    final FDate time = extractEndTime(element);
                    if (time.isBeforeNotNullSafe(from)) {
                        return true;
                    }
                    if (time.isAfterNotNullSafe(to)) {
                        throw new FastNoSuchElementException(getName() + " end reached");
                    }
                    return false;
                }
            };
        } else if (from != null) {
            return new ASkippingIterator<E>(delegate.iterator()) {
                @Override
                protected boolean skip(final E element) {
                    final FDate time = extractEndTime(element);
                    if (time.isBeforeNotNullSafe(from)) {
                        return true;
                    }
                    return false;
                }
            };
        } else if (to != null) {
            return new ASkippingIterator<E>(delegate.iterator()) {
                @Override
                protected boolean skip(final E element) {
                    final FDate time = extractEndTime(element);
                    if (time.isAfterNotNullSafe(to)) {
                        throw new FastNoSuchElementException(getName() + " end reached");
                    }
                    return false;
                }
            };
        } else {
            throw new IllegalStateException("missing another condition?");
        }
    }

    protected abstract String getName();

    @SuppressWarnings("unchecked")
    private ICloseableIterator<E> newIteratorReverse() {
        if (from == null && to == null) {
            return (ICloseableIterator<E>) delegate.iterator();
        } else if (from != null && to != null) {
            return new ASkippingIterator<E>(delegate.iterator()) {
                @Override
                protected boolean skip(final E element) {
                    final FDate time = extractEndTime(element);
                    if (time.isAfterNotNullSafe(from)) {
                        return true;
                    }
                    if (time.isBeforeNotNullSafe(to)) {
                        throw new FastNoSuchElementException(getName() + " end reached");
                    }
                    return false;
                }
            };
        } else if (from != null) {
            return new ASkippingIterator<E>(delegate.iterator()) {
                @Override
                protected boolean skip(final E element) {
                    final FDate time = extractEndTime(element);
                    if (time.isAfterNotNullSafe(from)) {
                        return true;
                    }
                    return false;
                }
            };
        } else if (to != null) {
            return new ASkippingIterator<E>(delegate.iterator()) {
                @Override
                protected boolean skip(final E element) {
                    final FDate time = extractEndTime(element);
                    if (time.isBeforeNotNullSafe(to)) {
                        throw new FastNoSuchElementException(getName() + " end reached");
                    }
                    return false;
                }
            };
        } else {
            throw new IllegalStateException("missing another condition?");
        }
    }

}
