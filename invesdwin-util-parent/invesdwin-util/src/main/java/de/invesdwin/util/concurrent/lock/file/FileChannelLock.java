package de.invesdwin.util.concurrent.lock.file;

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

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
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

    private volatile String lastTryLockFailureReason;

    @GuardedBy("this")
    private final FileChannelLockFinalizer finalizer;
    //set on successful acquisition, cleared on release; read by the demand-cycle detection across threads (hence volatile)
    private volatile Thread lockAcquirerThread;

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
            Duration.ONE_HUNDRED_MILLISECONDS.sleepRandom();
        }
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        final Duration maxDuration = new Duration(time, FTimeUnit.valueOfTimeUnit(unit));
        final Instant start = new Instant();
        while (!tryLock()) {
            if (start.isGreaterThan(maxDuration)) {
                return false;
            }
            Duration.ONE_HUNDRED_MILLISECONDS.orLower(start.toDuration()).sleepRandom();
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
                    //per-instance thread lock, NOT the name-keyed JVM pool: a pool instance abandoned by its owner
                    //thread without close() (throwing safety-net paths) stayed locked forever on an idle pool thread
                    //and silently blocked every later update of that key across ALL instances (forensics smoke-46:
                    //name lock held, no live registry entry). Cross-instance local exclusion is enforced one layer
                    //down by the OS file lock (OverlappingFileLockException path) and the claim-steal logic, which
                    //are instance independent and recoverable.
                    finalizer.threadLock = de.invesdwin.util.concurrent.lock.Locks
                            .newReentrantLock(finalizer.path.toAbsolutePath().toString());
                }
                if (!finalizer.threadLock.tryLock()) {
                    lastTryLockFailureReason = "THREAD_LOCK_HELD_BY_OTHER_THREAD";
                    return false;
                }
            }

            Files.forceMkdirParent(finalizer.file);
            final Path targetPath = finalizer.path;

            boolean moveSucceeded = false;

            // ONLY perform the logical file rewrite if heartbeats are enabled
            if (finalizer.heartbeatEnabled) {
                finalizer.heartbeatPath = targetPath.resolveSibling(Files.setExtensionNormalizeFileName(
                        targetPath.getFileName().toString(), HeartbeatFileChannelLockRegistry.HEARTBEAT_EXTENSION));
                moveSucceeded = atomicMove(targetPath);
            }

            finalizer.raf = new RandomAccessFile(finalizer.file, "rw");
            finalizer.channel = finalizer.raf.getChannel();

            try {
                finalizer.fileLock = finalizer.channel.tryLock();
                if (finalizer.fileLock == null) {
                    // Another local process holds the OS lock
                    lastTryLockFailureReason = "OS_LOCK_HELD_BY_OTHER_PROCESS";
                    unlock();
                    return false;
                }
            } catch (final OverlappingFileLockException e) {
                // Another local thread holds the OS lock
                lastTryLockFailureReason = "OVERLAPPING_FILE_LOCK_BY_OTHER_THREAD";
                unlock();
                return false;
            } catch (final IOException e) {
                if (finalizer.heartbeatEnabled) {
                    // OS locking is not supported or network errored.
                    finalizer.fileLock = null;
                } else {
                    throw e;
                }
            }

            if (finalizer.heartbeatEnabled) {
                // The logical lock is the absolute source of truth across a shared filesystem.
                // If we didn't successfully create or steal the logical file, we MUST fail.
                if (!moveSucceeded) {
                    lastTryLockFailureReason = "CLAIM_MOVE_FAILED";
                    unlock();
                    return false;
                }

                if (!touchHeartbeatUnchecked()) {
                    lastTryLockFailureReason = "HEARTBEAT_TOUCH_FAILED";
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

            return tryLockSuccess();
        } catch (final IOException e) {
            lastTryLockFailureReason = "IO_"+e.getClass().getSimpleName();
            unlock();
            throw new IllegalStateException("Unable to lock file: " + finalizer.file, e);
        }
    }

    private boolean tryLockSuccess() {
        finalizer.locked = true;
        this.lockAcquirerThread = Thread.currentThread();
        finalizer.register(this);
        if (finalizer.heartbeatEnabled) {
            HeartbeatFileChannelLockRegistry.register(this);
        }
        return true;
    }

    private boolean atomicMove(final Path targetPath) {
        final Path tempPath = targetPath.resolveSibling(Files.setExtensionNormalizeFileName(
                targetPath.getFileName().toString(), AtomicNioFileChannelContext.TMP_SUFFIX));
        // Store only the unique owner string; time is tracked purely via filesystem metadata
        final String lockContent = HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER;
        boolean moveSucceeded = false;
        try {
            Files.createDirectories(tempPath.getParent());
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
            final String currentOwner = finalizer.readOwnerFallback(targetPath);

            if (HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER.equals(currentOwner)) {
                //the claim names THIS JVM - but the owner string is per JVM, not per lock instance: a stale claim
                //left by an abandoned/crashed local instance must NOT make a fresh tryLock believe it holds the
                //lock (that converted killed-fork leftovers into a permanent self-block, smoke-41/43: claim=true,
                //registry empty). Only a live, still-locked local instance counts as 'already ours'; a local ghost
                //is stolen immediately (safe: nothing in this JVM owns it).
                if (HeartbeatFileChannelLockRegistry.hasLiveLocalInstance(targetPath.toFile())) {
                    return true;
                }
                Files.writeString(tempPath, lockContent);
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                final String verifyContent = finalizer.readOwnerFallback(targetPath);
                final boolean won = HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER.equals(verifyContent);
                if (!won) {
                    lastTryLockFailureReason = "GHOST_STEAL_VERIFY_FAILED";
                }
                return won;
            }

            if (finalizer.heartbeatEnabled) {
                final long now = FDateMillis.nowMillis();

                Path checkPath = targetPath;
                if (finalizer.heartbeatPath != null && Files.exists(finalizer.heartbeatPath)) {
                    checkPath = finalizer.heartbeatPath;
                }

                // Pure filesystem metadata check: immune to client-side clock skews
                final long lastModified = Files.exists(checkPath) ? Files.getLastModifiedTime(checkPath).toMillis() : 0;

                if (HeartbeatFileChannelLockRegistry.HEARTBEAT_TIMEOUT.isLessThanMillis(now - lastModified)) {
                    Files.writeString(tempPath, lockContent);
                    Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                    // VERIFICATION STEP: Read back to guarantee we won the race against other nodes
                    final String verifyContent = finalizer.readOwnerFallback(targetPath);
                    if (HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER.equals(verifyContent)) {
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
            final Path heartbeatPath = finalizer.heartbeatPath;
            if (heartbeatPath == null) {
                return false;
            }

            if (Files.exists(heartbeatPath)) {
                final String currentOwner = Files.readString(heartbeatPath).trim();
                if (!HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER.equals(currentOwner)) {
                    //A foreign owner string only means another JVM wrote it - the same staleness rule as the
                    //claim steal decides: an un-beating (timeout-old) heartbeat is crash residue from a dead JVM
                    //and MUST be taken over by the new claim winner; refusing it turned every killed/crashed fork
                    //into a permanent per-key block (the winner stole the claim, then this touch self-aborted and
                    //deleted its own claim file - endless steal/touch livelock, HEARTBEAT_TOUCH_FAILED forensics)
                    final long hbAge = FDateMillis.nowMillis() - Files.getLastModifiedTime(heartbeatPath).toMillis();
                    if (!HeartbeatFileChannelLockRegistry.HEARTBEAT_TIMEOUT.isLessThanMillis(hbAge)) {
                        finalizer.close();
                        return false;
                    }
                    Files.writeString(heartbeatPath, HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER);
                }
            }

            if (!Files.exists(heartbeatPath)) {
                Files.writeString(heartbeatPath, HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER);
            } else {
                // Use an efficient metadata-only update instead of rewriting the file content
                Files.setLastModifiedTime(heartbeatPath, FileTime.fromMillis(FDateMillis.nowMillis()));
            }
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
        this.lockAcquirerThread = null;
        if (finalizer.heartbeatEnabled) {
            HeartbeatFileChannelLockRegistry.remove(this);
        }
        finalizer.close();
    }

    /**
     * The thread that acquired this lock (null when not held or after release). Used by the updater demand-cycle
     * detection to resolve which thread a wait-for chain would close back onto; not a security boundary, only a
     * diagnostic aid within this JVM.
     */
    public Thread getLockAcquirerThread() {
        return lockAcquirerThread;
    }

    public String getLastTryLockFailureReason() {
        return lastTryLockFailureReason;
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
                if (threadLockCopy.isHeldByCurrentThread()) {
                    threadLockCopy.unlock();
                }
                //closing from a non-owner thread (warning-finalizer safety net, joiner-side cleanup): an
                //IllegalMonitorStateException here used to abort close BEFORE the remaining cleanup and leave
                //the name-keyed thread lock held by the original (now idle) pool thread forever - every later
                //update of that key then failed tryLock silently (forensics smoke-46: name lock held, no live
                //registry instance). Leave the thread lock to its owner thread; everything else still unwinds.
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
                    final String currentOwner = readOwnerFallback(targetPath);
                    return HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER.equals(currentOwner);
                }
            } catch (final Exception ignored) {
            }
            return false;
        }

        private String readOwnerFallback(final Path targetPath) throws IOException {
            try {
                return Files.readString(targetPath).trim();
            } catch (final IOException e) {
                /*
                 * Windows lock fallback: if the main file is unreadable due to a mandatory OS lock, check the heartbeat
                 * file to verify ownership.
                 */
                if (heartbeatEnabled && heartbeatPath != null && targetPath != heartbeatPath
                        && Files.exists(heartbeatPath)) {
                    return Files.readString(heartbeatPath).trim();
                }
                throw e;
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