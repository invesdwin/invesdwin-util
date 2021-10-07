package de.invesdwin.util.streams.pool.buffered;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PreLockedBufferedFileDataInputStream extends BufferedFileDataInputStream {

    private final Lock lock;

    public PreLockedBufferedFileDataInputStream(final Lock lock, final File file) throws FileNotFoundException {
        super(file);
        this.lock = lock;
    }

    public PreLockedBufferedFileDataInputStream(final Lock lock, final File file, final int bufferSize)
            throws FileNotFoundException {
        super(file, bufferSize);
        this.lock = lock;
    }

    public PreLockedBufferedFileDataInputStream(final Lock lock, final Path path) throws IOException {
        super(path);
        this.lock = lock;
    }

    public PreLockedBufferedFileDataInputStream(final Lock lock, final Path path, final int bufferSize)
            throws IOException {
        super(path, bufferSize);
        this.lock = lock;
    }

    @Override
    protected void onClose() {
        super.onClose();
        lock.unlock();
    }

}
