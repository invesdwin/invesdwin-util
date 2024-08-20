package de.invesdwin.util.concurrent.pool;

public interface ISizedObjectPool<E> extends IObjectPool<E> {

    int size();

}
