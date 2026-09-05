package de.invesdwin.util.concurrent.lock;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.strategy.DefaultLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.wrap.StrategyLock;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class FileChannelLock implements Closeable, ILock {

    public static final String TMP_EXTENSION = ".tmp";
    public static final String TMP_SUFFIX = "_" + Files.normalizePath(FileChannelLockHeartbeatRegistry.HEARTBEAT_OWNER)
            + TMP_EXTENSION;

    @GuardedBy("this")
    private final FileChannelLockFinalizer finalizer;

    public FileChannelLock(final File file) {
        this.finalizer = new FileChannelLockFinalizer(file, isDeleteFileAfterUnlock(), isThreadLockEnabled(),
                isHeartbeatEnabled());
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
                    finalizer.threadLock = FileChannelThreadLock.FILE_LOCK.get(finalizer.file.getAbsolutePath());
                }
                if (!finalizer.threadLock.tryLock()) {
                    return false;
                }
            }

            Files.forceMkdirParent(finalizer.file);
            final Path targetPath = finalizer.file.toPath();

            if (finalizer.heartbeatEnabled) {
                finalizer.heartbeatPath = targetPath.resolveSibling(
                        targetPath.getFileName().toString() + FileChannelLockHeartbeatRegistry.HEARTBEAT_EXTENSION);
            }

            final boolean moveSucceeded = atomicMove(targetPath);

            finalizer.raf = new RandomAccessFile(finalizer.file, "rw");
            finalizer.channel = finalizer.raf.getChannel();

            try {
                finalizer.fileLock = finalizer.channel.tryLock();
                if (finalizer.fileLock == null) {
                    // Another local process holds the OS lock
                    unlock();
                    return false;
                }
            } catch (final OverlappingFileLockException e) {
                // Another local thread holds the OS lock
                unlock();
                return false;
            } catch (final IOException e) {
                // OS locking is not supported or network errored.
                // We fallback gracefully to purely logical locking (moveSucceeded)
                finalizer.fileLock = null;
            }

            // The logical lock is the absolute source of truth across a shared filesystem.
            // If we didn't successfully create or steal the logical file, we MUST fail.
            if (!moveSucceeded) {
                unlock();
                return false;
            }

            finalizer.locked = true;
            finalizer.register(this);

            if (finalizer.heartbeatEnabled) {
                touchHeartbeat();
                FileChannelLockHeartbeatRegistry.register(this);
            }

            return true;
        } catch (final IOException e) {
            throw new IllegalStateException("Unable to lock file: " + finalizer.file, e);
        }
    }

    private boolean atomicMove(final Path targetPath) {
        final Path tempPath = targetPath.resolveSibling(targetPath.getFileName().toString() + TMP_SUFFIX);
        final String lockContent = FDateMillis.nowMillis() + ";" + FileChannelLockHeartbeatRegistry.HEARTBEAT_OWNER;
        boolean moveSucceeded = false;
        try {
            Files.writeString(tempPath, lockContent);
            Files.move(tempPath, targetPath);
            moveSucceeded = true;
        } catch (final FileAlreadyExistsException e) {
            moveSucceeded = tryStealOrVerifyLock(targetPath, tempPath, lockContent);
        } catch (final IOException e) {
            moveSucceeded = tryStealOrVerifyLock(targetPath, tempPath, lockContent);
        } finally {
            try {
                Files.deleteIfExists(tempPath);
            } catch (final IOException ignored) {
            }
        }
        return moveSucceeded;
    }

    private boolean tryStealOrVerifyLock(final Path targetPath, final Path tempPath, final String lockContent) {
        try {
            final String content = Files.readString(targetPath);
            final String[] parts = content.split(";", 2);

            if (parts.length != 2) {
                return false;
            }
            final String owner = parts[1].trim();
            if (finalizer.heartbeatEnabled) {
                long timestamp = Long.parseLong(parts[0].trim());
                if (finalizer.heartbeatPath != null && Files.exists(finalizer.heartbeatPath)) {
                    try {
                        final byte[] hbBytes = Files.readAllBytes(finalizer.heartbeatPath);
                        final String hbContent = new String(hbBytes, Charsets.defaultCharset());
                        final String[] hbParts = hbContent.split(";", 2);
                        if (hbParts.length == 2 && hbParts[1].trim().equals(owner)) {
                            timestamp = Long.parseLong(hbParts[0].trim());
                        }
                    } catch (final Exception ignored) {
                    }
                }

                if (FDateMillis.nowMillis() - timestamp > FileChannelLockHeartbeatRegistry.HEARTBEAT_TIMEOUT_MILLIS) {
                    Files.writeString(tempPath, lockContent);
                    Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    return true;
                }
            }

            if (FileChannelLockHeartbeatRegistry.HEARTBEAT_OWNER.equals(owner)) {
                return true;
            }
        } catch (final Exception ignored) {
        }
        return false;
    }

    void touchHeartbeat() {
        if (!finalizer.locked || !finalizer.heartbeatEnabled) {
            return;
        }
        try {
            final Path heartbeatPath = finalizer.heartbeatPath;
            if (heartbeatPath == null) {
                return;
            }
            final String lockContent = FDateMillis.nowMillis() + ";" + FileChannelLockHeartbeatRegistry.HEARTBEAT_OWNER;
            Files.writeString(heartbeatPath, lockContent, Charsets.defaultCharset());
        } catch (final IOException ignored) {
        }
    }

    @Override
    public synchronized boolean isLocked() {
        return finalizer.locked;
    }

    @Override
    public synchronized boolean isHeldByCurrentThread() {
        return finalizer.locked && (!finalizer.threadLockEnabled
                || (finalizer.threadLock != null && finalizer.threadLock.isHeldByCurrentThread()));
    }

    @Override
    public synchronized void unlock() {
        if (finalizer.heartbeatEnabled) {
            FileChannelLockHeartbeatRegistry.remove(this);
        }
        finalizer.close();
    }

    protected boolean isDeleteFileAfterUnlock() {
        return true;
    }

    protected boolean isThreadLockEnabled() {
        return false;
    }

    protected boolean isHeartbeatEnabled() {
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
        private final boolean heartbeatEnabled;
        private Path heartbeatPath;

        private RandomAccessFile raf;
        private FileChannel channel;
        private FileLock fileLock;
        private ILock threadLock;
        private volatile boolean locked;

        private FileChannelLockFinalizer(final File file, final boolean deleteFileAfterUnlock,
                final boolean threadLockEnabled, final boolean heartbeatEnabled) {
            this.file = file;
            this.deleteFileAfterUnlock = deleteFileAfterUnlock;
            this.threadLockEnabled = threadLockEnabled;
            this.heartbeatEnabled = heartbeatEnabled;
        }

        @Override
        protected void clean() {
            final FileLock fileLockCopy = fileLock;
            if (fileLockCopy != null) {
                try {
                    fileLockCopy.release();
                } catch (final IOException ignored) {
                }
                fileLock = null;
            }
            final FileChannel channelCopy = channel;
            if (channelCopy != null) {
                try {
                    channelCopy.close();
                } catch (final IOException ignored) {
                }
                channel = null;
            }
            final RandomAccessFile rafCopy = raf;
            if (rafCopy != null) {
                try {
                    rafCopy.close();
                } catch (final IOException ignored) {
                }
                raf = null;
            }
            final ILock threadLockCopy = threadLock;
            if (threadLockCopy != null) {
                threadLockCopy.unlock();
                threadLock = null;
            }
            if (locked) {
                locked = false;
                if (deleteFileAfterUnlock) {
                    file.delete();
                }
            }
            final Path heartbeatPathCopy = heartbeatPath;
            if (heartbeatPathCopy != null) {
                try {
                    Files.deleteIfExists(heartbeatPathCopy);
                } catch (final IOException ignored) {
                }
                heartbeatPath = null;
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

    //CHECKSTYLE:OFF
    @Override
    public ILock withStrategy(final ILockingStrategy strategy) {
        //CHECKSTYLE:ON
        return StrategyLock.maybeWrap(strategy, this);
    }

    @Override
    public ILockingStrategy getStrategy() {
        return DefaultLockingStrategy.INSTANCE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ILockTrace getLockTrace() {
        return Locks.getDefaultLockTrace();
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}