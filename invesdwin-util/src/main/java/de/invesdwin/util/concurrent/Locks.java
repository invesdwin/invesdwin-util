package de.invesdwin.util.concurrent;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.Immutable;

import com.google.common.util.concurrent.CycleDetectingLockFactory;
import com.google.common.util.concurrent.CycleDetectingLockFactory.Policies;

@Immutable
public final class Locks {

    /**
     * Keep it disabled by default to keep best performance.
     */
    private static CycleDetectingLockFactory cycleDetectingLockFactory = CycleDetectingLockFactory
            .newInstance(Policies.DISABLED);

    private Locks() {}

    public static ReentrantLock newReentrantLock(final String lockName) {
        return cycleDetectingLockFactory.newReentrantLock(lockName);
    }

    public static ReentrantLock newReentrantLock(final String lockName, final boolean fair) {
        return cycleDetectingLockFactory.newReentrantLock(lockName, fair);
    }

    public static ReentrantReadWriteLock newReentrantReadWriteLock(final String lockName) {
        return cycleDetectingLockFactory.newReentrantReadWriteLock(lockName);
    }

    public static ReentrantReadWriteLock newReentrantReadWriteLock(final String lockName, final boolean fair) {
        return cycleDetectingLockFactory.newReentrantReadWriteLock(lockName, fair);
    }

    public static void setCycleDetectingLockFactory(final CycleDetectingLockFactory cycleDetectingLockFactory) {
        Locks.cycleDetectingLockFactory = cycleDetectingLockFactory;
    }

    /**
     * Cycle detection is disabled per default for performance purposes, using this you can enable it for debugging
     * purposes when required.
     */
    public static void setCycleDetectingLockFactory(final Policies cycleDetectingPolicy) {
        setCycleDetectingLockFactory(CycleDetectingLockFactory.newInstance(cycleDetectingPolicy));
    }

}
