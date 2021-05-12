package de.invesdwin.util.collections.fast;

import java.lang.reflect.Array;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.lang.Objects;

@Immutable
public final class EmptyFastIterable<E> implements IFastIterable<E> {

    @SuppressWarnings("rawtypes")
    private static final EmptyFastIterable INSTANCE = new EmptyFastIterable<>();

    @SuppressWarnings("unchecked")
    private E[] array = (E[]) Objects.EMPTY_ARRAY;

    private EmptyFastIterable() {
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(final E value) {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] asArray(final Class<E> type) {
        final E[] arrayCopy = array;
        if (arrayCopy.getClass().getComponentType().equals(type)) {
            return arrayCopy;
        } else {
            final E[] newArray = (E[]) Array.newInstance(type, 0);
            array = newArray;
            return newArray;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptyFastIterable<T> getInstance() {
        return INSTANCE;
    }

}
