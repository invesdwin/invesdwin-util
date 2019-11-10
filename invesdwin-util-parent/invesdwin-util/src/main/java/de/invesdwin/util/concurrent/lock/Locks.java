package de.invesdwin.util.concurrent.lock;

import java.util.LinkedList;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.Immutable;

import com.google.common.util.concurrent.CycleDetectingLockFactory;
import com.google.common.util.concurrent.CycleDetectingLockFactory.Policies;
import com.googlecode.concurentlocks.CompositeLock;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;
import de.invesdwin.util.concurrent.lock.internal.ALocksStaticFacade;
import de.invesdwin.util.concurrent.lock.internal.TimeoutLock;
import de.invesdwin.util.concurrent.lock.internal.TimeoutReentrantLock;
import de.invesdwin.util.concurrent.lock.internal.TracedLock;
import de.invesdwin.util.concurrent.lock.internal.TracedReentrantLock;
import de.invesdwin.util.concurrent.lock.internal.WrappedLock;
import de.invesdwin.util.concurrent.lock.internal.WrappedReentrantLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.TimeoutReadWriteLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.TimeoutReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.TracedReadWriteLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.TracedReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.WrappedReadWriteLock;
import de.invesdwin.util.concurrent.lock.internal.readwrite.WrappedReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;
import de.invesdwin.util.concurrent.lock.trace.internal.DisabledLockTrace;
import de.invesdwin.util.concurrent.lock.trace.internal.EnabledLockTrace;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.UniqueNameGenerator;
import de.invesdwin.util.time.duration.Duration;

@StaticFacadeDefinition(name = "de.invesdwin.util.concurrent.lock.internal.ALocksStaticFacade", targets = {
        com.googlecode.concurentlocks.Locks.class })
@Immutable
public final class Locks extends ALocksStaticFacade {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(Locks.class);

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator() {
        @Override
        public String get(final String name) {
            if (Strings.isBlank(name)) {
                throw new IllegalArgumentException("name should not be blank: " + name);
            }
            return super.get(name);
        }
    };

    private static ILockTrace lockTrace = DisabledLockTrace.INSTANCE;
    private static Duration lockWaitTimeout = null;
    private static boolean lockWaitTimeoutOnlyWriteLocks = false;

    /**
     * Keep it disabled by default to keep best performance.
     */
    private static CycleDetectingLockFactory cycleDetectingLockFactory = CycleDetectingLockFactory
            .newInstance(Policies.DISABLED);

    private Locks() {}

    public static IReentrantLock newReentrantLock(final String lockName) {
        final String name = UNIQUE_NAME_GENERATOR.get(lockName);
        final ReentrantLock lock = cycleDetectingLockFactory.newReentrantLock(name);
        return maybeWrap(name, lock);
    }

    public static IReentrantLock newReentrantLock(final String lockName, final boolean fair) {
        final String name = UNIQUE_NAME_GENERATOR.get(lockName);
        final ReentrantLock lock = cycleDetectingLockFactory.newReentrantLock(name, fair);
        return maybeWrap(name, lock);
    }

    public static IReentrantReadWriteLock newReentrantReadWriteLock(final String lockName) {
        final String name = UNIQUE_NAME_GENERATOR.get(lockName);
        final ReentrantReadWriteLock lock = cycleDetectingLockFactory.newReentrantReadWriteLock(name);
        return maybeWrap(name, lock);
    }

