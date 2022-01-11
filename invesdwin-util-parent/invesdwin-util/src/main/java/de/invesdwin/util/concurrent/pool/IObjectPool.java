package de.invesdwin.util.concurrent.pool;

public interface IObjectPool<E> {

    E borrowObject();

    void returnObject(E element);

    void clear();

    void invalidateObject(E element);

}
