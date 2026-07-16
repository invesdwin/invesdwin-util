package de.invesdwin.util.collections.array.primitive.empty;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.primitive.IGenericPrimitiveArray;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class EmptyGenericPrimitiveArray<E> implements IGenericPrimitiveArray<E> {

    @SuppressWarnings("rawtypes")
    private static final EmptyGenericPrimitiveArray INSTANCE = new EmptyGenericPrimitiveArray();

    private EmptyGenericPrimitiveArray() {}

    @Override
    public int getId() {
        return ID_EMPTY;
    }

    @Override
    public void set(final int index, final E value) {}

    @Override
    public E get(final int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public IGenericPrimitiveArray<E> slice(final int fromIndex, final int length) {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] asArray() {
        return (E[]) Objects.EMPTY_ARRAY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] asArray(final int fromIndex, final int length) {
        return (E[]) Objects.EMPTY_ARRAY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] asArrayCopy() {
        return (E[]) Objects.EMPTY_ARRAY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] asArrayCopy(final int fromIndex, final int length) {
        return (E[]) Objects.EMPTY_ARRAY;
    }

    @Override
    public void getGenerics(final int srcPos, final IGenericPrimitiveArray<E> dest, final int destPos, final int length) {
        //noop
    }

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        return 0;
    }

    @Override
    public int getBufferLength() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptyGenericPrimitiveArray<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public void clear() {}

}
