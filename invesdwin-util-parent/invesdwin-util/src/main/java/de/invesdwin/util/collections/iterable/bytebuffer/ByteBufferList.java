package de.invesdwin.util.collections.iterable.bytebuffer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class ByteBufferList<E> implements List<E> {

    private final IByteBuffer buffer;
    private final ISerde<E> serde;
    private final int fixedLength;

    public ByteBufferList(final IByteBuffer buffer, final ISerde<E> serde, final int fixedLength) {
        this.buffer = buffer;
        this.serde = serde;
        this.fixedLength = fixedLength;
        Assertions.checkTrue(fixedLength > 0);
    }

    @Override
    public int size() {
        return buffer.capacity() / fixedLength;
    }

    @Override
    public boolean isEmpty() {
        return buffer.capacity() < fixedLength;
    }

    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return new ByteBufferCloseableIterator<>(buffer, serde, fixedLength);
    }

    @Override
    public Object[] toArray() {
        return Lists.toListWithoutHasNext(iterator()).toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return Lists.toListWithoutHasNext(iterator()).toArray(a);
    }

    @Override
    public boolean add(final E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(final int index) {
        return serde.fromBuffer(buffer.newSlice(index * fixedLength, fixedLength));
    }

    @Override
    public E set(final int index, final E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final int index, final E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return new ByteBufferList<>(buffer.newSlice(fromIndex * fixedLength, toIndex * fixedLength), serde,
                fixedLength);
    }

}
