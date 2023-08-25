package de.invesdwin.util.collections.iterable.bytebuffer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class ByteBufferCloseableIterator<E> implements ICloseableIterator<E> {

    private final IByteBuffer buffer;
    private final ISerde<E> serde;
    private final int fixedLength;
    private final int limit;
    private int pos;

    public ByteBufferCloseableIterator(final IByteBuffer buffer, final ISerde<E> serde, final int fixedLength) {
        this.buffer = buffer;
        this.serde = serde;
        this.fixedLength = fixedLength;
        Assertions.checkTrue(fixedLength > 0);
        this.limit = buffer.capacity() / fixedLength;
        this.pos = 0;
    }

    @Override
    public boolean hasNext() {
        return pos < limit;
    }

    @Override
    public E next() {
        final E value = serde.fromBuffer(buffer.slice(pos, fixedLength));
        pos += fixedLength;
        return value;
    }

    @Override
    public void close() {
        pos = limit;
    }

}
