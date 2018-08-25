package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.Immutable;

import com.google.common.util.concurrent.CycleDetectingLockFactory;
import com.google.common.util.concurrent.CycleDetectingLockFactory.Policies;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.concurrent.lock.internal.ALocksStaticFacade;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.WrappedReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.trace.DisabledLockTrace;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;
import de.invesdwin.util.lang.UniqueNameGenerator;

@StaticFacadeDefinition(name = "de.invesdwin.util.concurrent.lock.internal.ALocksStaticFacade", targets = {
        com.googlecode.concurentlocks.Locks.class })
@Immutable
public final class Locks extends ALocksStaticFacade {

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator();

    private static ILockTrace lockTrace = DisabledLockTrace.INSTANCE;

    /**
     * Keep it disabled by default to keep best performance.
     */
    private static CycleDetectingLockFactory cycleDetectingLockFactory = CycleDetectingLockFactory
            .newInstance(Policies.DISABLED);

    private Locks() {}

    public static IReentrantLock newReentrantLock(final String lockName) {
        final String name = UNIQUE_NAME_GENERATOR.get(lockName);
        final ReentrantLock lock = cycleDetectingLockFactory.newReentrantLock(name);
        return new WrappedReentrantLock(name, lock);
    }

    public static IReentrantLock newReentrantLock(final String lockName, final boolean fair) {
        final String name = UNIQUE_NAME_GENERATOR.get(lockName);
        final ReentrantLock lock = cycleDetectingLockFactory.newReentrantLock(name, fair);
        return new WrappedReentrantLock(name, lock);
    }

    public static IReentrantReadWriteLock newReentrantReadWriteLock(final String lockName) {
        final String name = UNIQUE_NAME_GENERATOR.get(lockName);
        final ReentrantReadWriteLock lock = cycleDetectingLockFactory.newReentrantReadWriteLock(name);
        return new WrappedReentrantReadWriteLock(name, lock);
    }

    public static IReentrantReadWriteLock newReentrantReadWriteLock(final String lockName, final boolean fair) {
        final String name = UNIQUE_NAME_GENERATOR.get(lockName);
        final ReentrantReadWriteLock lock = cycleDetectingLockFactory.newReentrantReadWriteLock(name, fair);
        return new WrappedReentrantReadWriteLock(name, lock);
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

    public static ILockTrace getLockTrace() {
        return lockTrace;
    }

    public static void setLockTrace(final ILockTrace lockTrace) {
        if (lockTrace == null) {
            Locks.lockTrace = DisabledLockTrace.INSTANCE;
        } else {
            Locks.lockTrace = lockTrace;
        }
    }

}
