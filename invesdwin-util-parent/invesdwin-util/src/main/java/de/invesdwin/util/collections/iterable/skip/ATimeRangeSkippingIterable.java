package de.invesdwin.util.collections.iterable.skip;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.IReverseCloseableIterable;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.range.TimeRange;

@NotThreadSafe
public abstract class ATimeRangeSkippingIterable<E> implements ICloseableIterable<E>, IReverseCloseableIterable<E> {

    protected final ICloseableIterable<? extends E> delegate;
    protected final FDate from;
    protected final FDate to;

    public ATimeRangeSkippingIterable(final ICloseableIterable<? extends E> delegate, final TimeRange timeRange) {
        this(delegate, timeRange.getFrom(), timeRange.getTo());
    }

    public ATimeRangeSkippingIterable(final ICloseableIterable<? extends E> delegate, final FDate from,
            final FDate to) {
        this.delegate = delegate;
        this.from = from;
        this.to = to;
    }

    protected abstract FDate extractEndTime(E element);

    @SuppressWarnings("unchecked")
    @Override
    public ICloseableIterator<E> iterator() {
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
                        throw FastNoSuchElementException.getInstance("%s end reached", getName());
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
                        throw FastNoSuchElementException.getInstance("%s end reached", getName());
                    }
                    return false;
                }
            };
        } else {
            throw new IllegalStateException("missing another condition?");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ICloseableIterator<E> reverseIterator() {
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
                        throw FastNoSuchElementException.getInstance("%s end reached", getName());
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
                        throw FastNoSuchElementException.getInstance("%s end reached", getName());
                    }
                    return false;
                }
            };
        } else {
            throw new IllegalStateException("missing another condition?");
        }
    }

    protected abstract String getName();

}
