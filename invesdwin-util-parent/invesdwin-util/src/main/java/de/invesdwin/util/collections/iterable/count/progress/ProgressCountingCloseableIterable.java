package de.invesdwin.util.collections.iterable.count.progress;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.log.ILog;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class ProgressCountingCloseableIterable<E> implements ICloseableIterable<E> {

    public static final Duration DEFAULT_CHECK_INTERVAL = ProgressCountingCloseableIterator.DEFAULT_CHECK_INTERVAL;

    private final ILog log;
    private final TextDescription name;
    private final ICloseableIterable<? extends E> delegate;
    private final long size;

    public ProgressCountingCloseableIterable(final ILog log, final TextDescription name,
            final ICloseableIterable<? extends E> delegate, final long size) {
        this.log = log;
        this.name = name;
        this.delegate = delegate;
        this.size = size;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new ProgressCountingCloseableIterator<E>(log, name, delegate.iterator(), size) {
            @Override
            protected Duration newCheckInterval() {
                return ProgressCountingCloseableIterable.this.newCheckInterval();
            }
        };
    }

    protected Duration newCheckInterval() {
        return DEFAULT_CHECK_INTERVAL;
    }

}
