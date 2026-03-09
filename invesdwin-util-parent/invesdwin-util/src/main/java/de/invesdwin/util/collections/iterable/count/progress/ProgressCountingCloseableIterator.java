package de.invesdwin.util.collections.iterable.count.progress;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.count.CountingCloseableIterator;
import de.invesdwin.util.concurrent.loop.LoopInterruptedCheck;
import de.invesdwin.util.lang.string.ProcessedEventsRateString;
import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.log.ILog;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class ProgressCountingCloseableIterator<E> implements ICloseableIterator<E> {

    public static final Duration DEFAULT_CHECK_INTERVAL = CountingCloseableIterator.DEFAULT_CHECK_INTERVAL;

    private final ILog log;
    private final TextDescription name;
    private final ICloseableIterator<? extends E> delegate;
    private final long size;

    private final LoopInterruptedCheck loopInterruptedCheck;
    private final long startNanos;
    private boolean logged;
    private long count;

    public ProgressCountingCloseableIterator(final ILog log, final TextDescription name,
            final ICloseableIterator<? extends E> delegate, final long size) {
        this.log = log;
        this.name = name;
        this.delegate = delegate;
        this.size = size;
        this.loopInterruptedCheck = new LoopInterruptedCheck(newCheckInterval());
        this.startNanos = System.nanoTime();
    }

    protected Duration newCheckInterval() {
        return DEFAULT_CHECK_INTERVAL;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public E next() {
        final E next = delegate.next();
        count++;
        if (log.isInfoEnabled() && loopInterruptedCheck.checkNoInterrupt()) {
            final long elapsedNanos = System.nanoTime() - startNanos;
            final Duration duration = new Duration(elapsedNanos, FTimeUnit.NANOSECONDS);
            log.info("%s(%s) iterating at %s (%s/%s) processing %s since %s",
                    ProgressCountingCloseableIterator.class.getSimpleName(), name,
                    new Percent(count, size).asScale(PercentScale.PERCENT), count, size,
                    new ProcessedEventsRateString(count, duration), duration);
            logged = true;
        }
        return next;
    }

    @Override
    public void close() {
        delegate.close();
        if (logged) {
            final long elapsedNanos = System.nanoTime() - startNanos;
            final Duration duration = new Duration(elapsedNanos, FTimeUnit.NANOSECONDS);
            log.info("%s(%s) finished at %s (%s/%s) processing %s after %s",
                    ProgressCountingCloseableIterator.class.getSimpleName(), name,
                    new Percent(count, size).asScale(PercentScale.PERCENT), count, size,
                    new ProcessedEventsRateString(count, duration), duration);
            logged = false;
        }
    }

    public long getCount() {
        return count;
    }

}
