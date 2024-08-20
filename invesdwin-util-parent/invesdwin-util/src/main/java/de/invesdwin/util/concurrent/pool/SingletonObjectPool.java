package de.invesdwin.util.concurrent.pool;

import javax.annotation.concurrent.Immutable;

@Immutable
public class SingletonObjectPool<E> implements IObjectPool<E> {

    protected final E singleton;

    public SingletonObjectPool(final E singleton) {
        this.singleton = singleton;
    }

    @Override
    public E borrowObject() {
        return singleton;
    }

    @Override
    public void returnObject(final E element) {}

    @Override
    public void clear() {}

    @Override
    public void invalidateObject(final E element) {}

}
