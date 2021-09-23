package de.invesdwin.util.collections.loadingcache.historical.query.internal.filter;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.skip.ASkippingIterable;

@NotThreadSafe
abstract class AFilterSkippingIterable<E> extends ASkippingIterable<E> {

    AFilterSkippingIterable(final ICloseableIterable<? extends E> delegate) {
        super(delegate);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ICloseableIterator<E> iterator() {
        if (delegate instanceof AFilterSkippingIterable) {
            return (ICloseableIterator<E>) delegate.iterator();
        } else {
            return super.iterator();
        }
    }

}
