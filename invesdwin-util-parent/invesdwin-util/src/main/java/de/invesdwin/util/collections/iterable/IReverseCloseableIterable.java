package de.invesdwin.util.collections.iterable;

public interface IReverseCloseableIterable<E> extends ICloseableIterable<E> {

    ICloseableIterator<E> reverseIterator();

    default ICloseableIterable<E> reverseIterable() {
        return new ICloseableIterable<E>() {
            @Override
            public ICloseableIterator<E> iterator() {
                return reverseIterator();
            }
        };
    }

}
