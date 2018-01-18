package de.invesdwin.util.concurrent;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.shutdown.ShutdownHookManager;

@Immutable
public final class Threads {

    public static final String NESTED_THREAD_NAME_SEPARATOR = " <- ";

    private Threads() {}

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
        final String curThreadName = Strings.substringBefore(Thread.currentThread().getName(),
                NESTED_THREAD_NAME_SEPARATOR);
        Thread.currentThread()
                .setName(Strings.eventuallyAddSuffix(curThreadName, NESTED_THREAD_NAME_SEPARATOR + parentThreadName));
    }

    public static int getCurrentNestedThreadLevel(final String threadNameContains) {
        int nestedLevel = 0;
        final String threadname = Thread.currentThread().getName();
        final String[] nestedThreads = Strings.splitByWholeSeparator(threadname, Threads.NESTED_THREAD_NAME_SEPARATOR);
        for (final String nestedThread : nestedThreads) {
            if (nestedThread.contains(threadNameContains)) {
                nestedLevel++;
            }
        }
        return nestedLevel;
    }

}
