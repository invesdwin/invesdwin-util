package de.invesdwin.util.marshallers.serde;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.iterable.buffer.EmptyBufferingIterator;
import de.invesdwin.util.collections.iterable.buffer.IBufferingIterator;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class DynamicLengthBufferingIteratorDelegateSerde<E> implements ISerde<IBufferingIterator<? extends E>> {

    private final ISerde<E> delegate;

    @SuppressWarnings("unchecked")
    public DynamicLengthBufferingIteratorDelegateSerde(final ISerde<? extends E> delegate) {
        this.delegate = (ISerde<E>) delegate;
    }

    @Override
    public IBufferingIterator<? extends E> fromBytes(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return EmptyBufferingIterator.getInstance();
        }
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final IBufferingIterator<? extends E> objs) {
        return SerdeBaseMethods.toBytes(this, objs);
    }

    @Override
    public IBufferingIterator<? extends E> fromBuffer(final IByteBuffer buffer, final int length) {
        if (length == 0) {
            return EmptyBufferingIterator.getInstance();
        }
        int curOffset = 0;
        //COUNT
        final int size = buffer.getInt(curOffset);
        curOffset += Integer.BYTES;
        if (size == 0) {
            return EmptyBufferingIterator.getInstance();
        }
        final BufferingIterator<E> result = new BufferingIterator<E>();
        for (int i = 0; i < size; i++) {
            //OBJ_SIZE
            final int objSize = buffer.getInt(curOffset);
            curOffset += Integer.BYTES;
            //OBJ
            final IByteBuffer slice = buffer.slice(curOffset, objSize);
            final E obj = delegate.fromBuffer(slice, objSize);
            result.add(obj);
            curOffset += objSize;
        }
        return result;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final IBufferingIterator<? extends E> objs) {
        int curOffset = 0;
        //COUNT
        buffer.putInt(curOffset, objs.size());
        curOffset += Integer.BYTES;
        try {
            while (true) {
                final E obj = objs.next();
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
