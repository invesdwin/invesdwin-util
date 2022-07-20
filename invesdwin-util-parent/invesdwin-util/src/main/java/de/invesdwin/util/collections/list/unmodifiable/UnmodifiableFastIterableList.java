package de.invesdwin.util.collections.list.unmodifiable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@NotThreadSafe
public class UnmodifiableFastIterableList<E> extends UnmodifiableList<E> implements IFastIterableList<E> {

    public UnmodifiableFastIterableList(final IFastIterableList<E> delegate) {
        super(delegate);
    }

    @Override
    public IFastIterableList<E> getDelegate() {
        return (IFastIterableList<E>) super.getDelegate();
    }

    @Override
    public E[] asArray(final E[] emptyArray) {
        return getDelegate().asArray(emptyArray);
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return getDelegate().iterator();
    }

}
