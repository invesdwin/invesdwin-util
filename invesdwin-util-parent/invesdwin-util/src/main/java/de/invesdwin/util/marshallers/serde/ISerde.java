package de.invesdwin.util.marshallers.serde;

import de.invesdwin.util.lang.buffer.IByteBuffer;

public interface ISerde<O> {

    O fromBytes(byte[] bytes);

    byte[] toBytes(O obj);

    O fromBuffer(IByteBuffer buffer);

    int toBuffer(O obj, IByteBuffer buffer);

}