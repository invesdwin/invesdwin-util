package de.invesdwin.util.concurrent.loop;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.loop.base.ABaseSpinWait;
import de.invesdwin.util.concurrent.loop.base.ISpinWaitCondition;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public abstract class ASpinWait extends ABaseSpinWait implements ISpinWaitCondition {

    public boolean awaitFulfill(final Instant waitingSince) throws Exception {
        return super.awaitFulfill(this, waitingSince);
    }

    public boolean awaitFulfill(final long waitingSinceNanos) throws Exception {
        return super.awaitFulfill(this, waitingSinceNanos);
    }

    public boolean awaitFulfill(final Instant waitingSince, final Duration maxWait) throws Exception {
        return super.awaitFulfill(this, waitingSince, maxWait);
    }

    public boolean awaitFulfill(final long waitingSinceNanos, final Duration maxWait) throws Exception {
        return super.awaitFulfill(this, waitingSinceNanos, maxWait);
    }

    public boolean awaitFulfill(final long waitingSinceNanos, final long maxWaitTime, final TimeUnit maxWaitUnit)
            throws Exception {
        return super.awaitFulfill(this, waitingSinceNanos, maxWaitTime, maxWaitUnit);
    }

    public boolean awaitFulfill(final long waitingSinceNanos, final long maxWaitNanos) throws Exception {
        return super.awaitFulfill(this, waitingSinceNanos, maxWaitNanos);
    }

}
