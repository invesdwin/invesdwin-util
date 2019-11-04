package de.invesdwin.util.concurrent.lock.trace;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.error.Throwables;

@ThreadSafe
public final class LockTraceEntry extends Exception {

    private final String lockName;
    private final String threadName;

    public LockTraceEntry(final String lockName, final String threadName) {
        super("LockName [" + lockName + "] ThreadName [" + threadName + "]");
        this.lockName = lockName;
        this.threadName = threadName;
    }

    public String getLockName() {
        return lockName;
    }

    public String getThreadName() {
        return threadName;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        if (Throwables.isDebugStackTraceEnabled()) {
            super.fillInStackTrace();
        }
        return this;
    }
}