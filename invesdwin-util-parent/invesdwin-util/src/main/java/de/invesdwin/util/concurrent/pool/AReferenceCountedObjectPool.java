package de.invesdwin.util.concurrent.pool;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class AReferenceCountedObjectPool<E> implements IObjectPool<E> {

    @GuardedBy("this")
    private long referenceCount;
    @GuardedBy("this")
    private volatile E instance;

    @Override
    public synchronized E borrowObject() {
        if (instance == null) {
            instance = newObject();
            referenceCount = 1;
            return instance;
        } else {
            referenceCount++;
            return instance;
        }
    }

    protected abstract E newObject();

    @Override
    public synchronized void returnObject(final E element) {
        if (instance == element) {
            referenceCount--;
            if (referenceCount <= 0) {
                close();
            }
        } else {
            invalidateObject(element);
        }
    }

    @Override
    public synchronized void clear() {
        if (instance != null) {
            close();
        }
    }

    private void close() {
        invalidateObject(instance);
        instance = null;
        referenceCount = 0;
    }

    @Override
    public int size() {
        if (instance == null) {
            return 0;
        } else {
            return 1;
        }
    }

}
