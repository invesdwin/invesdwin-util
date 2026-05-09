package de.invesdwin.util.concurrent.lock.trace;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.IReentrantLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;

public interface ILockTrace {

    void locked(String name);

    void unlocked(String name);

    RuntimeException handleLockException(String lockName, Throwable lockException);

    boolean isLockedByThisThread(String lockName);

    ILock wrap(String lockName, Lock lock);

    IReentrantLock wrap(String lockName, ReentrantLock lock);

    IReadWriteLock wrap(String lockName, ReadWriteLock lock);

    IReentrantReadWriteLock wrap(String lockName, ReentrantReadWriteLock lock);

    ILock maybeWrap(ILock lock);

    IReentrantLock maybeWrap(IReentrantLock lock);

    IReadWriteLock maybeWrap(IReadWriteLock lock);

    IReentrantReadWriteLock maybeWrap(IReentrantReadWriteLock lock);

}
