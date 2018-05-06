package de.invesdwin.util.concurrent;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class FileChannelLock implements Closeable {

    private final File file;
    private RandomAccessFile raf;
    private FileChannel channel;
    private FileLock lock;

    public FileChannelLock(final File file) {
        this.file = file;
    }

    public synchronized boolean tryLock() {
        try {
            // Get a file channel for the file
            raf = new RandomAccessFile(file, "rw");
            channel = raf.getChannel();

            // Try acquiring the lock without blocking. This method returns
            // null or throws an exception if the file is already locked.
            try {
                lock = channel.tryLock();
            } catch (final OverlappingFileLockException e) {
                // File is already locked in this thread or virtual machine
                unlock();
                return false;
            }
            if (lock == null) {
                unlock();
                return false;
            }
            return true;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void unlock() {
        // Release the lock - if it is not null!
        if (lock != null) {
            try {
                lock.release();
            } catch (final IOException e) {
                //ignore
            }
            lock = null;
        }

        // Close the file
        if (channel != null) {
            try {
                channel.close();
            } catch (final IOException e) {
                //ignore
            }
            channel = null;
        }
        if (raf != null) {
            try {
                raf.close();
            } catch (final IOException e) {
                //ignore
            }
            raf = null;
        }
    }

    @Override
    public void close() {
        unlock();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

}
