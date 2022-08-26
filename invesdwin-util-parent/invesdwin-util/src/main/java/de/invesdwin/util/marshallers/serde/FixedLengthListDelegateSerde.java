package de.invesdwin.util.marshallers.serde;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class FixedLengthListDelegateSerde<E> implements ISerde<List<? extends E>> {

    private final ISerde<E> delegate;
    private final int fixedLength;

    public FixedLengthListDelegateSerde(final ISerde<E> delegate, final int fixedLength) {
        this.delegate = delegate;
        this.fixedLength = fixedLength;
    }

    @Override
    public List<? extends E> fromBytes(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return Collections.emptyList();
        }
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final List<? extends E> objs) {
        return SerdeBaseMethods.toBytes(this, objs);
    }

    @Override
    public List<? extends E> fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return Collections.emptyList();
        }
        final int size = buffer.capacity() / fixedLength;
        final List<E> result = new ArrayList<E>(size);
        int curOffset = 0;
        for (int i = 0; i < size; i++) {
            final IByteBuffer slice = buffer.slice(curOffset, fixedLength);
            final E obj = delegate.fromBuffer(slice);
            result.add(obj);
            curOffset += fixedLength;
        }
        return result;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final List<? extends E> objs) {
        final int length = objs.size() * fixedLength;
        final int size = objs.size();
        int curOffset = 0;
        for (int i = 0; i < size; i++) {
            final E obj = objs.get(i);
            final IByteBuffer slice = buffer.slice(curOffset, fixedLength);
            final int objLength = delegate.toBuffer(slice, obj);
            if (objLength != fixedLength) {
                throw new IllegalArgumentException("Serialized object [" + obj + "] has unexpected byte length of ["
                        + objLength + "] while fixed length [" + fixedLength + "] was expected!");
            }
            curOffset += fixedLength;
        }
        return length;
    }

}
