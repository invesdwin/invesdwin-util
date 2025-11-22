package de.invesdwin.util.concurrent.loop.count;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.loop.ILoopInterruptedCheck;

@NotThreadSafe
public class SynchronizedCountingLoopInterruptedCheck implements ILoopInterruptedCheck {

    private final int flushInterval;
    private final AtomicInteger count = new AtomicInteger();

    public SynchronizedCountingLoopInterruptedCheck(final int flushInterval) {
        this.flushInterval = flushInterval;
    }

    @Override
    public void resetInterval() {
        count.set(0);
    }

    @Override
    public boolean check() throws InterruptedException {
        return checkClock();
    }

    @Override
    public boolean checkClock() throws InterruptedException {
        final int c = count.incrementAndGet();
        if (c % flushInterval == 0) {
            return onInterval();
        } else {
            return false;
        }
    }

    protected boolean onInterval() throws InterruptedException {
        Threads.throwIfInterrupted(Thread.currentThread());
        return true;
    }

}
