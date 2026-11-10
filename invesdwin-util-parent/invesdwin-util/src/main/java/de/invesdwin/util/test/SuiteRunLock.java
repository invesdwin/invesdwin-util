package de.invesdwin.util.test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.lock.file.FileChannelLock;

/**
 * A cross-process guard for the reference-comparison suite family: the suites share store directories and reference
 * files, so two suite runs at the same time corrupt each other's expectations, and the failure shows up later as
 * several unrelated red tests spread across different suites rather than as the collision it is.
 *
 * <p>
 * The convention "the suites run serially" is not enforced by the build (the parent pom pins forkCount=1 and leaves the
 * threadCount block commented out, which serialises classes inside one fork and says nothing about a second suite
 * process), so this lock is taken for the lifetime of a launcher session by a {@code LauncherSessionListener} in each
 * suite module. The lock itself is a {@link FileChannelLock}, so a run that only holds a shell {@code flock} on the
 * same path is NOT seen by it - the two mechanisms do not observe each other, which is why the module-side guard is the
 * one that covers IDE and hand-run launches.
 *
 * <p>
 * The posture is loud, not fatal: a collision is reported, because agents and developers do run suites side by side
 * and a hard stop would break work unrelated to the contended stores. {@link #setExclusive(boolean)} turns it into a
 * failed session; {@link #setLockFile(File)} moves the lock for a run that deliberately uses its own stores. The
 * listener reads both from system properties, which is the only place they can be set from a command line.
 *
 * <p>
 * One lock file per maven module, named after its artifactId, under a {@code locks} subfolder of the project's own log
 * directory (the listener hands that directory to {@link #setLockDir(File)}); without one it falls back to
 * {@code ~/.invesdwin/locks}. Not the cache directory: a suite run deletes cache contents, and {@link FileChannelLock}
 * unlinks its own file on unlock, so a lock file removed under a running holder silently un-guards the run - the log
 * directory is not part of that cleanup.
 *
 * <p>
 * Naming the file after the module means a collision log names the contended suite family; the cost is that two
 * different modules do not see each other's lock, so {@link #LOCK_FILE_PROPERTY} is how a run is pointed at another
 * module's file when two families must be serialised against each other.
 */
@ThreadSafe
public final class SuiteRunLock {

    public static final String EXCLUSIVE_PROPERTY = "invesdwin.suite.exclusive";
    public static final String LOCK_FILE_PROPERTY = "invesdwin.suite.lockFile";

    //CHECKSTYLE:OFF SystemProperties - a launcher session listener runs before the context exists, so the
    //context-backed SystemProperties cannot be used here; these reads are the whole surface.
    //NOT java.io.tmpdir: the platform configurer rewrites it per run (…/invesdwin_temp_<user>/<session>@<host>/),
    //which would make the lock per-run and the guard useless. This base is only the fallback: normally the listener
    //hands in the project's own log directory, which sits with the checkout the run belongs to.
    private static final String FALLBACK_LOCK_BASE = System.getProperty("user.home") + File.separator + ".invesdwin";
    private static final String LOCK_SUBDIR = "locks";
    private static final String LOCK_FILE_SUFFIX = ".lock";
    private static final boolean DEFAULT_EXCLUSIVE = Boolean.parseBoolean(
            System.getProperty(EXCLUSIVE_PROPERTY, "false"));
    //CHECKSTYLE:ON

    @GuardedBy("SuiteRunLock.class")
    private static final Map<File, FileChannelLock> HELD = ILockCollectionFactory.getInstance(false)
            .newConcurrentMap();
    @GuardedBy("SuiteRunLock.class")
    private static File lockDir;
    @GuardedBy("SuiteRunLock.class")
    private static File lockFileOverride;
    @GuardedBy("SuiteRunLock.class")
    private static boolean exclusive = DEFAULT_EXCLUSIVE;
    @GuardedBy("SuiteRunLock.class")
    private static String lastFailureReason;

    private SuiteRunLock() {}

    /**
     * The base directory for the module locks - a suite module's listener hands in its project log directory. Locks
     * always land in a {@code locks} subfolder of whatever base is given, so they never sit mixed in with the files the
     * run writes there itself.
     */
    public static synchronized void setLockDir(final File dir) {
        lockDir = dir;
    }

    /**
     * The lock for one maven module: {@code <lockDir>/locks/<artifactId>.lock}, named after the module whose suite run
     * takes it, so a collision log says which suite family is contended instead of pointing at one shared name. A run
     * only ever takes the lock of the module it is running; {@link #LOCK_FILE_PROPERTY} is how a run that must see (or
     * avoid) another module's lock is pointed at that file.
     */
    public static synchronized File lockFileForModule(final String artifactId) {
        if (lockFileOverride != null) {
            return lockFileOverride;
        }
        final File base = lockDir != null ? lockDir : new File(FALLBACK_LOCK_BASE);
        return new File(new File(base, LOCK_SUBDIR), artifactId + LOCK_FILE_SUFFIX);
    }

    public static synchronized void setLockFile(final File file) {
        lockFileOverride = file;
    }

    public static synchronized boolean isExclusive() {
        return exclusive;
    }

    public static synchronized void setExclusive(final boolean value) {
        exclusive = value;
    }

    /**
     * Takes the suite lock for {@code file}; true when this run may proceed on it - it holds the lock, already held it
     * in this JVM, or the file cannot be locked at all (a guard that cannot take its lock must not break the suite it
     * guards). False only for a real collision, which {@link #getLastFailureReason()} then describes.
     */
    public static synchronized boolean tryAcquire(final File file) {
        lastFailureReason = null;
        if (HELD.containsKey(file)) {
            //this JVM already holds this file (a nested launcher session in one process is not a second run)
            return true;
        }
        final File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.isDirectory()) {
            //the locks subfolder appears the first time a suite run needs it
            parentDir.mkdirs();
        }
        final FileChannelLock lock = new FileChannelLock(file);
        if (lock.tryLock()) {
            HELD.put(file, lock);
            return true;
        }
        final String reason = lock.getLastTryLockFailureReason();
        lock.close();
        lastFailureReason = reason;
        return false;
    }

    /**
     * Why the last {@link #tryAcquire(File)} failed, using {@link FileChannelLock}'s own vocabulary
     * (OS_LOCK_HELD_BY_OTHER_PROCESS, OVERLAPPING_FILE_LOCK_BY_OTHER_THREAD, IO_…); null when it did not fail.
     */
    public static synchronized String getLastFailureReason() {
        return lastFailureReason;
    }

    /**
     * Releases every lock this JVM took (the launcher session closing).
     */
    public static synchronized void release() {
        for (final FileChannelLock lock : HELD.values()) {
            lock.close();
        }
        HELD.clear();
    }

    public static synchronized void release(final File file) {
        final FileChannelLock lock = HELD.remove(file);
        if (lock != null) {
            lock.close();
        }
    }

    public static synchronized boolean isHeld(final File file) {
        return HELD.containsKey(file);
    }

    /**
     * Reads the command-line configuration once; kept separate from class loading so a test can drive it directly.
     */
    public static synchronized void configureFromSystemProperties() {
        //CHECKSTYLE:OFF SystemProperties - see the note on the field block above.
        final String file = System.getProperty(LOCK_FILE_PROPERTY);
        if (file != null && !file.isEmpty()) {
            lockFileOverride = Paths.get(file).toFile();
        }
        exclusive = Boolean.parseBoolean(System.getProperty(EXCLUSIVE_PROPERTY, Boolean.toString(exclusive)));
        //CHECKSTYLE:ON
    }

}
