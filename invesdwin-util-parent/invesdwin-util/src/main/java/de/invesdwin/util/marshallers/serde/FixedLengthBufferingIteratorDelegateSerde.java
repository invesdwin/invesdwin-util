package de.invesdwin.util.marshallers.serde;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.buffer.BufferingIterator;
import de.invesdwin.util.collections.iterable.buffer.EmptyBufferingIterator;
import de.invesdwin.util.collections.iterable.buffer.IBufferingIterator;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class FixedLengthBufferingIteratorDelegateSerde<E>
        implements ISerde<IBufferingIterator<? extends E>>, IFlyweightSerdeProvider<IBufferingIterator<? extends E>> {

    private final ISerde<E> delegate;
    private final int fixedLength;

    @SuppressWarnings("unchecked")
    public FixedLengthBufferingIteratorDelegateSerde(final ISerde<? extends E> delegate, final int fixedLength) {
        this.delegate = (ISerde<E>) delegate;
        this.fixedLength = fixedLength;
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
    public IBufferingIterator<? extends E> fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return EmptyBufferingIterator.getInstance();
        }
        final int size = buffer.capacity() / fixedLength;
        final BufferingIterator<E> result = new BufferingIterator<E>();
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
    public int toBuffer(final IByteBuffer buffer, final IBufferingIterator<? extends E> objs) {
        final int length = objs.size() * fixedLength;
        int curOffset = 0;
        try {
            while (true) {
                final E obj = objs.next();
                final IByteBuffer slice = buffer.slice(curOffset, fixedLength);
                final int objLength = delegate.toBuffer(slice, obj);
                if (objLength != fixedLength) {
                    throw new IllegalArgumentException("Serialized object [" + obj + "] has unexpected byte length of ["
                            + objLength + "] while fixed length [" + fixedLength + "] was expected!");
                }
                curOffset += fixedLength;
            }
        } catch (final NoSuchElementException e) {
            //end reached
        }
        return length;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ISerde<IBufferingIterator<? extends E>> asFlyweightSerde() {
        if (delegate instanceof IFlyweightSerdeProvider) {
            final IFlyweightSerdeProvider<E> flyweightSerdeProvider = (IFlyweightSerdeProvider<E>) delegate;
            final ISerde<E> flyweightSerde = flyweightSerdeProvider.asFlyweightSerde();
            if (flyweightSerde != null) {
                return new FixedLengthBufferingIteratorDelegateSerde<E>(flyweightSerde, fixedLength);
            }
        }
        return null;
    }

}
