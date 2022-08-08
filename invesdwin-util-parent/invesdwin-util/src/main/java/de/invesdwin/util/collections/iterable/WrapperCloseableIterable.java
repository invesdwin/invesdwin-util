package de.invesdwin.util.collections.iterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.delegate.ADelegateList;
import de.invesdwin.util.collections.iterable.collection.ArrayListCloseableIterable;
import de.invesdwin.util.collections.iterable.collection.ArraySubListCloseableIterable;
import de.invesdwin.util.collections.iterable.collection.CollectionCloseableIterable;
import de.invesdwin.util.collections.iterable.collection.ListCloseableIterable;

@NotThreadSafe
public final class WrapperCloseableIterable<E> implements ICloseableIterable<E> {

    private final Iterable<? extends E> delegate;

    private WrapperCloseableIterable(final Iterable<? extends E> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ICloseableIterator<E> iterator() {
        return WrapperCloseableIterator.maybeWrap(delegate.iterator());
    }

    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> maybeUnwrap(final ICloseableIterable<? extends T> iterator) {
        if (iterator instanceof WrapperCloseableIterable) {
            final WrapperCloseableIterable<T> it = (WrapperCloseableIterable<T>) iterator;
            return (Iterable<T>) it.delegate;
        } else {
            return (Iterable<T>) iterator;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ICloseableIterable<T> maybeWrap(final T... iterable) {
        return maybeWrap(new ArrayList<T>(Arrays.asList(iterable)));
    }

    @SuppressWarnings("unchecked")
    public static <T> ICloseableIterable<T> maybeWrap(final Iterable<? extends T> iterable) {
        if (iterable instanceof ICloseableIterable) {
            return (ICloseableIterable<T>) iterable;
        } else if (iterable instanceof Collection) {
            return maybeWrap((Collection<T>) iterable);
        } else {
            return new WrapperCloseableIterable<T>(iterable);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ICloseableIterable<T> maybeWrap(final List<? extends T> iterable) {
        if (iterable instanceof ICloseableIterable) {
            return (ICloseableIterable<T>) iterable;
        }
        final List<? extends T> unwrappedList = ADelegateList.maybeUnwrapToRoot(iterable);
        if (unwrappedList instanceof ICloseableIterable) {
            return (ICloseableIterable<T>) iterable;
        } else if (unwrappedList instanceof ArrayList) {
            return new ArrayListCloseableIterable<T>((ArrayList<T>) unwrappedList);
        } else if (unwrappedList.getClass().equals(ArraySubListCloseableIterable.SUBLIST_CLASS)) {
            return new ArraySubListCloseableIterable<T>(unwrappedList);
        } else {
            return new ListCloseableIterable<T>(unwrappedList);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ICloseableIterable<T> maybeWrap(final Collection<? extends T> iterable) {
        if (iterable instanceof List) {
            return maybeWrap((List<T>) iterable);
        } else if (iterable instanceof ICloseableIterable) {
            return (ICloseableIterable<T>) iterable;
        } else {
            return new CollectionCloseableIterable<T>(iterable);
        }
    }

}
