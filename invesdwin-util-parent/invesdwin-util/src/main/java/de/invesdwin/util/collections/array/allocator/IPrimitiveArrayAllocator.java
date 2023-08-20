package de.invesdwin.util.collections.array.allocator;

import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.IIntegerArray;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface IPrimitiveArrayAllocator {

    IByteBuffer getByteBuffer(String id);

    IDoubleArray getDoubleArray(String id);

    IIntegerArray getIntegerArray(String id);

    IBooleanArray getBooleanArray(String id);

    IBitSet getBitSet(String id);

    ILongArray getLongArray(String id);

    IByteBuffer newByteBuffer(String id, int size);

    IDoubleArray newDoubleArray(String id, int size);

    IIntegerArray newIntegerArray(String id, int size);

    IBooleanArray newBooleanArray(String id, int size);

    IBitSet newBitSet(String id, int size);

    ILongArray newLongArray(String id, int size);

    <T> T unwrap(Class<T> type);

    IAttributesMap getAttributes();

}
