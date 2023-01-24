package de.invesdwin.util.concurrent.lock;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.OperatingSystem;

@NotThreadSafe
public class FileChannelLockTest {

    @Test
    public void testDualLock() throws IOException {
        if (OperatingSystem.isWindows()) {
            //symlinks can not be created on windows
            return;
        }
        final File lock1File = new File("cache/" + FileChannelLock.class.getSimpleName() + ".lock");
        final File lock2Symlink = new File(lock1File.getAbsolutePath() + "2");
        Files.deleteQuietly(lock1File);
        Files.deleteQuietly(lock2Symlink);
        Files.touch(lock1File);
        //        final FileChannelLock lock1 = new FileChannelLock(lock1File);
        //        lock1.tryLockThrowing();
        Files.createSymbolicLink(lock2Symlink.getAbsoluteFile().toPath(), lock1File.getAbsoluteFile().toPath());
        Files.deleteQuietly(lock1File);
        final FileChannelLock lock2 = new FileChannelLock(lock2Symlink);
        lock2.tryLockThrowing();
        Files.deleteQuietly(lock1File);
        Files.deleteQuietly(lock2Symlink);

        lock2.close();

    }

}
