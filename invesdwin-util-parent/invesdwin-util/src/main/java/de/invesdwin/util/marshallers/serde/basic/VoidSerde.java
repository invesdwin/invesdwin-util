package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class VoidSerde implements ISerde<Void> {

    public static final VoidSerde GET = new VoidSerde();
    public static final int FIXED_LENGTH = 0;

    @Override
    public Void fromBytes(final byte[] bytes) {
        return null;
    }

    @Override
    public byte[] toBytes(final Void obj) {
        return Bytes.EMPTY_ARRAY;
    }

    @Override
    public Void fromBuffer(final IByteBuffer buffer) {
        return null;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final Void obj) {
        return 0;
    }

}
