package de.invesdwin.util.collections.delegate.unmodifiable;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.fast.IFastIterableList;

@NotThreadSafe
public class ReverseUnmodifiableFastIterableList<E> extends ReverseUnmodifiableList<E> implements IFastIterableList<E> {

    private E[] prevAsArray = null;
    private E[] prevReverseAsArray = null;

    public ReverseUnmodifiableFastIterableList(final List<E> delegate) {
        super(delegate);
    }

    @Override
    public IFastIterableList<E> getDelegate() {
        return (IFastIterableList<E>) super.getDelegate();
    }

    @Override
    public E[] asArray(final E[] emptyArray) {
        final E[] asArray = getDelegate().asArray(emptyArray);
        if (prevAsArray != asArray) {
            if (asArray.length <= 1) {
                prevReverseAsArray = asArray;
                prevAsArray = asArray;
            } else {
                final E[] reverseAsArray = asArray.clone();
                Arrays.reverse(reverseAsArray);
                prevReverseAsArray = reverseAsArray;
                prevAsArray = asArray;
            }
        }
        return prevReverseAsArray;
    }

}
