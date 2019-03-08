package de.invesdwin.util.concurrent.lock.trace;

public interface ILockTrace {

    void locked(String name);

    void unlocked(String name);

    RuntimeException handleLockException(String lockName, Throwable lockException);

    boolean isLockedByThisThread(String lockName);

}
