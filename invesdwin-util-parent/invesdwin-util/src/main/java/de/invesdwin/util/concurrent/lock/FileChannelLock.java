package de.invesdwin.util.concurrent.lock;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
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
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.duration.Duration;

/**
 * Provides cross-process synchronization using file-based locking.
 * <p>
 * This lock supports two distinct locking strategies to accommodate both local and distributed environments, making it
 * suitable for network-mountable, multiprocess storage architectures:
 * <ul>
 * <li><b>Standard OS Native Locking ({@code isHeartbeatEnabled() == false}):</b><br>
 * Relies entirely on standard Java NIO {@link java.nio.channels.FileChannel#tryLock()}. The OS native lock is the
 * absolute source of truth. This strategy is ideal for local, single-node multiprocess coordination, leaving the target
 * file's byte contents completely untouched. It will safely reject lock acquisitions (e.g., via {@code IOException}) on
 * network drives where native POSIX locking is unsupported.</li>
 * 
 * <li><b>Distributed Logical Locking ({@code isHeartbeatEnabled() == true}):</b><br>
 * Acts as the absolute source of truth across a shared filesystem by atomically moving a temporary file containing a
 * unique owner ID to claim the lock. It maintains an active {@code .heartbeat} file to signal liveliness. If a remote
 * node crashes or a network partition occurs, the lock will time out via lightweight filesystem metadata checks,
 * allowing active nodes to safely steal the logical lock.</li>
 * </ul>
 */
@ThreadSafe
public class FileChannelLock implements Closeable, ILock {

    public static final String TMP_EXTENSION = ".tmp";
    public static final String TMP_SUFFIX = "_" + Files.normalizePath(FileChannelLockHeartbeatRegistry.HEARTBEAT_OWNER)
            + TMP_EXTENSION;

    @GuardedBy("this")
    private final FileChannelLockFinalizer finalizer;

    public FileChannelLock(final File file) {
        this.finalizer = new FileChannelLockFinalizer(file, file.toPath(), isDeleteFileAfterUnlock(),
                isThreadLockEnabled(), isHeartbeatEnabled());
    }

    public FileChannelLock(final Path file) {
        this.finalizer = new FileChannelLockFinalizer(file.toFile(), file, isDeleteFileAfterUnlock(),
                isThreadLockEnabled(), isHeartbeatEnabled());
    }

    public File getFile() {
        return finalizer.file;
    }

