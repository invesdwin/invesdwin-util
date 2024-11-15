package de.invesdwin.util.concurrent.pool;

import javax.annotation.concurrent.Immutable;

@Immutable
public class SingletonObjectPool<E> implements IObjectPool<E> {

    protected final E singleton;
    private int size;

    public SingletonObjectPool(final E singleton) {
        this.singleton = singleton;
        if (singleton == null) {
            size = 0;
        } else {
            size = 1;
        }
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

    @Override
    public int size() {
        return size;
    }

}
