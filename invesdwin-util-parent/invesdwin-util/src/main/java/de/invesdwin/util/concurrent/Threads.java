package de.invesdwin.util.concurrent;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.shutdown.ShutdownHookManager;
import io.netty.util.concurrent.FastThreadLocal;

@Immutable
public final class Threads {

    public static final String NESTED_THREAD_NAME_SEPARATOR = " <- ";
    /**
     * Above 1998 character there can occur deadlocks when settings thread names
     */
    private static final int MAX_THREAD_NAME_LENGTH = 1800;

    private static final FastThreadLocal<String> THREAD_POOL_NAME = new FastThreadLocal<>();

    private Threads() {
    }

    public static void throwIfInterrupted(final Thread thread) throws InterruptedException {
        if (isInterrupted(thread)) {
            throw new InterruptedException();
        }
    }

    public static void throwIfInterrupted() throws InterruptedException {
        throwIfInterrupted(Thread.currentThread());
    }

    public static boolean isInterrupted() {
        return isInterrupted(Thread.currentThread());
    }

    public static boolean isInterrupted(final Thread thread) {
        return thread.isInterrupted() || ShutdownHookManager.isShuttingDown();
    }

    public static void updateParentThreadName(final String parentThreadName) {
        final String curThreadName = getCurrentThreadName();
        if (curThreadName.endsWith(parentThreadName)) {
            return;
        }
        final String curRootThreadName = Strings.substringBefore(curThreadName, NESTED_THREAD_NAME_SEPARATOR);
        if (curRootThreadName.endsWith(parentThreadName)) {
            return;
        }
        final String newThreadName = curRootThreadName + NESTED_THREAD_NAME_SEPARATOR + parentThreadName;
        setCurrentThreadName(newThreadName);
    }

    public static void setCurrentThreadName(final String newThreadName) {
        if (getCurrentThreadName().equals(newThreadName)) {
            return;
        }
        if (newThreadName.length() > MAX_THREAD_NAME_LENGTH) {
            throw new IllegalStateException("Thread name length [" + newThreadName.length() + "] should be less than ["
                    + MAX_THREAD_NAME_LENGTH + "] characters: " + newThreadName);
        }
        Thread.currentThread().setName(newThreadName);
    }

    public static int getCurrentNestedThreadLevel(final String threadNameContains) {
        int nestedLevel = 0;
        final String threadname = getCurrentThreadName();
        final String[] nestedThreads = Strings.splitByWholeSeparator(threadname, Threads.NESTED_THREAD_NAME_SEPARATOR);
        for (final String nestedThread : nestedThreads) {
            if (nestedThread.contains(threadNameContains)) {
                nestedLevel++;
            }
        }
        return nestedLevel;
    }

    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    public static String getCurrentRootThreadName() {
        final String curThreadName = getCurrentThreadName();
        final String curRootThreadName = Strings.substringBefore(curThreadName, NESTED_THREAD_NAME_SEPARATOR);
        return curRootThreadName;
    }

    public static String getCurrentThreadPoolName() {
        return THREAD_POOL_NAME.get();
    }

    public static void setCurrentThreadPoolName(final String name) {
        if (name == null) {
            THREAD_POOL_NAME.remove();
        } else {
            THREAD_POOL_NAME.set(name);
        }
    }

}
