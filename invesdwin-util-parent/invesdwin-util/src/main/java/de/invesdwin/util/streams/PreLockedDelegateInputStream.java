package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PreLockedDelegateInputStream extends SimpleDelegateInputStream {

    private Lock lock;

    public PreLockedDelegateInputStream(final Lock lock, final InputStream delegate) {
        super(delegate);
        this.lock = lock;
    }

    @Override
    public void close() throws IOException {
        if (lock != null) {
            super.close();
            lock.unlock();
            lock = null;
        }
    }

}
