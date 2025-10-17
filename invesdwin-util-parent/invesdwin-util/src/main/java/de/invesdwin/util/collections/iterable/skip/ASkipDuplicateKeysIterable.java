package de.invesdwin.util.collections.iterable.skip;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.time.date.FDate;

@Immutable
public abstract class ASkipDuplicateKeysIterable<E> implements ICloseableIterable<E> {

    protected final ICloseableIterable<? extends E> delegate;

    public ASkipDuplicateKeysIterable(final ICloseableIterable<? extends E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new ASkipDuplicateKeysIterator<E>(delegate.iterator()) {

            @Override
            protected FDate extractEndTime(final E value) {
                return ASkipDuplicateKeysIterable.this.extractEndTime(value);
            }

            @Override
            protected boolean isKeepLastDuplicate() {
                return ASkipDuplicateKeysIterable.this.isKeepLastDuplicate();
            }

        };
    }

    protected boolean isKeepLastDuplicate() {
        return ASkipDuplicateKeysIterator.DEFAULT_KEEP_LAST_DUPLICATE;
    }

    protected abstract FDate extractEndTime(E value);

}
