package de.invesdwin.util.collections.iterable.concurrent;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@NotThreadSafe
public class ProducerQueueIterable<E> implements ICloseableIterable<E> {

    private String name;
    private ICloseableIterable<E> producer;
    private int queueSize;
    private boolean utilizationDebugEnabled;

    public ProducerQueueIterable(final String name, final ICloseableIterable<E> producer) {
        this(name, producer, AProducerQueueIterator.DEFAULT_QUEUE_SIZE);
    }

    public ProducerQueueIterable(final String name, final ICloseableIterable<E> producer, final int queueSize) {
        this.name = name;
        this.producer = producer;
        this.queueSize = queueSize;
    }

    @Override
    public ACloseableIterator<E> iterator() {
        final AProducerQueueIterator<E> iterator = new AProducerQueueIterator<E>(name, queueSize) {
            @Override
            protected ICloseableIterator<E> newProducer() {
                return producer.iterator();
            }
        };
        if (utilizationDebugEnabled) {
            iterator.withUtilizationDebugEnabled();
        }
        return iterator;
    }

    public ProducerQueueIterable<E> withUtilizationDebugEnabled() {
        this.utilizationDebugEnabled = true;
        return this;
    }

    public boolean isUtilizationDebugEnabled() {
        return utilizationDebugEnabled;
    }

}