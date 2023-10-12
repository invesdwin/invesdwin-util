package de.invesdwin.util.concurrent.lock;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class FileChannelLock implements Closeable, ILock {

    private static final LoadingCache<String, ILock> FILE_LOCK = Caffeine.newBuilder()
            .weakValues()
            .<String, ILock> build((name) -> Locks.newReentrantLock(name));

    @GuardedBy("this")
    private final FileChannelLockFinalizer finalizer;

    public FileChannelLock(final File file) {
        this.finalizer = new FileChannelLockFinalizer(file, isDeleteFileAfterUnlock(), isThreadLockEnabled());
    }

    public File getFile() {
        return finalizer.file;
    }

    @Override
    public String getName() {
        return getFile().getName();
    }

    @Override
    public void lock() {
        try {
            lockInterruptibly();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        while (!tryLock()) {
            FTimeUnit.MILLISECONDS.sleep(1);
        }
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        final Duration maxDuration = new Duration(time, FTimeUnit.valueOfTimeUnit(unit));
        final Instant start = new Instant();
        while (!tryLock()) {
            FTimeUnit.MILLISECONDS.sleep(1);
            if (start.isGreaterThan(maxDuration)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public synchronized boolean tryLock() {
        try {
            if (finalizer.locked) {
                return true;
            }
            if (finalizer.threadLockEnabled) {
                if (finalizer.threadLock == null) {
                    finalizer.threadLock = FILE_LOCK.get(finalizer.file.getAbsolutePath());
                }
                if (!finalizer.threadLock.tryLock()) {
                    return false;
                }
            }
            if (!finalizer.file.exists()) {
                Files.forceMkdirParent(finalizer.file);
                Files.touch(finalizer.file);
            }
            // Get a file channel for the file
            finalizer.raf = new RandomAccessFile(finalizer.file, "rw");
            finalizer.channel = finalizer.raf.getChannel();

            // Try acquiring the lock without blocking. This method returns
            // null or throws an exception if the file is already locked.
            try {
                finalizer.fileLock = finalizer.channel.tryLock();
            } catch (final OverlappingFileLockException e) {
                // File is already locked in this thread or virtual machine
                unlock();
                return false;
            }
            if (finalizer.fileLock == null) {
                unlock();
                return false;
            }
            finalizer.locked = true;
            finalizer.register(this);
            return true;
        } catch (final IOException e) {
            throw new IllegalStateException("Unable to lock file: " + finalizer.file, e);
        }
    }

    public synchronized boolean isLocked() {
        return finalizer.locked;
    }

    @Override
    public synchronized void unlock() {
        finalizer.close();
    }

    protected boolean isDeleteFileAfterUnlock() {
        return true;
    }

    protected boolean isThreadLockEnabled() {
        return false;
    }

    @Override
    public void close() {
        unlock();
    }

    public FileChannelLock tryLockThrowing() {
        if (!tryLock()) {
            throw new IllegalStateException("Unable to lock file: " + finalizer.file);
        }
        return this;
    }

    public FileChannelLock tryLockThrowing(final long time, final TimeUnit unit) throws InterruptedException {
        if (!tryLock(time, unit)) {
            throw new IllegalStateException("Unable to lock file: " + finalizer.file);
        }
        return this;
    }

    private static final class FileChannelLockFinalizer extends AFinalizer {

        private final File file;
        private final boolean deleteFileAfterUnlock;
        private final boolean threadLockEnabled;
        private RandomAccessFile raf;
        private FileChannel channel;
        private FileLock fileLock;
        private ILock threadLock;
        private boolean locked;

        private FileChannelLockFinalizer(final File file, final boolean deleteFileAfterUnlock,
                final boolean threadLockEnabled) {
            this.file = file;
            this.deleteFileAfterUnlock = deleteFileAfterUnlock;
            this.threadLockEnabled = threadLockEnabled;
        }

        @Override
        protected void clean() {
            // Release the lock - if it is not null!
            if (fileLock != null) {
                try {
                    fileLock.release();
                } catch (final IOException e) {
                    //ignore
                }
                fileLock = null;
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
            if (threadLock != null) {
                threadLock.unlock();
                threadLock = null;
            }
            if (locked) {
                locked = false;
                if (deleteFileAfterUnlock) {
                    file.delete();
                }
            }
        }

        @Override
        protected boolean isCleaned() {
            return !locked;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }

    }

    @Deprecated
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("not implemented");
    }

}
