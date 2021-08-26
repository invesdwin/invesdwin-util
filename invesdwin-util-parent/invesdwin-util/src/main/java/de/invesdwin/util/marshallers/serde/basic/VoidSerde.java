package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Bytes;

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
    public Void fromBuffer(final IByteBuffer buffer, final int length) {
        return null;
    }

    @Override
    public int toBuffer(final Void obj, final IByteBuffer buffer) {
        return 0;
    }

}
