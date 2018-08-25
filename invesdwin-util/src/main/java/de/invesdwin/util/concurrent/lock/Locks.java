package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.Immutable;

import com.google.common.util.concurrent.CycleDetectingLockFactory;
import com.google.common.util.concurrent.CycleDetectingLockFactory.Policies;

import de.invesdwin.util.lang.UniqueNameGenerator;

@Immutable
public final class Locks {

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator();

    /**
     * Keep it disabled by default to keep best performance.
     */
    private static CycleDetectingLockFactory cycleDetectingLockFactory = CycleDetectingLockFactory
            .newInstance(Policies.DISABLED);

    private Locks() {}

    public static WrappedReentrantLock newReentrantLock(final String lockName) {
        return wrap(cycleDetectingLockFactory.newReentrantLock(UNIQUE_NAME_GENERATOR.get(lockName)));
    }

    public static WrappedReentrantLock newReentrantLock(final String lockName, final boolean fair) {
        return wrap(cycleDetectingLockFactory.newReentrantLock(UNIQUE_NAME_GENERATOR.get(lockName), fair));
    }

    public static WrappedReentrantReadWriteLock newReentrantReadWriteLock(final String lockName) {
        return wrap(cycleDetectingLockFactory.newReentrantReadWriteLock(UNIQUE_NAME_GENERATOR.get(lockName)));
    }

    public static WrappedReentrantReadWriteLock newReentrantReadWriteLock(final String lockName, final boolean fair) {
        return wrap(
                cycleDetectingLockFactory.newReentrantReadWriteLock(UNIQUE_NAME_GENERATOR.get(lockName), fair));
    }

    private static WrappedReentrantLock wrap(final ReentrantLock newReentrantLock) {
        return null;
    }

    private static WrappedReentrantReadWriteLock wrap(final ReentrantReadWriteLock reentrantReadWriteLock) {
        return null;
    }

    public static void setCycleDetectingLockFactory(final CycleDetectingLockFactory cycleDetectingLockFactory) {
        Locks.cycleDetectingLockFactory = cycleDetectingLockFactory;
    }

    /**
     * Cycle detection is disabled per default for performance purposes, using this you can enable it for debugging
     * purposes when required.
     */
    public static void setCycleDetectingPolicy(final Policies cycleDetectingPolicy) {
        setCycleDetectingLockFactory(CycleDetectingLockFactory.newInstance(cycleDetectingPolicy));
    }

}
