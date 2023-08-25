package de.invesdwin.util.collections.iterable.bytebuffer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.IReverseCloseableIterable;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public abstract class AByteBufferCloseableIterable<E> implements IReverseCloseableIterable<E> {

    protected abstract ISerde<E> getSerde();

    protected abstract int getFixedLength();

    protected abstract IByteBuffer getBuffer();

    @Override
    public ICloseableIterator<E> iterator() {
        return new ByteBufferCloseableIterator<>(getBuffer(), getSerde(), getFixedLength());
    }

    @Override
    public ICloseableIterator<E> reverseIterator() {
        return new ReverseByteBufferCloseableIterator<>(getBuffer(), getSerde(), getFixedLength());
    }

}
