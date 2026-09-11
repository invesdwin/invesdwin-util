package de.invesdwin.util.concurrent.lock.file;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
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
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class HeartbeatFileChannelLockRegistry {

    // Append UUID to ensure uniqueness even if PID@Hostname (e.g. 1@localhost) matches exactly across containers
    public static final String HEARTBEAT_OWNER = ManagementFactory.getRuntimeMXBean().getName() + "_"
            + UUIDs.newPseudoRandomUUID();
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

    private HeartbeatFileChannelLockRegistry() {}

    public static void register(final FileChannelLock lock) {
        REGISTRY.put(lock.getFile(), new WeakReference<>(lock));
        startHeartbeatExecutorIfNeeded();
    }

    public static void remove(final FileChannelLock lock) {
        REGISTRY.remove(lock.getFile());
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

    private static void updateHeartbeats() {
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
                    } else {
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
            if (!dir.exists() || !dir.isDirectory()) {
                continue;
            }
            Files.cleanupStaleTempFiles(dir.toPath(), HEARTBEAT_TIMEOUT, FileChannelLock.TMP_EXTENSION,
                    HEARTBEAT_EXTENSION);
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
}