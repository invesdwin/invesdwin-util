package de.invesdwin.util.test;

import java.io.File;
import java.nio.file.Paths;

import javax.annotation.concurrent.NotThreadSafe;

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
 * One object owns exactly one lock file, and there is no static state: the launcher instantiates one listener per
 * launcher session, so this object lives exactly as long as the lock has to be held, and two sessions in one JVM get
 * two objects that really do contend - {@link FileChannelLock} names that case instead of letting a second holder
 * through, which a process-wide registry of held files would have hidden.
 *
 * <p>
 * The posture is loud, not fatal: a refusal is reported, because agents and developers do run suites side by side and
 * a hard stop would break work unrelated to the contended stores. {@link #EXCLUSIVE_PROPERTY} turns a refusal into a
 * failed session; {@link #LOCK_FILE_PROPERTY} moves the lock for a run that deliberately uses its own stores. Both are
 * read once at construction, which is the only place they can come from on a command line.
 *
 * <p>
 * One lock file per maven module, named after its artifactId, under a {@code locks} subfolder of the project's own log
 * directory (the listener hands that directory in); without one it falls back to {@code ~/.invesdwin/locks}. Not the
 * cache directory: a suite run deletes cache contents, and {@link FileChannelLock} unlinks its own file on unlock, so a
 * lock file removed under a running holder silently un-guards the run - the log directory is not part of that cleanup.
 *
 * <p>
 * Naming the file after the module means a collision log names the contended suite family; the cost is that two
 * different modules do not see each other's lock, so {@link #LOCK_FILE_PROPERTY} is how a run is pointed at another
 * module's file when two families must be serialised against each other.
 */
@NotThreadSafe
public final class SuiteRunLock implements AutoCloseable {

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
    //CHECKSTYLE:ON

    private final File lockFile;
    private final boolean exclusive;
    private FileChannelLock held;
    private String failureReason;

    /**
     * The lock of one maven module: {@code <logDir>/locks/<artifactId>.lock}, or {@link #LOCK_FILE_PROPERTY} when that
     * is set - which is also how a run gets pointed at another module's file.
     *
     * @param logDir the project's own log directory; null falls back to the machine-wide base above.
     */
    public SuiteRunLock(final String artifactId, final File logDir) {
        this(newLockFile(artifactId, logDir));
    }

    /**
     * One explicit lock file, bypassing the module naming: what {@link #LOCK_FILE_PROPERTY} resolves to, and what a
     * test drives directly.
     */
    public SuiteRunLock(final File lockFile) {
        this.lockFile = lockFile;
        //CHECKSTYLE:OFF SystemProperties - see the note on the constant block above.
        this.exclusive = Boolean.parseBoolean(System.getProperty(EXCLUSIVE_PROPERTY, "false"));
        //CHECKSTYLE:ON
    }

    public File getLockFile() {
        return lockFile;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    /**
     * Takes the lock; true when this run may proceed on it - it holds it, or this object re-acquires what it already
     * holds. False for every refusal (another process, another thread, an io failure), which
     * {@link #getFailureReason()} then names.
     */
    public boolean tryAcquire() {
        if (held != null) {
            return true;
        }
        final File parentDir = lockFile.getParentFile();
        if (parentDir != null && !parentDir.isDirectory()) {
            //the locks subfolder appears the first time a suite run needs it
            parentDir.mkdirs();
        }
        final FileChannelLock lock = new FileChannelLock(lockFile);
        if (lock.tryLock()) {
            held = lock;
            failureReason = null;
            return true;
        }
        failureReason = lock.getLastTryLockFailureReason();
        lock.close();
        return false;
    }

    /**
     * Why the last {@link #tryAcquire()} was refused, using {@link FileChannelLock}'s own vocabulary
     * (OS_LOCK_HELD_BY_OTHER_PROCESS, OVERLAPPING_FILE_LOCK_BY_OTHER_THREAD, IO_…); null when it was not refused.
     */
    public String getFailureReason() {
        return failureReason;
    }

    public boolean isHeld() {
        return held != null;
    }

    /**
     * A refusal as it should be logged or thrown, so each module's listener has its own copy of neither the wording
     * nor the opt-out hint.
     */
    public String newRefusalMessage() {
        return "another suite run holds the suite lock [" + lockFile + "] (" + failureReason + "); the suite runs share"
                + " store directories and reference files, so overlapping runs produce unrelated-looking failures"
                + " across them (missing run reports, truncated cold reads, mutated reference membership). Run them"
                + " serially, or use -D" + LOCK_FILE_PROPERTY + "=PATH for a run with its own stores.";
    }

    /**
     * Releases the lock (the launcher session closing); harmless when nothing was taken. {@link FileChannelLock}
     * unlinks its own file on unlock, so the path exists exactly while a run holds it.
     */
    @Override
    public void close() {
        if (held != null) {
            held.close();
            held = null;
        }
    }

    private static File newLockFile(final String artifactId, final File logDir) {
        //CHECKSTYLE:OFF SystemProperties - see the note on the constant block above.
        final String override = System.getProperty(LOCK_FILE_PROPERTY);
        if (override != null && !override.isEmpty()) {
            return Paths.get(override).toFile();
        }
        //CHECKSTYLE:ON
        final File base = logDir != null ? logDir : new File(FALLBACK_LOCK_BASE);
        return new File(new File(base, LOCK_SUBDIR), artifactId + LOCK_FILE_SUFFIX);
    }

}
