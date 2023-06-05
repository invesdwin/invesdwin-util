package de.invesdwin.util.collections.iterable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.IBufferingIterator;

@NotThreadSafe
public class FlatteningIterable<E> implements ICloseableIterable<E> {

    private final ICloseableIterable<? extends Iterable<? extends E>> delegate;

    @SafeVarargs
    public FlatteningIterable(final Iterable<? extends E>... delegate) {
        this.delegate = WrapperCloseableIterable.maybeWrap(delegate);
    }

    public FlatteningIterable(final ICloseableIterable<? extends Iterable<? extends E>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        final ATransformingIterator<Iterable<? extends E>, Iterator<? extends E>> transformingDelegate = new ATransformingIterator<Iterable<? extends E>, Iterator<? extends E>>(
                delegate.iterator()) {
            @Override
            protected Iterator<? extends E> transform(final Iterable<? extends E> value) {
                return WrapperCloseableIterable.maybeWrap(value).iterator();
            }
        };
        return new FlatteningIterator<E>(transformingDelegate);
    }

    public static <T> ICloseableIterable<? extends T> maybeWrap(final List<ICloseableIterable<? extends T>> iterables) {
        if (iterables == null || iterables.isEmpty()) {
            return EmptyCloseableIterable.getInstance();
        } else if (iterables.size() == 1) {
            return iterables.get(0);
        } else {
            return new FlatteningIterable<T>(WrapperCloseableIterable.maybeWrap(iterables));
        }
    }

    public static <T> ICloseableIterable<? extends T> maybeWrap(
            final IBufferingIterator<ICloseableIterable<? extends T>> iterables) {
        if (iterables == null || iterables.isEmpty()) {
            return EmptyCloseableIterable.getInstance();
        } else if (iterables.size() == 1) {
            return iterables.next();
        } else {
            return new FlatteningIterable<T>(iterables.asConsumingIterable());
        }
    }

    public static <T> ICloseableIterable<? extends T> maybeWrap(
            final Collection<ICloseableIterable<? extends T>> iterables) {
        if (iterables == null || iterables.isEmpty()) {
            return EmptyCloseableIterable.getInstance();
        } else if (iterables.size() == 1) {
            return iterables.iterator().next();
        } else {
            return new FlatteningIterable<T>(WrapperCloseableIterable.maybeWrap(iterables));
        }
    }

}
