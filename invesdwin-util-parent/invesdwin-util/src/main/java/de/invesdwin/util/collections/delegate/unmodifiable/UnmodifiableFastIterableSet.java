package de.invesdwin.util.collections.delegate.unmodifiable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@NotThreadSafe
public class UnmodifiableFastIterableSet<E> extends UnmodifiableSet<E> implements IFastIterableSet<E> {

    public UnmodifiableFastIterableSet(final IFastIterableSet<E> delegate) {
        super(delegate);
    }

    @Override
    public IFastIterableSet<E> getDelegate() {
        return (IFastIterableSet<E>) super.getDelegate();
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
