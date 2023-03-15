package de.invesdwin.util.concurrent.lock;

import java.io.Closeable;
import java.util.concurrent.locks.Lock;

public interface ILock extends Lock, Closeable {

    String getName();

    @Override
    default void close() {
        unlock();
    }

}
