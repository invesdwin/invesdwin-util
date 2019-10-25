package de.invesdwin.util.concurrent.lock;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.invesdwin.util.lang.Files;

@NotThreadSafe
public class FileChannelLockTest {

    @Test
    public void testDualLock() throws IOException {
        final File lock1File = new File("cache/" + FileChannelLock.class.getSimpleName() + ".lock");
        final File lock2Symlink = new File(lock1File.getAbsolutePath() + "2");
        FileUtils.deleteQuietly(lock1File);
        FileUtils.deleteQuietly(lock2Symlink);
        Files.touch(lock1File);
        //        final FileChannelLock lock1 = new FileChannelLock(lock1File);
        //        lock1.tryLockThrowing();
        Files.createSymbolicLink(lock2Symlink.getAbsoluteFile().toPath(), lock1File.getAbsoluteFile().toPath());
        Files.deleteQuietly(lock1File);
        final FileChannelLock lock2 = new FileChannelLock(lock2Symlink);
        lock2.tryLockThrowing();
        FileUtils.deleteQuietly(lock1File);
        FileUtils.deleteQuietly(lock2Symlink);

        lock2.close();

    }

}
