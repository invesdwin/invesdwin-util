package de.invesdwin.util.collections.iterable.count;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.loop.LoopInterruptedCheck;
import de.invesdwin.util.lang.string.ProcessedEventsRateString;
import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.log.ILog;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class CountingCloseableIterator<E> implements ICloseableIterator<E> {

    public static final Duration DEFAULT_CHECK_INTERVAL = Duration.FIVE_SECONDS;

    private final ILog log;
    private final TextDescription name;
    private final ICloseableIterator<E> delegate;

    private final LoopInterruptedCheck loopInterruptedCheck;
    private final long startNanos;
    private boolean logged;
    private long count;

    public CountingCloseableIterator(final ILog log, final TextDescription name, final ICloseableIterator<E> delegate) {
        this.log = log;
        this.name = name;
        this.delegate = delegate;
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
            log.info("%s(%s) loading at %s processing %s during %s", CountingCloseableIterator.class.getSimpleName(),
                    name, count, new ProcessedEventsRateString(count, duration), duration);
            logged = true;
        }
        return next;
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public void close() {
        delegate.close();
        if (logged) {
            final long elapsedNanos = System.nanoTime() - startNanos;
            final Duration duration = new Duration(elapsedNanos, FTimeUnit.NANOSECONDS);
            log.info("%s(%s) finished at %s processing %s after %s", CountingCloseableIterator.class.getSimpleName(),
                    name, count, new ProcessedEventsRateString(count, duration), duration);
            logged = false;
        }
    }

    public long getCount() {
        return count;
    }

}
