package de.invesdwin.util.collections.delegate;

import java.util.Iterator;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.IBufferingIterator;
import de.invesdwin.util.collections.iterable.skip.ASkippingIterator;

@NotThreadSafe
public abstract class ADelegateBufferingIterator<E> implements IBufferingIterator<E> {

    private final IBufferingIterator<E> delegate;

    @SuppressWarnings("unchecked")
    public ADelegateBufferingIterator() {
        this.delegate = (IBufferingIterator<E>) newDelegate();
    }

    protected ADelegateBufferingIterator(final IBufferingIterator<E> delegate) {
        this.delegate = delegate;
    }

    public IBufferingIterator<E> getDelegate() {
        return delegate;
    }

    protected abstract IBufferingIterator<? extends E> newDelegate();

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return getDelegate().iterator();
    }

    @Override
    public boolean add(final E e) {
        if (isAddAllowed(e)) {
            return getDelegate().add(e);
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(final E element) {
        return delegate.remove(element);
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public boolean addAll(final IBufferingIterator<E> iterable) {
        final ICloseableIterator<E> allowedElements = filterAllowedElements(iterable.iterator());
        return getDelegate().addAll(allowedElements);
    }

    @Override
    public boolean addAll(final Iterable<? extends E> iterable) {
        final ICloseableIterator<E> allowedElements = filterAllowedElements(
                WrapperCloseableIterable.maybeWrap(iterable).iterator());
        return getDelegate().addAll(allowedElements);
    }

    @Override
    public boolean addAll(final ICloseableIterable<? extends E> iterable) {
        final ICloseableIterator<E> allowedElements = filterAllowedElements(iterable.iterator());
        return getDelegate().addAll(allowedElements);
    }

    @Override
    public boolean addAll(final ICloseableIterator<? extends E> iterator) {
        final ICloseableIterator<E> allowedElements = filterAllowedElements(iterator);
        return getDelegate().addAll(allowedElements);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean addAll(final Iterator<? extends E> iterator) {
        final ICloseableIterator<E> allowedElements = filterAllowedElements(
                WrapperCloseableIterator.maybeWrap(iterator));
        return getDelegate().addAll(allowedElements);
    }

    @Override
    public boolean consume(final Iterable<? extends E> iterable) {
        return addAll(iterable);
    }

    @Override
    public boolean consume(final Iterator<? extends E> iterator) {
        return addAll(iterator);
    }

    @Override
    public boolean consume(final IBufferingIterator<E> iterator) {
        return addAll(iterator);
    }

    protected ICloseableIterator<E> filterAllowedElements(final ICloseableIterator<? extends E> c) {
        return new ASkippingIterator<E>(c) {

            @Override
            protected boolean skip(final E element) {
                return !isAddAllowed(element);
            }
        };
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    /**
     * Can be overwritten to add restrictions
     */
    public boolean isAddAllowed(final E e) {
        return true;
    }

    @Override
    public String toString() {
        return getDelegate().toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        return getDelegate().equals(obj);
    }

    @Override
    public int hashCode() {
        return getDelegate().hashCode();
    }

    public static <T> IBufferingIterator<T> maybeUnwrapToRoot(final IBufferingIterator<T> collection) {
        IBufferingIterator<T> cur = collection;
        while (cur instanceof ADelegateBufferingIterator) {
            final ADelegateBufferingIterator<T> c = (ADelegateBufferingIterator<T>) cur;
            cur = c.getDelegate();
        }
        return cur;
    }

    @Override
    public E getHead() {
        return getDelegate().getHead();
    }

    @Override
    public E getTail() {
        return getDelegate().getTail();
    }

    @Override
    public boolean prepend(final E element) {
        if (isAddAllowed(element)) {
            return prepend(element);
        } else {
            return false;
        }
    }

    @Override
    public void close() {
        getDelegate().close();
    }

    @Override
    public boolean hasNext() {
        return getDelegate().hasNext();
    }

    @Override
    public E next() {
        return getDelegate().next();
    }

    @Override
    public ICloseableIterable<E> snapshot() {
        return getDelegate().snapshot();
    }

}
