package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.log.ILog;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class CountingCloseableIterable<E> implements ICloseableIterable<E> {

    public static final Duration DEFAULT_CHECK_INTERVAL = CountingCloseableIterator.DEFAULT_CHECK_INTERVAL;

    private final ILog log;
    private final TextDescription name;
    private final ICloseableIterable<E> delegate;

    public CountingCloseableIterable(final ILog log, final TextDescription name, final ICloseableIterable<E> delegate) {
        this.log = log;
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new CountingCloseableIterator<E>(log, name, delegate.iterator()) {
            @Override
            protected Duration newCheckInterval() {
                return CountingCloseableIterable.this.newCheckInterval();
            }
        };
    }

    protected Duration newCheckInterval() {
        return DEFAULT_CHECK_INTERVAL;
    }

}
