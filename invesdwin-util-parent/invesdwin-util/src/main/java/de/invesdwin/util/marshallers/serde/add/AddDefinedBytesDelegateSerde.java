package de.invesdwin.util.marshallers.serde.add;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class AddDefinedBytesDelegateSerde<O> extends AddUndefinedBytesDelegateSerde<O> {

    private final byte value;

    public AddDefinedBytesDelegateSerde(final ISerde<O> delegate, final Integer delegateFixedLength,
            final int addedBytes, final byte value) {
        super(delegate, delegateFixedLength, addedBytes);
        this.value = value;
    }

    @Override
    protected void clear(final IByteBuffer buffer, final int index, final int length) {
        buffer.clear(value, index, length);
    }

}
