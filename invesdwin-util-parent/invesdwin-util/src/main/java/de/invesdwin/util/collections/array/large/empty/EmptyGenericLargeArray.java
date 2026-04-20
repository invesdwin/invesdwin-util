package de.invesdwin.util.collections.array.large.empty;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public final class EmptyGenericLargeArray<E> implements IGenericLargeArray<E> {

    @SuppressWarnings("rawtypes")
    private static final EmptyGenericLargeArray INSTANCE = new EmptyGenericLargeArray();

    private EmptyGenericLargeArray() {}

    @Override
    public int getId() {
        return ID_EMPTY;
    }

    @Override
    public void set(final long index, final E value) {}

    @Override
    public E get(final long index) {
        return null;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public IGenericLargeArray<E> slice(final long fromIndex, final long length) {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] asArray(final long fromIndex, final int length) {
        return (E[]) Objects.EMPTY_ARRAY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] asArrayCopy(final long fromIndex, final int length) {
        return (E[]) Objects.EMPTY_ARRAY;
    }

    @Override
    public void getGenerics(final long srcPos, final IGenericLargeArray<E> dest, final long destPos,
            final long length) {
        //noop
    }

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        return 0;
    }

    @Override
    public long getBufferLength() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptyGenericLargeArray<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public void clear() {}

}
