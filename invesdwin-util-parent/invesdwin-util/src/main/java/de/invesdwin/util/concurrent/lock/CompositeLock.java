package de.invesdwin.util.concurrent.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class CompositeLock implements ILock {

    private final ILock[] locks;
    private final String name;

    public CompositeLock(final ILock... locks) {
        this.locks = locks;
        this.name = newName();
    }

    private String newName() {
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < locks.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(locks[i].getName());
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isLocked() {
        for (int i = 0; i < locks.length; i++) {
            if (locks[i].isLocked()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isLockedByCurrentThread() {
        for (int i = 0; i < locks.length; i++) {
            if (locks[i].isLockedByCurrentThread()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void lock() {
        Locks.lockAll(locks);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        Locks.lockInterruptiblyAll(locks);
    }

    @Override
    public boolean tryLock() {
        return Locks.tryLockAll(locks);
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        return Locks.tryLockAll(time, unit, locks);
    }

    @Override
    public void unlock() {
        // Unlock in reverse order...
        Locks.unlockAllReverse(locks);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("This lock does not support conditions");
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(name).addValue(Arrays.toString(locks)).toString();
    }

    public static ILock valueOf(final ILock[] locks) {
        if (locks == null || locks.length == 0) {
            return DisabledLock.INSTANCE;
        }
        if (locks.length == 1) {
            final ILock lock = locks[0];
            if (lock == null) {
                return DisabledLock.INSTANCE;
            } else {
                return lock;
            }
        }
        final List<ILock> validLocks = new ArrayList<>(locks.length);
        for (int i = 0; i < locks.length; i++) {
            final ILock lock = locks[i];
            if (lock != null && lock != DisabledLock.INSTANCE) {
                validLocks.add(lock);
            }
        }
        if (validLocks.size() == 1) {
            return validLocks.get(0);
        }
        if (validLocks.isEmpty()) {
            return DisabledLock.INSTANCE;
        } else {
            //CHECKSTYLE:OFF
            return new CompositeLock(validLocks.toArray(ILock.EMPTY_ARRAY));
            //CHECKSTYLE:ON
        }
    }

}
