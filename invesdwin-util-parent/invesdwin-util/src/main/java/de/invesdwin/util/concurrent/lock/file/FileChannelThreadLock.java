package de.invesdwin.util.concurrent.lock.file;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;

/**
 * Caffeine is not compatible to Java 8 which otherwise fails HadoopYarn test MpjExpress test. Thus extract this into an
 * optional class dependency.
 */
@ThreadSafe
final class FileChannelThreadLock {

    private FileChannelThreadLock() {}

    static ILock get(final String name) {
        return NameKeyedLocks.get(name);
    }

}
