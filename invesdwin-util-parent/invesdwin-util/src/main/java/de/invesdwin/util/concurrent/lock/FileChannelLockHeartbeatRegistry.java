package de.invesdwin.util.concurrent.lock;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.UUIDs;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDateMillis;

@ThreadSafe
public final class FileChannelLockHeartbeatRegistry {

    // Append UUID to ensure uniqueness even if PID@Hostname (e.g. 1@localhost) matches exactly across containers
    public static final String HEARTBEAT_OWNER = ManagementFactory.getRuntimeMXBean().getName() + "_"
            + UUIDs.newPseudoRandomUUID();
    public static final String HEARTBEAT_EXTENSION = ".heartbeat";
    public static final long HEARTBEAT_TIMEOUT_MILLIS = 90 * FTimeUnit.MILLISECONDS_IN_SECOND; // 90 seconds
    private static final int HEARTBEAT_INTERVAL_SECONDS = 30;

    private static final Map<File, WeakReference<FileChannelLock>> REGISTRY = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();

    private static final Object EXECUTOR_LOCK = new Object();
    private static ScheduledExecutorService heartbeatExecutor;

    private FileChannelLockHeartbeatRegistry() {}

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
                        .newScheduledThreadPool(FileChannelLockHeartbeatRegistry.class.getSimpleName(), 1);
                heartbeatExecutor.scheduleAtFixedRate(FileChannelLockHeartbeatRegistry::updateHeartbeats,
                        HEARTBEAT_INTERVAL_SECONDS, HEARTBEAT_INTERVAL_SECONDS, TimeUnit.SECONDS);
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
        final long now = FDateMillis.nowMillis();
        final long staleThreshold = now - HEARTBEAT_TIMEOUT_MILLIS;

        final Set<File> activeFiles = ILockCollectionFactory.getInstance(false).newSet();
        final Iterator<Entry<File, WeakReference<FileChannelLock>>> iterator = REGISTRY.entrySet().iterator();

        while (iterator.hasNext()) {
            final Entry<File, WeakReference<FileChannelLock>> entry = iterator.next();
            final WeakReference<FileChannelLock> ref = entry.getValue();
            final FileChannelLock lock = ref != null ? ref.get() : null;

            if (lock != null) {
                lock.touchHeartbeat();
                activeFiles.add(lock.getFile());
            } else {
                iterator.remove();
            }
        }

        cleanupStaleFiles(activeFiles, staleThreshold);
        stopHeartbeatExecutorIfNeeded();
    }

    private static void cleanupStaleFiles(final Set<File> activeFiles, final long staleThreshold) {
        final Map<File, List<String>> dirToPrefixes = ILockCollectionFactory.getInstance(false).newMap();

        // Group prefixes by directory to ensure we only scan each affected directory once
        for (final File file : activeFiles) {
            final File parent = file.getParentFile();
            if (parent != null) {
                dirToPrefixes.computeIfAbsent(parent, k -> new ArrayList<>()).add(file.getName());
            }
        }

        for (final Map.Entry<File, List<String>> entry : dirToPrefixes.entrySet()) {
            final File dir = entry.getKey();
            if (!dir.exists() || !dir.isDirectory()) {
                continue;
            }

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir.toPath())) {
                for (final Path path : stream) {
                    final String fileName = path.getFileName().toString();

                    boolean matchesPrefix = false;
                    for (final String prefix : entry.getValue()) {
                        if (fileName.startsWith(prefix) && (fileName.endsWith(FileChannelLock.TMP_EXTENSION)
                                || fileName.endsWith(HEARTBEAT_EXTENSION))) {
                            matchesPrefix = true;
                            break;
                        }
                    }

                    if (matchesPrefix) {
                        try {
                            if (Files.getLastModifiedTime(path).toMillis() < staleThreshold) {
                                Files.deleteIfExists(path);
                            }
                        } catch (final Exception ignored) {
                            // Ignore concurrent access or deletion issues
                        }
                    }
                }
            } catch (final Exception ignored) {
                // Ignore directory scanning issues
            }
        }
    }
}