package de.invesdwin.util.collections.iterable.bytebuffer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class ReverseByteBufferCloseableIterator<E> implements ICloseableIterator<E> {

    private final IByteBuffer buffer;
    private final ISerde<E> serde;
    private final int fixedLength;
    private int pos = 0;

    public ReverseByteBufferCloseableIterator(final IByteBuffer buffer, final ISerde<E> serde, final int fixedLength) {
        this.buffer = buffer;
        this.serde = serde;
        this.fixedLength = fixedLength;
        Assertions.checkTrue(fixedLength > 0);
        final int limit = buffer.capacity();
        this.pos = limit;
    }

    @Override
    public boolean hasNext() {
        return pos > 0;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw FastNoSuchElementException.getInstance("end reached");
        }
        pos -= fixedLength;
        final E value = serde.fromBuffer(buffer.slice(pos, fixedLength));
        return value;
    }

    @Override
    public void close() {
        pos = 0;
    }

}
