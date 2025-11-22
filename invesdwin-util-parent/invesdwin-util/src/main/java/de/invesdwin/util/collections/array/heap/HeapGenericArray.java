package de.invesdwin.util.collections.array.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.IGenericArray;
import de.invesdwin.util.collections.array.SliceDelegateGenericArray;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class HeapGenericArray<E> implements IGenericArray<E> {

    private final E[] values;

    @SuppressWarnings("unchecked")
    public HeapGenericArray(final Class<E> type, final int size) {
        this.values = (E[]) Arrays.newInstance(type, size);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public HeapGenericArray(final E[] emptyArray, final int size) {
        this((Class) emptyArray.getClass().getComponentType(), size);
    }

    public HeapGenericArray(final E[] values) {
        this.values = values;
    }

    @Override
    public int getId() {
        return System.identityHashCode(values);
    }

    @Override
    public void set(final int index, final E value) {
        values[index] = value;
    }

    @Override
    public E get(final int index) {
        return values[index];
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public IGenericArray<E> slice(final int fromIndex, final int length) {
        return new SliceDelegateGenericArray<E>(this, fromIndex, length);
    }

    @Override
    public E[] asArray() {
        return values;
    }

    @Override
    public E[] asArray(final int fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArray();
        } else {
            return Arrays.copyOfRange(values, fromIndex, fromIndex + length);
        }
    }

    @Override
    public E[] asArrayCopy() {
        return values.clone();
    }

    @Override
    public E[] asArrayCopy(final int fromIndex, final int length) {
        if (fromIndex == 0 && length == size()) {
            return asArrayCopy();
        } else {
            return Arrays.copyOfRange(values, fromIndex, fromIndex + length);
        }
    }

    @Override
    public void getGenerics(final int srcPos, final IGenericArray<E> dest, final int destPos, final int length) {
        final HeapGenericArray<E> cDest = ((HeapGenericArray<E>) dest);
        System.arraycopy(values, srcPos, cDest.values, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

}
