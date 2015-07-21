package de.invesdwin.util.collections.iterable.concurrent;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@NotThreadSafe
public class ProducerQueueIterable<E> implements ICloseableIterable<E> {

    private String name;
    private ICloseableIterable<E> producer;
    private int queueSize;
    private boolean debugEnabled;

    public ProducerQueueIterable(final String name, final ICloseableIterable<E> producer) {
        this(name, producer, ProducerQueueIterator.DEFAULT_QUEUE_SIZE);
    }

    public ProducerQueueIterable(final String name, final ICloseableIterable<E> producer, final int queueSize) {
        this.name = name;
        this.producer = producer;
        this.queueSize = queueSize;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        final ProducerQueueIterator<E> iterator = new ProducerQueueIterator<E>(name, producer.iterator(), queueSize);
        if (debugEnabled) {
            iterator.withDebugEnabled();
        }
        return iterator;
    }

    public ProducerQueueIterable<E> withDebugEnabled() {
        this.debugEnabled = true;
        return this;
    }

}