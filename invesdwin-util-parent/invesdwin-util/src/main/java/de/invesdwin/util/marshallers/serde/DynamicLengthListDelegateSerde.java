package de.invesdwin.util.marshallers.serde;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class DynamicLengthListDelegateSerde<E> implements ISerde<List<? extends E>> {

    private final ISerde<E> delegate;

    @SuppressWarnings("unchecked")
    public DynamicLengthListDelegateSerde(final ISerde<? extends E> delegate) {
        this.delegate = (ISerde<E>) delegate;
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
        int curOffset = 0;
        //COUNT
        final int size = buffer.getInt(curOffset);
        curOffset += Integer.BYTES;
        if (size == 0) {
            return Collections.emptyList();
        }
        final List<E> result = new ArrayList<E>();
        for (int i = 0; i < size; i++) {
            //OBJ_SIZE
            final int objSize = buffer.getInt(curOffset);
            curOffset += Integer.BYTES;
            //OBJ
            final IByteBuffer slice = buffer.slice(curOffset, objSize);
            final E obj = delegate.fromBuffer(slice);
            result.add(obj);
            curOffset += objSize;
        }
        return result;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final List<? extends E> objs) {
        int curOffset = 0;
        //COUNT
        buffer.putInt(curOffset, objs.size());
        curOffset += Integer.BYTES;
        try {
            for (int i = 0; i < objs.size(); i++) {
                final E obj = objs.get(i);
                final IByteBuffer slice = buffer.sliceFrom(curOffset + Integer.BYTES);
                final int objLength = delegate.toBuffer(slice, obj);
                buffer.putInt(curOffset, objLength);
                //OBJ_SIZE
                curOffset += Integer.BYTES;
                //OBJ
                curOffset += objLength;
            }
        } catch (final NoSuchElementException e) {
            //end reached
        }
        return curOffset;
    }

}
