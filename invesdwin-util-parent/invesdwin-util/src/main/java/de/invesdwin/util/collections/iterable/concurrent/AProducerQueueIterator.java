package de.invesdwin.util.collections.iterable.concurrent;

import java.util.function.Consumer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ADelegateCloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@NotThreadSafe
public abstract class AProducerQueueIterator<E> extends AGenericProducerQueueIterator<E> {

    private final ICloseableIterator<E> producer;

    public AProducerQueueIterator(final String name) {
        this(name, DEFAULT_QUEUE_SIZE);
    }

    public AProducerQueueIterator(final String name, final int queueSize) {
        super(name, queueSize);
        this.producer = new ADelegateCloseableIterator<E>() {

            @Override
            protected ICloseableIterator<E> newDelegate() {
                return AProducerQueueIterator.this.newProducer();
            }
        };
        start();
    }

    protected abstract ICloseableIterator<E> newProducer();

    @Override
    protected void internalProduce(final Consumer<E> consumer) {
        while (!isInnerClosed() && producer.hasNext()) {
            final E next = producer.next();
            consumer.accept(next);
        }
    }

    @Override
    protected void internalCloseProducer() {
        producer.close();
    }

    @Override
    public AProducerQueueIterator<E> withUtilizationDebugEnabled() {
        super.withUtilizationDebugEnabled();
        return this;
    }

}