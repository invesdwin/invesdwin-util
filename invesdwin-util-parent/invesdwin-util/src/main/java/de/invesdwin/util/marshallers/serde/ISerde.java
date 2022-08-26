package de.invesdwin.util.marshallers.serde;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface ISerde<O> {

    O fromBytes(byte[] bytes);

    byte[] toBytes(O obj);

    O fromBuffer(IByteBuffer buffer);

    int toBuffer(IByteBuffer buffer, O obj);

}