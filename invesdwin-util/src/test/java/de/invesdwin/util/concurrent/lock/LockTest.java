package de.invesdwin.util.concurrent.lock;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;
import de.invesdwin.util.time.Instant;

// CHECKSTYLE:OFF
@NotThreadSafe
public class LockTest {

    private static final int ITERATIONS = 10_000_000;
    private static final int REPETITIONS = 10;

    private final ILock lock = Locks.newReentrantLock("lock");
    private final Object lockObject = new Object();
    private final ILock lockDisabled = DisabledLock.INSTANCE;

    private Object value;

    private Object getRaw() {
        if (value == null) {
            value = new Object();
        }
        return value;
    }

    private Object getLock() {
        lock.lock();
        try {
            if (value == null) {
                value = new Object();
            }
            return value;
        } finally {
            lock.unlock();
        }
    }

    private Object getSynchronized() {
        synchronized (lockObject) {
            if (value == null) {
                value = new Object();
            }
            return value;
        }
    }

    private Object getDisabled() {
        lockDisabled.lock();
        try {
            if (value == null) {
                value = new Object();
            }
            return value;
        } finally {
            lockDisabled.unlock();
        }
    }

    private Object getDoubleCheckedRaw() {
        if (value == null) {
            if (value == null) {
                value = new Object();
            }
        }
        return value;
    }

    private Object getDoubleCheckedLock() {
        if (value == null) {
            lock.lock();
            try {
                if (value == null) {
                    value = new Object();
                }
            } finally {
                lock.unlock();
            }
        }
        return value;
    }

    private Object getDoubleCheckedSynchronized() {
        if (value == null) {
            synchronized (lockObject) {
                if (value == null) {
                    value = new Object();
                }
            }
        }
        return value;
    }

    private Object getDoubleCheckedLockDisabled() {
        if (value == null) {
            lockDisabled.lock();
            try {
                if (value == null) {
                    value = new Object();
                }
            } finally {
                lockDisabled.unlock();
            }
        }
        return value;
    }

    @Test
    public void test() {
        for (int i = 0; i < REPETITIONS; i++) {
            System.out.println("******************************************");
            value = null;
            testRaw();
            value = null;
            testLock();
            value = null;
            testSynchronized();
            value = null;
            testDisabledLock();
            value = null;
            testDoubleCheckedRaw();
            value = null;
            testDoubleCheckedLock();
            value = null;
            testDoubleCheckedSynchronized();
            value = null;
            testDoubleCheckedDisabledLock();
        }
    }

    private void testRaw() {
        final Instant start = new Instant();

        for (int i = 0; i < ITERATIONS; i++) {
            Assertions.checkNotNull(getRaw());
        }

        System.out.println("testRaw: " + start);
    }

    private void testLock() {
        final Instant start = new Instant();

        for (int i = 0; i < ITERATIONS; i++) {
            Assertions.checkNotNull(getLock());
        }

        System.out.println("testLock: " + start);
    }

    private void testSynchronized() {
        final Instant start = new Instant();

        for (int i = 0; i < ITERATIONS; i++) {
            Assertions.checkNotNull(getSynchronized());
        }

        System.out.println("testSynchronized: " + start);
    }

    private void testDisabledLock() {
        final Instant start = new Instant();

        for (int i = 0; i < ITERATIONS; i++) {
            Assertions.checkNotNull(getDisabled());
        }

        System.out.println("testDisabledLock: " + start);
    }

    private void testDoubleCheckedRaw() {
        final Instant start = new Instant();

        for (int i = 0; i < ITERATIONS; i++) {
            Assertions.checkNotNull(getDoubleCheckedRaw());
        }

        System.out.println("testDoubleCheckedRaw: " + start);
    }

    private void testDoubleCheckedLock() {
        final Instant start = new Instant();

        for (int i = 0; i < ITERATIONS; i++) {
            Assertions.checkNotNull(getDoubleCheckedLock());
        }

        System.out.println("testDoubleCheckedLock: " + start);
    }

    private void testDoubleCheckedSynchronized() {
        final Instant start = new Instant();

        for (int i = 0; i < ITERATIONS; i++) {
            Assertions.checkNotNull(getDoubleCheckedSynchronized());
        }

        System.out.println("testDoubleCheckedSynchronized: " + start);
    }

    private void testDoubleCheckedDisabledLock() {
        final Instant start = new Instant();

        for (int i = 0; i < ITERATIONS; i++) {
            Assertions.checkNotNull(getDoubleCheckedLockDisabled());
        }

        System.out.println("testDoubleCheckedDisabledLock: " + start);
    }

}
