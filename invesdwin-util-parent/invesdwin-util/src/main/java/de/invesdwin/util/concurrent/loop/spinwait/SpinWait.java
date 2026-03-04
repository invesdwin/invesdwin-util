package de.invesdwin.util.concurrent.loop.spinwait;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public class SpinWait extends ABaseSpinWait {

    @Override
    public boolean awaitFulfill(final ISpinWaitCondition condition, final Instant waitingSince) throws Exception {
        return super.awaitFulfill(condition, waitingSince);
    }

    @Override
    public boolean awaitFulfill(final ISpinWaitCondition condition, final long waitingSinceNanos) throws Exception {
        return super.awaitFulfill(condition, waitingSinceNanos);
    }

    @Override
    public boolean awaitFulfill(final ISpinWaitCondition condition, final Instant waitingSince, final Duration maxWait)
            throws Exception {
        return super.awaitFulfill(condition, waitingSince, maxWait);
    }

    @Override
    public boolean awaitFulfill(final ISpinWaitCondition condition, final long waitingSinceNanos,
            final Duration maxWait) throws Exception {
        return super.awaitFulfill(condition, waitingSinceNanos, maxWait);
    }

    @Override
    public boolean awaitFulfill(final ISpinWaitCondition condition, final long waitingSinceNanos,
            final long maxWaitTime, final TimeUnit maxWaitUnit) throws Exception {
        return super.awaitFulfill(condition, waitingSinceNanos, maxWaitTime, maxWaitUnit);
    }

    @Override
    protected boolean awaitFulfill(final ISpinWaitCondition condition, final long waitingSinceNanos,
            final long maxWaitNanos) throws Exception {
        return super.awaitFulfill(condition, waitingSinceNanos, maxWaitNanos);
    }

}
