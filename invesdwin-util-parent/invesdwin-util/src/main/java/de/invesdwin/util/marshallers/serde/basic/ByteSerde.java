package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class ByteSerde implements ISerde<byte[]> {

    public static final ByteSerde GET = new ByteSerde();

    @Override
    public byte[] fromBytes(final byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] toBytes(final byte[] obj) {
        return obj;
    }

    @Override
    public byte[] fromBuffer(final IByteBuffer buffer) {
        return SerdeBaseMethods.fromBuffer(this, buffer);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final byte[] obj) {
        return SerdeBaseMethods.toBuffer(this, buffer, obj);
    }
}
