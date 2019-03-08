package de.invesdwin.util.concurrent.lock.disabled;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledCondition implements Condition {

    public static final DisabledCondition INSTANCE = new DisabledCondition();

    private DisabledCondition() {}

    @Override
    public void await() throws InterruptedException {}

    @Override
    public void awaitUninterruptibly() {}

    @Override
    public long awaitNanos(final long nanosTimeout) throws InterruptedException {
        return 0;
    }

    @Override
    public boolean await(final long time, final TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public boolean awaitUntil(final Date deadline) throws InterruptedException {
        return false;
    }

    @Override
    public void signal() {}

    @Override
    public void signalAll() {}

}
