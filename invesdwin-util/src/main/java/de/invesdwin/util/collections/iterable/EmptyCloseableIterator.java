package de.invesdwin.util.collections.iterable;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

@Immutable
public class EmptyCloseableIterator<E> extends ACloseableIterator<E> {

    @Override
    protected boolean innerHasNext() {
        return false;
    }

    @Override
    protected E innerNext() {
        throw new NoSuchElementException();
    }

    @Override
    protected void innerClose() {}

}
