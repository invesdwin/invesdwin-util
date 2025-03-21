package de.invesdwin.util.marshallers.serde.add;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class AddUndefinedBytesDelegateSerde<O> implements ISerde<O> {

    private final ISerde<O> delegate;
    private final int addedBytes;
    private Integer fixedLength;

    public AddUndefinedBytesDelegateSerde(final ISerde<O> delegate, final Integer delegateFixedLength,
            final int addedBytes) {
        this.delegate = delegate;
        this.addedBytes = addedBytes;
        if (delegateFixedLength == null) {
            this.fixedLength = null;
        } else {
            this.fixedLength = delegateFixedLength + addedBytes;
        }
    }

    public Integer getFixedLength() {
        return fixedLength;
    }

    @Override
    public O fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final O obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public O fromBuffer(final IByteBuffer buffer) {
        return delegate.fromBuffer(buffer.slice(0, buffer.capacity() - addedBytes));
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final O obj) {
        final int position = delegate.toBuffer(buffer, obj);
        clear(buffer, position, addedBytes);
        return position + addedBytes;
    }

    protected void clear(final IByteBuffer buffer, final int index, final int length) {
        buffer.ensureCapacity(index + length);
    }

}
