package de.invesdwin.util.collections.iterable.collection.fast;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;

@NotThreadSafe
public class FastToListFlatteningIterable<E> implements IFastToListCloseableIterable<E> {

    private final IFastToListCloseableIterable<? extends IFastToListCloseableIterable<? extends E>> delegate;

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public FastToListFlatteningIterable(final Iterable<? extends E>... delegate) {
        this.delegate = (IFastToListCloseableIterable<? extends IFastToListCloseableIterable<? extends E>>) WrapperCloseableIterable
                .maybeWrap(delegate);
    }

    @SuppressWarnings("unchecked")
    public FastToListFlatteningIterable(final IFastToListCloseableIterable<? extends Iterable<? extends E>> delegate) {
        this.delegate = (IFastToListCloseableIterable<? extends IFastToListCloseableIterable<? extends E>>) delegate;
    }

    @Override
    public IFastToListCloseableIterator<E> iterator() {
        final AFastToListTransformingIterable<Iterable<? extends E>, IFastToListCloseableIterator<E>> transformingDelegate = new AFastToListTransformingIterable<Iterable<? extends E>, IFastToListCloseableIterator<E>>(
                delegate) {
            @SuppressWarnings("unchecked")
            @Override
            protected IFastToListCloseableIterator<E> transform(final Iterable<? extends E> value) {
                return (IFastToListCloseableIterator<E>) WrapperCloseableIterable.maybeWrap(value).iterator();
            }
        };
        return new FastToListFlatteningIterator<E>(transformingDelegate.iterator());
    }

    @Override
    public List<E> toList() {
        return iterator().toList();
    }

    @Override
    public List<E> toList(final List<E> list) {
        iterator().toList(list);
        return list;
    }

    @Override
    public E getHead() {
        return iterator().getHead();
    }

    @Override
    public E getTail() {
        return iterator().getTail();
    }

}
