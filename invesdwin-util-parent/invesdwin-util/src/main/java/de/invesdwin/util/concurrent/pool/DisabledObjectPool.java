package de.invesdwin.util.concurrent.pool;

import javax.annotation.concurrent.Immutable;

@Immutable
public class DisabledObjectPool<E> implements ICloseableObjectPool<E> {

    @SuppressWarnings({ "rawtypes" })
    private static final DisabledObjectPool INSTANCE = new DisabledObjectPool<>();

    @SuppressWarnings("unchecked")
    public static <T> DisabledObjectPool<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public E borrowObject() {
        return null;
    }

    @Override
    public void returnObject(final E element) {}

    @Override
    public void clear() {}

    @Override
    public void invalidateObject(final E element) {}

    @Override
    public void close() {}

}
