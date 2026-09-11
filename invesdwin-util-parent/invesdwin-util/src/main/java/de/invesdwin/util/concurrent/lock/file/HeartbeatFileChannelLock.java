package de.invesdwin.util.concurrent.lock.file;

import java.io.File;
import java.nio.file.Path;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class HeartbeatFileChannelLock extends FileChannelLock {

    public HeartbeatFileChannelLock(final File file) {
        super(file);
    }

    public HeartbeatFileChannelLock(final Path path) {
        super(path);
    }

    @Override
    protected boolean isHeartbeatEnabled() {
        return true;
    }

}
