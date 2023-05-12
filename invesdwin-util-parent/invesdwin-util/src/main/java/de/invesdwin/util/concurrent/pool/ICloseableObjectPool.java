package de.invesdwin.util.concurrent.pool;

import java.io.Closeable;

public interface ICloseableObjectPool<E> extends IObjectPool<E>, Closeable {

    @Override
    void close();

}
