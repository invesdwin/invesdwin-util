package de.invesdwin.util.concurrent.lock.file;

import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;

/**
 * JVM-wide pool of name-keyed reentrant locks with weak values: the same name always resolves to the same lock while
 * anyone references it, and entries disappear automatically once no holder and no parked waiter remains - safe for
 * arbitrarily moving identities (store versions, file paths, resource keys) over the lifetime of a process.
 */
@ThreadSafe
public final class NameKeyedLocks {

    private static final LoadingCache<String, ILock> POOL = Caffeine.newBuilder()
            .weakValues()
            .<String, ILock> build((name) -> Locks.newReentrantLock(name));

    private NameKeyedLocks() {}

    public static ILock get(final String name) {
        return POOL.get(name);
    }

}
