package de.invesdwin.util.concurrent.future;

import java.util.concurrent.Future;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DelegateFuture<E> extends ADelegateFuture<E> {

    public DelegateFuture(final Future<E> delegate) {
        super(delegate);
    }

    @Override
    protected Future<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