    public static IReentrantReadWriteLock newReentrantReadWriteLock(final String lockName, final boolean fair) {
        final String name = UNIQUE_NAME_GENERATOR.get(lockName);
        final ReentrantReadWriteLock lock = cycleDetectingLockFactory.newReentrantReadWriteLock(name, fair);
        return maybeWrap(name, lock);
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

    public static ILock maybeWrap(final String lockName, final Lock lock) {
        if (lock instanceof ILock) {
            return (ILock) lock;
        } else {
            return maybeWrapTimeout(maybeWrapTrace(lockName, lock));
        }
    }

    private static ILock maybeWrapTimeout(final ILock lock) {
        final Duration lockWaitTimeoutCopy = getLockWaitTimeout();
        if (lockWaitTimeoutCopy == null || isLockWaitTimeoutOnlyWriteLocks()) {
            return lock;
        } else {
            return new TimeoutLock(lock, lockWaitTimeoutCopy);
        }
    }

    private static ILock maybeWrapTrace(final String lockName, final Lock lock) {
        if (isLockTraceEnabled()) {
            return new TracedLock(lockName, lock);
        } else {
            return new WrappedLock(lockName, lock);
        }
    }

    public static IReentrantLock maybeWrap(final String lockName, final ReentrantLock lock) {
        if (lock instanceof IReentrantLock) {
            return (IReentrantLock) lock;
        } else {
            return maybeWrapTimeout(maybeWrapTrace(lockName, lock));
        }
    }

    private static IReentrantLock maybeWrapTimeout(final IReentrantLock lock) {
        final Duration lockWaitTimeoutCopy = getLockWaitTimeout();
        if (lockWaitTimeoutCopy == null || isLockWaitTimeoutOnlyWriteLocks()) {
            return lock;
        } else {
            return new TimeoutReentrantLock(lock, lockWaitTimeoutCopy);
        }
    }

    private static IReentrantLock maybeWrapTrace(final String lockName, final ReentrantLock lock) {
        if (isLockTraceEnabled()) {
            return new TracedReentrantLock(lockName, lock);
        } else {
            return new WrappedReentrantLock(lockName, lock);
        }
    }

    public static IReadWriteLock maybeWrap(final String lockName, final ReadWriteLock lock) {
        if (lock instanceof IReadWriteLock) {
            return (IReadWriteLock) lock;
        } else {
            return maybeWrapTimeout(maybeWrapTrace(lockName, lock));
        }
    }

    private static IReadWriteLock maybeWrapTimeout(final IReadWriteLock lock) {
        final Duration lockWaitTimeoutCopy = getLockWaitTimeout();
        if (lockWaitTimeoutCopy == null || isLockWaitTimeoutOnlyWriteLocks()) {
            return lock;
        } else {
            return new TimeoutReadWriteLock(lock, lockWaitTimeoutCopy);
        }
    }

    private static IReadWriteLock maybeWrapTrace(final String lockName, final ReadWriteLock lock) {
        if (isLockTraceEnabled()) {
            return new TracedReadWriteLock(lockName, lock);
        } else {
            return new WrappedReadWriteLock(lockName, lock);
        }
    }

    public static IReentrantReadWriteLock maybeWrap(final String lockName, final ReentrantReadWriteLock lock) {
        if (lock instanceof IReentrantReadWriteLock) {
            return (IReentrantReadWriteLock) lock;
        } else {
            return maybeWrapTimeout(maybeWrapTrace(lockName, lock));
        }
    }

    private static IReentrantReadWriteLock maybeWrapTimeout(final IReentrantReadWriteLock lock) {
        final Duration lockWaitTimeoutCopy = getLockWaitTimeout();
        if (lockWaitTimeoutCopy == null) {
            return lock;
        } else {
            return new TimeoutReentrantReadWriteLock(lock, lockWaitTimeoutCopy, isLockWaitTimeoutOnlyWriteLocks());
        }
    }

    private static IReentrantReadWriteLock maybeWrapTrace(final String lockName, final ReentrantReadWriteLock lock) {
        if (isLockTraceEnabled()) {
            return new TracedReentrantReadWriteLock(lockName, lock);
        } else {
            return new WrappedReentrantReadWriteLock(lockName, lock);
        }
    }

    public static ILockTrace getLockTrace() {
        return lockTrace;
    }

    public static void setLockTraceEnabled(final boolean lockTraceEnabled) {
        if (lockTraceEnabled) {
            if (!(Locks.lockTrace instanceof EnabledLockTrace)) {
                Locks.lockTrace = new EnabledLockTrace();
            }
        } else {
            Locks.lockTrace = DisabledLockTrace.INSTANCE;
        }
    }

    public static boolean isLockTraceEnabled() {
        return Locks.lockTrace != DisabledLockTrace.INSTANCE;
    }

    public static boolean isLockWaitTimeoutEnabled() {
        return lockWaitTimeout != null;
    }

    public static Duration getLockWaitTimeout() {
        return lockWaitTimeout;
    }

    public static boolean isLockWaitTimeoutOnlyWriteLocks() {
        return lockWaitTimeoutOnlyWriteLocks;
    }

    public static void setLockWaitTimeout(final Duration lockWaitTimeout) {
        setLockWaitTimeout(lockWaitTimeout, false);
    }

    public static void setLockWaitTimeout(final Duration lockWaitTimeout, final boolean onlyWriteLocks) {
        Locks.lockWaitTimeout = lockWaitTimeout;
        Locks.lockWaitTimeoutOnlyWriteLocks = onlyWriteLocks;
    }

    public static void timeoutLock(final ILock lock, final Duration lockWaitTimeout) {
        try {
            int tryCount = 0;
            while (!lock.tryLock(lockWaitTimeout.longValue(), lockWaitTimeout.getTimeUnit().timeUnitValue())) {
                tryCount++;
                LOG.catching(Locks.getLockTrace()
                        .handleLockException(lock.getName(), newLockTimeoutException(lockWaitTimeout, tryCount)));
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static TimeoutException newLockTimeoutException(final Duration lockWaitTimeout, final int tryCount) {
        return new TimeoutException(
                "timeout waiting for lock since [" + lockWaitTimeout.multiply(tryCount) + "] count: " + tryCount) {
            @Override
            public synchronized Throwable fillInStackTrace() {
                if (tryCount == 1) {
                    //only fill stack trace on first try
                    return super.fillInStackTrace();
                } else {
                    return null;
                }
            }

            @Override
            public String toString() {
                final String s = TimeoutException.class.getName();
                final String message = getLocalizedMessage();
                return (message != null) ? (s + ": " + message) : s;
            }
        };
    }

    public static Lock newCompositeLock(final Lock... locks) {
        if (locks == null || locks.length == 0) {
            return DisabledLock.INSTANCE;
        }
        final LinkedList<Lock> validLocks = new LinkedList<>();
        for (int i = 0; i < locks.length; i++) {
            final Lock lock = locks[i];
            if (lock != DisabledLock.INSTANCE) {
                validLocks.add(lock);
            }
        }
        if (validLocks.isEmpty()) {
            return DisabledLock.INSTANCE;
        } else {
            //CHECKSTYLE:OFF
            return new CompositeLock(validLocks);
            //CHECKSTYLE:ON
        }
    }

}
