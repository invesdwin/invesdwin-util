package de.invesdwin.util.collections.iterable;

import java.io.IOException;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

@Immutable
public class EmptyCloseableIterator<E> implements ICloseableIterator<E> {

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E next() {
        throw new NoSuchElementException();
    }

    @Override
    public void close() throws IOException {}

}
