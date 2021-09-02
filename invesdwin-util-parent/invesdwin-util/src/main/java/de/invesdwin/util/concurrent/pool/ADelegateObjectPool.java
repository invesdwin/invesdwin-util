package de.invesdwin.util.concurrent.pool;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ADelegateObjectPool<E> implements IObjectPool<E> {

    protected abstract IObjectPool<E> getDelegate();

    @Override
    public E borrowObject() {
        return getDelegate().borrowObject();
    }

    @Override
    public void returnObject(final E element) {
        getDelegate().returnObject(element);
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

}
