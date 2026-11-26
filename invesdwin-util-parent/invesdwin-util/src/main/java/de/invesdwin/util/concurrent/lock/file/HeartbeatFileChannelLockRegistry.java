package de.invesdwin.util.concurrent.lock.file;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.UUIDs;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class HeartbeatFileChannelLockRegistry {

    // Append UUID to ensure uniqueness even if PID@Hostname (e.g. 1@localhost) matches exactly across containers
    public static final String HEARTBEAT_OWNER = Strings.truncate(ManagementFactory.getRuntimeMXBean().getName(), 50)
            + "_" + UUIDs.newPseudoRandomUUID();
    public static final String HEARTBEAT_EXTENSION = ".heartbeat";
    public static final Duration HEARTBEAT_TIMEOUT = Duration.TWO_MINUTES;
    public static final Duration HEARTBEAT_INTERVAL = Duration.THIRTY_SECONDS;

    private static final int MAX_PREFIXES_POOL_SIZE = 100;

    private static final Map<File, WeakReference<FileChannelLock>> REGISTRY = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();

    private static final Map<File, List<String>> DIR_TO_PREFIXES = ILockCollectionFactory.getInstance(true).newMap();
    private static final List<List<String>> PREFIXES_POOL = new ArrayList<>(MAX_PREFIXES_POOL_SIZE);

    private static final Object EXECUTOR_LOCK = new Object();
    private static ScheduledExecutorService heartbeatExecutor;
    private static final AtomicNioFileChannelContext TMP_CLEANUP_CONTEXT = new AtomicNioFileChannelContext();
    private static final AtomicNioFileChannelContext HEARTBEAT_CLEANUP_CONTEXT = new AtomicNioFileChannelContext() {
        @Override
        protected String newTmpExtension() {
            return HEARTBEAT_EXTENSION;
        }

        @Override
        protected Duration newTmpCleanupInterval() {
            return HEARTBEAT_INTERVAL;
        }

        @Override
        protected Duration newTmpCleanupTimeout() {
            return HEARTBEAT_TIMEOUT;
        }

    };

    private HeartbeatFileChannelLockRegistry() {}

    public static void register(final FileChannelLock lock) {
        REGISTRY.put(lock.getFile(), new WeakReference<>(lock));
        startHeartbeatExecutorIfNeeded();
    }

    /**
     * Diagnostic: the locally registered lock instance for the given file plus its acquirer thread identity, state
     * and current stack (if alive) - identifies zombie heartbeat holders in the owning JVM.
     */
    public static String describeLocalClaim(final File lockPath) {
        FileChannelLock lock = null;
        final String wanted = lockPath.getAbsolutePath();
        for (final Map.Entry<File, WeakReference<FileChannelLock>> entry : REGISTRY.entrySet()) {
            if (wanted.equals(entry.getKey().getAbsolutePath())) {
                lock = entry.getValue().get();
                break;
            }
        }
        if (lock == null) {
            return "<no live registry entry>";
        }
        final StringBuilder out = new StringBuilder("acquirer=[");
        final Thread acquirer = lock.getLockAcquirerThread();
        if (acquirer == null) {
            out.append("null]");
        } else {
            out.append(acquirer.getName()).append("#").append(acquirer.getId()).append(" state=")
                    .append(acquirer.getState()).append("]\n");
            final StackTraceElement[] stack = acquirer.getStackTrace();
            for (int i = 0; i < stack.length && i < 20; i++) {
                out.append("      * ").append(stack[i]).append("\n");
            }
        }
        return out.toString();
    }

    /**
     * True when a still-locked HeartbeatFileChannelLock instance of this JVM is registered for the given lock file
     * with a live acquirer thread - the honest 'we already own it' test behind the owner-string match.
     */
    public static boolean hasLiveLocalInstance(final File lockFile) {
        //compare by absolute path (map keys are constructed File objects; path equality is the identity here)
        final String wanted = lockFile.getAbsolutePath();
        for (final Map.Entry<File, WeakReference<FileChannelLock>> entry : REGISTRY.entrySet()) {
            if (wanted.equals(entry.getKey().getAbsolutePath())) {
                final FileChannelLock lock = entry.getValue().get();
                if (lock == null) {
                    continue;
                }
                final Thread acquirer = lock.getLockAcquirerThread();
                if (lock.isLocked() && acquirer != null && acquirer.isAlive()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void remove(final FileChannelLock lock) {
        //Only the instance that owns this registration may take it away. Every failed tryLock() runs unlock() as its
        //cleanup, so a loser that wants the same path would otherwise evict the entry of the instance that really
        //holds the lock; hasLiveLocalInstance() is the honest "we already own it" test behind the claim owner-string
        //match, and with the entry gone FileChannelLock.tryStealOrVerifyLock() replaces the holder's own claim with
        //REPLACE_EXISTING - after that the holder's OS lock sits on an unlinked inode and a second thread of the SAME
        //JVM holds the same "exclusive" file lock (measured 2026-11-15: that is how two writers reached one
        //live-calculated segment file in the umbrella suite).
        final String wanted = lock.getFile().getAbsolutePath();
        File key = null;
        FileChannelLock holder = null;
        for (final Map.Entry<File, WeakReference<FileChannelLock>> entry : REGISTRY.entrySet()) {
            if (wanted.equals(entry.getKey().getAbsolutePath())) {
                key = entry.getKey();
                holder = entry.getValue().get();
                break;
            }
        }
        if (key == null) {
            return;
        }
        if (holder == null || holder == lock) {
            //a collected (null) holder is a corpse of this path's registration and must go, exactly as before
            REGISTRY.remove(key);
        }
        //size-guarded internally, so an unlock that changed nothing cannot shut the heartbeat loop down
        stopHeartbeatExecutorIfNeeded();
    }

    private static void startHeartbeatExecutorIfNeeded() {
        synchronized (EXECUTOR_LOCK) {
            if (heartbeatExecutor == null || heartbeatExecutor.isShutdown()) {
                heartbeatExecutor = Executors
                        .newScheduledThreadPool(HeartbeatFileChannelLockRegistry.class.getSimpleName(), 1);
                heartbeatExecutor.scheduleAtFixedRate(HeartbeatFileChannelLockRegistry::updateHeartbeats,
                        HEARTBEAT_INTERVAL.millisValue(), HEARTBEAT_INTERVAL.millisValue(), TimeUnit.MILLISECONDS);
            }
        }
    }

    private static void stopHeartbeatExecutorIfNeeded() {
        synchronized (EXECUTOR_LOCK) {
            if (REGISTRY.isEmpty() && heartbeatExecutor != null && !heartbeatExecutor.isShutdown()) {
                // Use shutdown() instead of shutdownNow() to avoid interrupting an active heartbeat update loop
                heartbeatExecutor.shutdown();
                heartbeatExecutor = null;
            }
        }
    }

    //package-visible, not private: the sweep's eviction rule is the other half of the identity guard below, and a
    //test must be able to run the real sweep instead of re-deriving it
    static void updateHeartbeats() {
        try {
            final Iterator<Entry<File, WeakReference<FileChannelLock>>> iterator = REGISTRY.entrySet().iterator();
            while (iterator.hasNext()) {
                final Entry<File, WeakReference<FileChannelLock>> entry = iterator.next();
                final WeakReference<FileChannelLock> ref = entry.getValue();
                final FileChannelLock lock = ref != null ? ref.get() : null;
                if (lock != null) {
                    if (lock.touchHeartbeat()) {
                        // Populate DIR_TO_PREFIXES directly in the first pass
                        final File file = lock.getFile();
                        final File parent = file.getParentFile();
                        if (parent != null) {
                            addDirToPrefix(file, parent);
                        }
                    } else if (!lock.isLocked()) {
                        //A failed beat on an instance that STILL holds the lock is a transient I/O condition
                        //(touchHeartbeatUnchecked() answers false for any IOException), and evicting it here would
                        //remove the very registration that stops tryStealOrVerifyLock() from replacing this JVM's own
                        //claim - a second thread of the same JVM could then take the same lock. That is the same
                        //damage remove() guards against, reached through the sweep rather than through unlock().
                        //A lock that has given up (touchHeartbeat() closes itself when the heartbeat is foreign and
                        //stale, :309) reports isLocked() false and is reaped here, as before.
                        iterator.remove();
                    }
                } else {
                    iterator.remove();
                }
            }
            cleanupStaleTempFiles();
        } finally {
            resetDirToPrefixes();
        }
        stopHeartbeatExecutorIfNeeded();
    }

    private static void addDirToPrefix(final File file, final File parent) {
        List<String> prefixes = DIR_TO_PREFIXES.get(parent);
        if (prefixes == null) {
            if (PREFIXES_POOL.isEmpty()) {
                prefixes = new ArrayList<>();
            } else {
                prefixes = PREFIXES_POOL.remove(PREFIXES_POOL.size() - 1);
            }
            DIR_TO_PREFIXES.put(parent, prefixes);
        }
        prefixes.add(file.getName());
    }

    private static void cleanupStaleTempFiles() {
        for (final Map.Entry<File, List<String>> entry : DIR_TO_PREFIXES.entrySet()) {
            final File dir = entry.getKey();
            final Path path = dir.toPath();
            HEARTBEAT_CLEANUP_CONTEXT.maybeRunCleanup(path);
            TMP_CLEANUP_CONTEXT.maybeRunCleanup(path);
        }

    }

    private static void resetDirToPrefixes() {
        if (!DIR_TO_PREFIXES.isEmpty()) {
            // Return lists to the pool for reuse
            for (final List<String> prefixes : DIR_TO_PREFIXES.values()) {
                if (PREFIXES_POOL.size() >= MAX_PREFIXES_POOL_SIZE) {
                    break;
                }
                if (!prefixes.isEmpty()) {
                    prefixes.clear();
                }
                PREFIXES_POOL.add(prefixes);
            }
            DIR_TO_PREFIXES.clear();
        }
    }

    /**
     * Reads the claim marker written into the update lock file at tryLock time (with the same heartbeat file fallback
     * used for Windows mandatory locks) and checks whether it was written by this JVM. The HEARTBEAT_OWNER string is
     * unique per JVM (PID@host plus random UUID), so a match proves that some still open HeartbeatFileChannelLock
     * instance of this JVM holds this lock file, i.e. the beating lock is a local ghost and not a remote process
     * working on the update.
     */
    /**
     * Does the given owner string name a claim made by THIS machine (by any JVM on it, including one that has died)?
     * <p>
     * The owner shape is {@code <pid>@<host>_<uuid>}. The OS file lock can only be authoritative about processes on
     * this host, so this is the boundary of the killed-holder takeover: a claim left by ANOTHER machine keeps relying
     * on the heartbeat timeout, because remote file locking may be unreliable or machine-scoped and must not be
     * second-guessed by a local lock table.
     */
    public static boolean isClaimOwnedByThisMachine(final String owner) {
        final String otherHost = hostOfOwner(owner);
        final String ownHost = hostOfOwner(HEARTBEAT_OWNER);
        return otherHost != null && ownHost != null && otherHost.equals(ownHost);
    }

    /** The host part of an owner string, or null when it is not in owner form. */
    private static String hostOfOwner(final String owner) {
        if (owner == null) {
            return null;
        }
        final String trimmed = owner.trim();
        final int at = trimmed.indexOf('@');
        if (at < 0 || at == trimmed.length() - 1) {
            return null;
        }
        //the join to the UUID is the LAST underscore: a UUID carries no underscores, a hostname may
        final int join = trimmed.lastIndexOf('_');
        if (join <= at) {
            return trimmed.substring(at + 1);
        }
        return trimmed.substring(at + 1, join);
    }

    public static boolean isLockClaimedByThisJvm(final File lockPath) {
        final String lockOwner = Files.readFileToStringNoThrow(lockPath, Charsets.defaultCharset());
        if (lockOwner != null) {
            return HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER.equals(lockOwner);
        }
        // Windows lock fallback: if the main file is unreadable due to a mandatory OS lock, check the heartbeat file.
        final File heartbeatPath = Files.setExtensionNormalizeFileName(lockPath,
                HeartbeatFileChannelLockRegistry.HEARTBEAT_EXTENSION);
        final String heartbeatOwner = Files.readFileToStringNoThrow(heartbeatPath, Charsets.defaultCharset());
        return HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER.equals(heartbeatOwner);
    }

}