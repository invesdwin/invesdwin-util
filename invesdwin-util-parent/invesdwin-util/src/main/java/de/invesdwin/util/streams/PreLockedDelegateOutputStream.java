package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PreLockedDelegateOutputStream extends SimpleDelegateOutputStream {

    private Lock lock;

    public PreLockedDelegateOutputStream(final Lock lock, final OutputStream delegate) {
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
