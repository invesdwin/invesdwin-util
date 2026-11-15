package de.invesdwin.util.concurrent.lock.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

/**
 * Encapsulates the path context and background maintenance duties for an {@link AtomicNioFileChannel}.
 * 
 * <p>
 * <b>Pattern used:</b> Employs a lazily-initialized background cleanup routine shared via memory derivation. To avoid
 * unbounded accumulation of stale temporary files (which can happen if a process crashes mid-write), it occasionally
 * audits the directory. Reuses internal state via {@link #derive(IFileChannelPath)} when iterating over siblings to
 * prevent redundant timestamp tracking or repeated file I/O operations for directory cleanup.
 */
@ThreadSafe
public class AtomicNioFileChannelContext implements Cloneable {

    public static final String TMP_EXTENSION = ".tmp";
    /**
     * Distinguishes the staging file of one JVM from that of another; it is NOT unique per writer - see
     * {@link #newTempPath(Path)}.
     */
    public static final String TMP_SUFFIX_BASE = "_"
            + Files.normalizePath(HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER);
    public static final String TMP_SUFFIX = TMP_SUFFIX_BASE + TMP_EXTENSION;
    public static final String CLEANUP_MARKER_EXTENSION = ".cleanup";

    public static final Duration TMP_CLEANUP_INTERVAL = Duration.ONE_DAY;
    public static final Duration TMP_CLEANUP_TIMEOUT = new Duration(12, FTimeUnit.HOURS);

    public static final long UNINITIALIZED_DIRECTORY_CLEANUP_TIME = -1L;

    static {
        Assertions.assertThat(TMP_SUFFIX).doesNotContain("/");
        Assertions.assertThat(TMP_SUFFIX.length()).isLessThanOrEqualTo(Files.MAX_FILE_NAME_LENGTH);
    }

    /**
     * Makes one JVM's staging names unique per open upload: two writers that share a target otherwise share one temp
     * file, and the first publisher renames it away under the target lock, so the second renames a name that is gone.
     */
    private static final AtomicLong TMP_WRITER_SEQUENCE = new AtomicLong();

    private final String tmpExtension = newTmpExtension();
    private final String tmpSuffixBase = newTmpSuffixBase();
    private final String tmpSuffix = newTmpSuffix();
    private final String tmpCleanupMarkerFilename = newTmpCleanupMarkerFilename();
    private final Duration tmpCleanupInterval = newTmpCleanupInterval();
    private final Duration tmpCleanupTimeout = newTmpCleanupTimeout();
    /**
     * The publish mode this context hands to its channels: false publishes by replace (an upload onto the same name
     * overwrites it, which is what a cache entry or a properties file wants), true publishes by claim (the target name
     * must not exist yet - see {@link Files#moveClaim(Path, Path)}).
     * It lives on the context rather than on the channel because every withSubDirectory / withFilename /
     * withBaseServerUri factory clones the context into the derived channel, so a per-channel flag would be silently
     * dropped by exactly those calls.
     */
    private final boolean claimPublish;

    @GuardedBy("this")
    private Path directoryPath;
    @GuardedBy("this")
    private AtomicLong directoryCleanupTime;

    public AtomicNioFileChannelContext() {
        this(false);
    }

    public AtomicNioFileChannelContext(final boolean claimPublish) {
        this.claimPublish = claimPublish;
    }

    public boolean isClaimPublish() {
        return claimPublish;
    }

    /**
     * The staging file one upload writes into before {@link #isClaimPublish()} decides how it is moved onto
     * <code>targetPath</code>. Every part of the name is load-bearing: the JVM's heartbeat owner keeps two processes
     * apart, and the per-open sequence keeps two threads of ONE JVM apart - with only the owner in the name, both
     * writers open the same file and the publisher that closes first renames it away, so the other one's publish fails
     * with NoSuchFileException on a name it no longer owns (measured 2026-11-15 in a parallel suite writing the shared
     * live-calculated segment store). The name still ends in {@link #TMP_EXTENSION}, so
     * {@link #cleanupStaleTempFiles()} reaps a leftover from a killed writer exactly as before.
     */
    public Path newTempPath(final Path targetPath) {
        final String writerSuffix = tmpSuffixBase + "_" + TMP_WRITER_SEQUENCE.incrementAndGet() + tmpExtension;
        return targetPath.resolveSibling(
                Files.setExtensionNormalizePath(targetPath.getFileName().toString(), writerSuffix));
    }

    protected String newTmpSuffixBase() {
        return TMP_SUFFIX_BASE;
    }

    protected String newTmpSuffix() {
        return tmpSuffixBase + tmpExtension;
    }

    protected String newTmpExtension() {
        return TMP_EXTENSION;
    }

    protected String newTmpCleanupMarkerFilename() {
        return newTmpExtension() + CLEANUP_MARKER_EXTENSION;
    }

    protected Duration newTmpCleanupInterval() {
        return TMP_CLEANUP_INTERVAL;
    }

    protected Duration newTmpCleanupTimeout() {
        return TMP_CLEANUP_TIMEOUT;
    }

    @Override
    public AtomicNioFileChannelContext clone() {
        try {
            return (AtomicNioFileChannelContext) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void maybeRunCleanup(final Path directoryPath) {
        attach(directoryPath);

        final FDate now = FDate.now();
        long last = directoryCleanupTime.get();

        // Lazy initialization block
        if (last == UNINITIALIZED_DIRECTORY_CLEANUP_TIME) {
            last = newInitialCleanupTime(this.directoryPath);
            // Attempt to set it. If another thread already initialized it (or ran a cleanup),
            // this safely fails and we re-fetch the latest value below.
            directoryCleanupTime.compareAndSet(UNINITIALIZED_DIRECTORY_CLEANUP_TIME, last);
            last = directoryCleanupTime.get();
        }

        if (tmpCleanupInterval.isGreaterThanMillis(now.millisValue() - last)) {
            return;
        }

        final Path markerPath = directoryPath.resolve(tmpCleanupMarkerFilename);
        if (Files.exists(markerPath)) {
            final long lastModified = Files.lastModifiedNoThrow(markerPath);
            if (tmpCleanupInterval.isGreaterThanMillis(now.millisValue() - lastModified)) {
                directoryCleanupTime.set(lastModified);
                return;
            }
        }

        try {
            // Inline atomic write for the marker path
            final Path tempMarkerPath = markerPath
                    .resolveSibling(Files.setExtensionNormalizePath(markerPath.getFileName().toString(), tmpSuffix));
            Files.createDirectories(tempMarkerPath.getParent());
            Files.writeString(tempMarkerPath, now.toString());
            Files.moveAtomic(tempMarkerPath, markerPath);

            directoryCleanupTime.set(now.millisValue());
            cleanupStaleTempFiles();
        } catch (final IOException e) {
            directoryCleanupTime.set(now.millisValue());
        }
    }

    private void attach(final Path directoryPath) {
        if (!Objects.equals(directoryPath, this.directoryPath)) {
            this.directoryPath = directoryPath;
            directoryCleanupTime = new AtomicLong(UNINITIALIZED_DIRECTORY_CLEANUP_TIME);
        }
    }

    private long newInitialCleanupTime(final Path dir) {
        final Path markerPath = dir.resolve(tmpCleanupMarkerFilename);
        if (Files.exists(markerPath)) {
            return Files.lastModifiedNoThrow(markerPath);
        }
        return 0;
    }

    private void cleanupStaleTempFiles() {
        Files.cleanupStaleTempFiles(directoryPath, tmpCleanupTimeout, tmpExtension, tmpCleanupMarkerFilename);
    }

}