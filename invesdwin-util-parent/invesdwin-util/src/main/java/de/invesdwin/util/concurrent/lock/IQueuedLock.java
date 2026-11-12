package de.invesdwin.util.concurrent.lock;

/**
 * A lock that can report how many threads are queued for it - the lock of a read-write pair shares ONE queue between
 * its read view and its write view, so for a view the number is always the number of the whole pair.
 */
public interface IQueuedLock extends ILock {

    int getQueueLength();

}
