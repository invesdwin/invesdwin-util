package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class EmptyGenericArray<E> implements IGenericArray<E> {

    @SuppressWarnings("rawtypes")
    private static final EmptyGenericArray INSTANCE = new EmptyGenericArray();

    private EmptyGenericArray() {}

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
    public IGenericArray<E> slice(final int fromIndex, final int length) {
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
    public void getGenerics(final int srcPos, final IGenericArray<E> dest, final int destPos, final int length) {
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

    @SuppressWarnings("unchecked")
    public static <T> EmptyGenericArray<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public void clear() {}

}
