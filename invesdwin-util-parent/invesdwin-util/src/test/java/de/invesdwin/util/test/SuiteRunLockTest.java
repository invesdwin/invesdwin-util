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

        final SuiteRunLock holder = new SuiteRunLock(lockFile);
        Assertions.assertThat(holder.tryAcquire())
                .as("an uncontended run takes the lock, creating the subfolder it sits in").isTrue();
        Assertions.assertThat(lockFile.getParentFile()).as("and the run's folder is there").isDirectory();
        Assertions.assertThat(holder.isHeld()).as("and reports it held").isTrue();
        Assertions.assertThat(holder.tryAcquire())
                .as("the holder taking it again is not a second run").isTrue();

        //a second holder object in the same jvm (a second launcher session in one fork) is refused by the same
        //mechanism - measured reason OVERLAPPING_FILE_LOCK_BY_OTHER_THREAD, on the same thread and on another one.
        Assertions.assertThat(new SuiteRunLock(lockFile).tryAcquire())
                .as("a second holder object in the same jvm is refused too").isFalse();

        //a second holder (a second suite process) must be refused, not queued. The foreign channel is opened only AFTER
        //close(): FileChannelLock deletes its file on unlock, so a channel opened before that would hold a lock on an
        //unlinked inode, and the next opener would see a fresh file instead of a collision.
        holder.close();
        final FileChannel other = FileChannel.open(lockFile.toPath(), StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
        try {
            other.lock();
            final SuiteRunLock contender = new SuiteRunLock(lockFile);
            Assertions.assertThat(contender.tryAcquire())
                    .as("with the file locked elsewhere the lock reports the collision").isFalse();
            Assertions.assertThat(contender.isHeld()).as("and holds nothing itself").isFalse();
            Assertions.assertThat(contender.getFailureReason())
                    .as("and says which mechanism refused it").isNotNull();
            Assertions.assertThat(contender.newRefusalMessage())
                    .as("and the message a listener logs names the file and the opt-out").contains(lockFile.getPath())
                    .contains(SuiteRunLock.LOCK_FILE_PROPERTY);
        } finally {
            other.close();
        }

        final SuiteRunLock next = new SuiteRunLock(lockFile);
        Assertions.assertThat(next.tryAcquire())
                .as("once the other holder closes, the lock is takeable again").isTrue();
        next.close();
        Assertions.assertThat(next.isHeld()).as("close frees it").isFalse();
        next.close();
        Assertions.assertThat(next.tryAcquire()).as("and close is harmless twice over").isTrue();
        next.close();
    }

    @Test
    public void testTheLockIsNamedAfterTheModuleUnderTheGivenLogDir() {
        //CHECKSTYLE:OFF SystemProperties - the class reads its configuration from the command line by design.
        final String savedLockFile = System.getProperty(SuiteRunLock.LOCK_FILE_PROPERTY);
        try {
            final SuiteRunLock moduleLock = new SuiteRunLock("invesdwin-util", new File("target/projectlogdir"));
            Assertions.assertThat(moduleLock.getLockFile().getName())
                    .as("named after the maven module that takes it").isEqualTo("invesdwin-util.lock");
            Assertions.assertThat(moduleLock.getLockFile().getParentFile().getName())
                    .as("locks get a subfolder of their own, whatever dir they are pointed at").isEqualTo("locks");
            Assertions.assertThat(moduleLock.getLockFile().getParentFile().getParentFile().getName())
                    .as("under the log dir the run was given").isEqualTo("projectlogdir");
            Assertions.assertThat(new SuiteRunLock("other-module", new File("target/projectlogdir")).getLockFile()
                    .getName()).as("another module has a file of its own").isEqualTo("other-module.lock");
            Assertions.assertThat(moduleLock.isExclusive())
                    .as("the posture is loud, not fatal by default").isFalse();

            //never under java.io.tmpdir: the platform rewrites it per run, which would make the lock per-run and the
            //guard useless; with no project dir given, the fallback is the machine-wide base.
            final SuiteRunLock fallback = new SuiteRunLock("invesdwin-util", null);
            Assertions.assertThat(fallback.getLockFile().getParentFile().getName())
                    .as("the fallback keeps the locks subfolder").isEqualTo("locks");
            Assertions.assertThat(fallback.getLockFile().getAbsolutePath())
                    .as("and the machine-wide store root as its base").contains(".invesdwin");

            System.setProperty(SuiteRunLock.LOCK_FILE_PROPERTY, "target/pointed-elsewhere.lock");
            Assertions.assertThat(new SuiteRunLock("invesdwin-util", new File("target/projectlogdir")).getLockFile()
                    .getName()).as("the property points a run at one explicit file")
                    .isEqualTo("pointed-elsewhere.lock");
            Assertions.assertThat(new SuiteRunLock("invesdwin-util", null).getLockFile().getName())
                    .as("and it wins over the module naming everywhere").isEqualTo("pointed-elsewhere.lock");

            System.setProperty(SuiteRunLock.EXCLUSIVE_PROPERTY, "true");
            Assertions.assertThat(new SuiteRunLock("invesdwin-util", null).isExclusive())
                    .as("the posture is read from the command line at construction").isTrue();
        } finally {
            System.clearProperty(SuiteRunLock.EXCLUSIVE_PROPERTY);
            if (savedLockFile == null) {
                System.clearProperty(SuiteRunLock.LOCK_FILE_PROPERTY);
            } else {
                System.setProperty(SuiteRunLock.LOCK_FILE_PROPERTY, savedLockFile);
            }
        }
        //CHECKSTYLE:ON
    }

}
