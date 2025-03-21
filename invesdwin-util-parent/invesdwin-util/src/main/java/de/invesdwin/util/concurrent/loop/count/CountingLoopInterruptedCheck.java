package de.invesdwin.util.concurrent.loop.count;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.loop.ILoopInterruptedCheck;

@NotThreadSafe
public class CountingLoopInterruptedCheck implements ILoopInterruptedCheck {

    private final int flushInterval;
    private int count = 0;

    public CountingLoopInterruptedCheck(final int flushInterval) {
        this.flushInterval = flushInterval;
    }

    @Override
    public void resetInterval() {
        count = 0;
    }

    @Override
    public boolean check() throws InterruptedException {
        return checkClock();
    }

    @Override
    public boolean checkClock() throws InterruptedException {
        count++;
        if (count % flushInterval == 0) {
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
