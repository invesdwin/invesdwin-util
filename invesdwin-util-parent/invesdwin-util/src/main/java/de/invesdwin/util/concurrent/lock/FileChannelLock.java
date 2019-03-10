package de.invesdwin.util.concurrent.lock;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.io.FileUtils;

import de.invesdwin.util.lang.cleanable.ACleanableAction;

@ThreadSafe
public class FileChannelLock implements Closeable {

    @GuardedBy("this")
    private final UnlockingCleanableAction cleanable;

    public FileChannelLock(final File file) {
        this.cleanable = new UnlockingCleanableAction(file, isDeleteFileAfterUnlock());
    }

    public File getFile() {
        return cleanable.file;
    }

    public synchronized boolean tryLock() {
        try {
            if (!cleanable.file.exists()) {
                FileUtils.forceMkdirParent(cleanable.file);
                FileUtils.touch(cleanable.file);
            }
            // Get a file channel for the file
            cleanable.raf = new RandomAccessFile(cleanable.file, "rw");
            cleanable.channel = cleanable.raf.getChannel();

            // Try acquiring the lock without blocking. This method returns
            // null or throws an exception if the file is already locked.
            try {
                cleanable.lock = cleanable.channel.tryLock();
            } catch (final OverlappingFileLockException e) {
                // File is already locked in this thread or virtual machine
                unlock();
                return false;
            }
            if (cleanable.lock == null) {
                unlock();
                return false;
            }
            cleanable.locked = true;
            cleanable.register(this);
            return true;
        } catch (final IOException e) {
            throw new IllegalStateException("Unable to lock file: " + cleanable.file, e);
        }
    }

    public synchronized boolean isLocked() {
        return cleanable.locked;
    }

    public synchronized void unlock() {
        cleanable.close();
    }

    protected boolean isDeleteFileAfterUnlock() {
        return true;
    }

    @Override
    public void close() {
        unlock();
    }

    public FileChannelLock tryLockThrowing() {
        if (!tryLock()) {
            throw new IllegalStateException("Unable to lock file: " + cleanable.file);
        }
        return this;
    }

    private static final class UnlockingCleanableAction extends ACleanableAction {

        private final File file;
        private final boolean deleteFileAfterUnlock;
        private RandomAccessFile raf;
        private FileChannel channel;
        private FileLock lock;
        private boolean locked;

        private UnlockingCleanableAction(final File file, final boolean deleteFileAfterUnlock) {
            this.file = file;
            this.deleteFileAfterUnlock = deleteFileAfterUnlock;
        }

        @Override
        protected void clean() {
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
            if (locked) {
                locked = false;
                if (deleteFileAfterUnlock) {
                    file.delete();
                }
            }
        }

        @Override
        public boolean isClosed() {
            return locked;
        }

    }

}