    public Path getPath() {
        return finalizer.path;
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
                    finalizer.threadLock = FileChannelThreadLock.FILE_LOCK
                            .get(finalizer.path.toAbsolutePath().toString());
                }
                if (!finalizer.threadLock.tryLock()) {
                    return false;
                }
            }

            Files.forceMkdirParent(finalizer.file);
            final Path targetPath = finalizer.path;

            boolean moveSucceeded = false;

            // ONLY perform the logical file rewrite if heartbeats are enabled
            if (finalizer.heartbeatEnabled) {
                finalizer.heartbeatPath = targetPath.resolveSibling(
                        targetPath.getFileName().toString() + FileChannelLockHeartbeatRegistry.HEARTBEAT_EXTENSION);
                moveSucceeded = atomicMove(targetPath);
            }

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
                finalizer.fileLock = null;
            }

            boolean registerHeartbeat = false;
            if (finalizer.heartbeatEnabled) {
                // The logical lock is the absolute source of truth across a shared filesystem.
                // If we didn't successfully create or steal the logical file, we MUST fail.
                if (!moveSucceeded) {
                    unlock();
                    return false;
                }

                if (touchHeartbeatUnchecked()) {
                    registerHeartbeat = true;
                } else {
                    unlock();
                    return false;
                }
            } else {
                // Heartbeat is disabled: The OS lock is the absolute source of truth.
                // If we couldn't get the OS lock (e.g., IOException on a network drive), we MUST fail.
                if (finalizer.fileLock == null) {
                    unlock();
                    return false;
                }
            }

            finalizer.locked = true;
            finalizer.register(this);
            if (registerHeartbeat) {
                FileChannelLockHeartbeatRegistry.register(this);
            }

            return true;
        } catch (final IOException e) {
            throw new IllegalStateException("Unable to lock file: " + finalizer.file, e);
        }
    }

    private boolean atomicMove(final Path targetPath) {
        final Path tempPath = targetPath.resolveSibling(targetPath.getFileName().toString() + TMP_SUFFIX);
        // Store only the unique owner string; time is tracked purely via filesystem metadata
        final String lockContent = FileChannelLockHeartbeatRegistry.HEARTBEAT_OWNER;
        boolean moveSucceeded = false;
        try {
            Files.writeString(tempPath, lockContent);
            Files.move(tempPath, targetPath);
            moveSucceeded = true;
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
            final String owner = content.trim();

            if (FileChannelLockHeartbeatRegistry.HEARTBEAT_OWNER.equals(owner)) {
                return true;
            }

            if (finalizer.heartbeatEnabled) {
                final long now = FDateMillis.nowMillis();
                final long timeout = FileChannelLockHeartbeatRegistry.HEARTBEAT_TIMEOUT_MILLIS;

                Path checkPath = targetPath;
                if (finalizer.heartbeatPath != null && Files.exists(finalizer.heartbeatPath)) {
                    checkPath = finalizer.heartbeatPath;
                }

                // Pure filesystem metadata check: immune to client-side clock skews
                final long lastModified = Files.exists(checkPath) ? Files.getLastModifiedTime(checkPath).toMillis() : 0;

                if ((now - lastModified) > timeout) {
                    Files.writeString(tempPath, lockContent);
                    Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                    // VERIFICATION STEP: Read back to guarantee we won the race against other nodes
                    final String verifyContent = Files.readString(targetPath);
                    if (FileChannelLockHeartbeatRegistry.HEARTBEAT_OWNER.equals(verifyContent.trim())) {
                        return true;
                    }
                }
            }
        } catch (final Exception ignored) {
        }
        return false;
    }

    boolean touchHeartbeat() {
        if (!finalizer.locked || !finalizer.heartbeatEnabled) {
            return false;
        }
        return touchHeartbeatUnchecked();
    }

    private boolean touchHeartbeatUnchecked() {
        try {
            final Path targetPath = finalizer.path;

            if (Files.exists(targetPath)) {
                final String currentOwner = Files.readString(targetPath).trim();
                if (!FileChannelLockHeartbeatRegistry.HEARTBEAT_OWNER.equals(currentOwner)) {
                    finalizer.close();
                    return false;
                }
            }

            final Path heartbeatPath = finalizer.heartbeatPath;
            if (heartbeatPath == null || !Files.exists(heartbeatPath)) {
                return false;
            }

            // Use an efficient metadata-only update instead of rewriting the file content
            Files.setLastModifiedTime(heartbeatPath, FileTime.fromMillis(FDateMillis.nowMillis()));
            return true;
        } catch (final IOException ignored) {
            return false;
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
        private final Path path;
        private final boolean deleteFileAfterUnlock;
        private final boolean threadLockEnabled;
        private final boolean heartbeatEnabled;
        private Path heartbeatPath;

        private RandomAccessFile raf;
        private FileChannel channel;
        private FileLock fileLock;
        private ILock threadLock;
        private volatile boolean locked;

        private FileChannelLockFinalizer(final File file, final Path path, final boolean deleteFileAfterUnlock,
                final boolean threadLockEnabled, final boolean heartbeatEnabled) {
            this.file = file;
            this.path = path;
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
                    if (canDeleteHeartbeat(path)) {
                        file.delete();
                    }
                }
            }
            final Path heartbeatPathCopy = heartbeatPath;
            if (heartbeatPathCopy != null) {
                if (canDeleteHeartbeat(heartbeatPathCopy)) {
                    try {
                        Files.deleteIfExists(heartbeatPathCopy);
                    } catch (final IOException ignored) {
                    }
                }
                heartbeatPath = null;
            }
        }

        private boolean canDeleteHeartbeat(final Path targetPath) {
            if (!heartbeatEnabled) {
                return true;
            }
            try {
                if (Files.exists(targetPath)) {
                    final String content = Files.readString(targetPath).trim();
                    return FileChannelLockHeartbeatRegistry.HEARTBEAT_OWNER.equals(content);
                }
            } catch (final Exception ignored) {
            }
            return false;
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