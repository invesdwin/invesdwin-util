package de.invesdwin.util.collections.fast;

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
    public boolean contains(final Object value) {
        return false;
    }

    @Override
    public E[] asArray(final E[] emptyArray) {
        final E[] arrayCopy = array;
        if (arrayCopy.getClass().getComponentType().equals(emptyArray.getClass().getComponentType())) {
            return arrayCopy;
        } else {
            array = emptyArray;
            return emptyArray;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptyFastIterable<T> getInstance() {
        return INSTANCE;
    }

}
