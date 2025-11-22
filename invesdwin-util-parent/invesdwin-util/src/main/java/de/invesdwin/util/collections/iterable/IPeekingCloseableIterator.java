package de.invesdwin.util.collections.iterable;

import com.google.common.collect.PeekingIterator;

public interface IPeekingCloseableIterator<E> extends ICloseableIterator<E>, PeekingIterator<E> {

}
