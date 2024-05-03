package de.invesdwin.util.concurrent.lock;

import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

/**
 * Caffeine is not compatible to Java 8 which otherwise fails HadoopYarn test MpjExpress test. Thus extract this into an
 * optional class dependency.
 */
@ThreadSafe
final class FileChannelThreadLock {

    static final LoadingCache<String, ILock> FILE_LOCK = Caffeine.newBuilder()
            .weakValues()
            .<String, ILock> build((name) -> Locks.newReentrantLock(name));

    private FileChannelThreadLock() {}

}
