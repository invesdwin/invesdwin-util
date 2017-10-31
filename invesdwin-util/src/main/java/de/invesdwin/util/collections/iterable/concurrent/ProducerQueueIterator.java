package de.invesdwin.util.collections.iterable.concurrent;

import java.util.function.Consumer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;

@NotThreadSafe
public class ProducerQueueIterator<E> extends AProducerQueueIterator<E> {

    private ICloseableIterator<E> producer;

    public ProducerQueueIterator(final String name, final ICloseableIterator<E> producer) {
        this(name, producer, DEFAULT_QUEUE_SIZE);
    }

    public ProducerQueueIterator(final String name, final ICloseableIterator<E> producer, final int queueSize) {
        super(name, queueSize);
        this.producer = producer;
    }

    @Override
    protected void internalProduce(final Consumer<E> consumer) {
        while (!isInnerClosed() && producer.hasNext()) {
            final E next = producer.next();
            consumer.accept(next);
        }
    }

    @Override
    protected void internalClose() {
        producer.close();
    }

}