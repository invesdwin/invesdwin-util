package de.invesdwin.util.test;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

/**
 * The run lock is what turns "the suites run serially" into something a second run can actually observe, so it has to
 * refuse a second holder and free the lock again - a guard that silently always succeeds is worse than no guard,
 * because the next reader of a red suite run will trust it.
 */
@NotThreadSafe
public class SuiteRunLockTest {

    @Test
    public void testTheLockRefusesASecondHolderAndFreesItAgain() throws IOException {
        final File lockFile = new File("target/invesdwin-suite-run-lock/invesdwin-suite.lock");
        lockFile.deleteOnExit();

        Assertions.assertThat(SuiteRunLock.tryAcquire(lockFile))
                .as("an uncontended run takes the lock, creating the subfolder it sits in").isTrue();
        Assertions.assertThat(lockFile.getParentFile()).as("and the run's folder is there").isDirectory();
        Assertions.assertThat(SuiteRunLock.isHeld(lockFile)).as("and reports it held").isTrue();
        Assertions.assertThat(SuiteRunLock.tryAcquire(lockFile))
                .as("the same JVM holding it is not a second run").isTrue();

        //a second holder (a second suite process) must be refused, not queued. The foreign channel is opened only AFTER
        //the release: FileChannelLock deletes its file on unlock, so a channel opened before that would hold a lock on
        //an unlinked inode, and the next opener would see a fresh file instead of a collision.
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
                .as("once the other holder closes, the lock is takeable again").isTrue();
        SuiteRunLock.release(lockFile);
        Assertions.assertThat(SuiteRunLock.isHeld(lockFile)).as("release frees it").isFalse();
    }

    @Test
    public void testTheLockIsNamedAfterTheModuleUnderTheGivenLogDir() {
        SuiteRunLock.setLockDir(new File("target/projectlogdir"));
        try {
            final File moduleLock = SuiteRunLock.lockFileForModule("invesdwin-util");
            Assertions.assertThat(moduleLock.getName())
                    .as("named after the maven module that takes it").isEqualTo("invesdwin-util.lock");
            Assertions.assertThat(moduleLock.getParentFile().getName())
                    .as("locks get a subfolder of their own, whatever dir they are pointed at").isEqualTo("locks");
            Assertions.assertThat(moduleLock.getParentFile().getParentFile().getName())
                    .as("under the log dir the run was given").isEqualTo("projectlogdir");
            Assertions.assertThat(SuiteRunLock.lockFileForModule("other-module").getName())
                    .as("another module has a file of its own").isEqualTo("other-module.lock");
        } finally {
            SuiteRunLock.setLockDir(null);
        }
        //never under java.io.tmpdir: the platform rewrites it per run, which would make the lock per-run and the guard
        //useless; with no project dir given, the fallback is the machine-wide root.
        final File fallback = SuiteRunLock.lockFileForModule("invesdwin-util");
        Assertions.assertThat(fallback.getParentFile().getName())
                .as("the fallback keeps the locks subfolder").isEqualTo("locks");
        Assertions.assertThat(fallback.getAbsolutePath())
                .as("and the machine-wide store root as its base").contains(".invesdwin");
        try {
            SuiteRunLock.setLockFile(new File("target/pointed-elsewhere.lock"));
            Assertions.assertThat(SuiteRunLock.lockFileForModule("invesdwin-util").getName())
                    .as("the system property points a run at one explicit file").isEqualTo("pointed-elsewhere.lock");
        } finally {
            SuiteRunLock.setLockFile(null);
        }
        Assertions.assertThat(SuiteRunLock.isExclusive())
                .as("the posture is loud, not fatal by default").isFalse();
    }

}
