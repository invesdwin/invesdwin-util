package de.invesdwin.util.collections.iterable;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class SingleValueIterator<E> extends ACloseableIterator<E> {

    private E singleValue;

    public SingleValueIterator(final E singleValue) {
        this.singleValue = singleValue;
    }

    @Override
    protected boolean innerHasNext() {
        return singleValue != null;
    }

    @Override
    protected E innerNext() {
        if (hasNext()) {
            final E ret = singleValue;
            singleValue = null;
            return ret;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    protected void innerClose() {
        singleValue = null;
    }

}
