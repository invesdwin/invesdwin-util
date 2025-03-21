package de.invesdwin.util.marshallers.serde.add;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class AddRandomBytesDelegateSerde<O> extends AddUndefinedBytesDelegateSerde<O> {

    private final IRandomGenerator random;

    public AddRandomBytesDelegateSerde(final ISerde<O> delegate, final Integer delegateFixedLength,
            final int addedBytes, final IRandomGenerator random) {
        super(delegate, delegateFixedLength, addedBytes);
        this.random = random;
    }

    @Override
    protected void clear(final IByteBuffer buffer, final int index, final int length) {
        buffer.clear(random, index, length);
    }

}
