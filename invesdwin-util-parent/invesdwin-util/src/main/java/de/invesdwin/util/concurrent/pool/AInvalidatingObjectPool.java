package de.invesdwin.util.concurrent.pool;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AInvalidatingObjectPool<E> implements IObjectPool<E> {

    @Override
    public final E borrowObject() {
        return newObject();
    }

    protected abstract E newObject();

    @Override
    public void returnObject(final E element) {
        invalidateObject(element);
    }

    @Override
    public final void clear() {
    }

    @Override
    public void invalidateObject(final E element) {
    }

}
