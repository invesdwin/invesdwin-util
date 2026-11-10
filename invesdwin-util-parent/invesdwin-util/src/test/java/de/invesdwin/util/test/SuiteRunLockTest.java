package de.invesdwin.util.test;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

/**
 * The run lock is what turns "the suites run serially" into something a second run can see, so it has to actually
 * refuse a second holder and actually free the lock again - a guard that silently always succeeds is worse than no
 * guard, because the next reader of a red suite run will trust it.
 */
@NotThreadSafe
public class SuiteRunLockTest {

    @Test
    public void testTheLockRefusesASecondHolderAndFreesItAgain() throws IOException {
        //inside the module's own build directory: no system properties needed, and the file goes with the build
        final File lockFile = new File("target/invesdwin-suite-run-lock-test.lock");
        lockFile.getParentFile().mkdirs();
        lockFile.deleteOnExit();

        Assertions.assertThat(SuiteRunLock.tryAcquire(lockFile))
                .as("an uncontended run takes the lock").isTrue();
        Assertions.assertThat(SuiteRunLock.isHeld(lockFile)).as("and reports it held").isTrue();
        Assertions.assertThat(SuiteRunLock.tryAcquire(lockFile))
                .as("the same JVM holding it is not a second run").isTrue();

        //a second holder (the shape of a second suite process) must be refused, not queued. The foreign channel is
        //opened only AFTER the release: FileChannelLock deletes its file on unlock, so a channel opened before that
        //would hold a lock on an unlinked inode and the next opener would see a fresh file instead of a collision.
        SuiteRunLock.release(lockFile);
        final FileChannel other = FileChannel.open(lockFile.toPath(), StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
        try {
            other.lock();
            Assertions.assertThat(SuiteRunLock.tryAcquire(lockFile))
                    .as("with the file locked elsewhere the lock reports the collision").isFalse();
            Assertions.assertThat(SuiteRunLock.isHeld(lockFile)).as("and holds nothing itself").isFalse();
            Assertions.assertThat(SuiteRunLock.getLastFailureReason())
                    .as("and says which mechanism refused it").isNotNull();
        } finally {
            other.close();
        }

        Assertions.assertThat(SuiteRunLock.tryAcquire(lockFile))
                .as("after the other holder closes, the lock is takeable again").isTrue();
        SuiteRunLock.release(lockFile);
        Assertions.assertThat(SuiteRunLock.isHeld(lockFile)).as("release frees it").isFalse();
    }

    @Test
    public void testDefaultLockFileIsOutsideThePerRunTempDir() {
        //the platform rewrites java.io.tmpdir per run, so a default under it would be per-run and the guard dead
        Assertions.assertThat(SuiteRunLock.defaultLockFile().getAbsolutePath())
                .as("the default lock file sits in the machine-wide store root")
                .contains(".invesdwin");
        Assertions.assertThat(SuiteRunLock.isExclusive())
                .as("the posture is loud, not fatal by default").isFalse();
    }
}
