package de.invesdwin.util.collections.iterable;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.SortedList;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.lang.ADelegateComparator;

@NotThreadSafe
public abstract class ASortedFeedsIterator<E> implements ICloseableIterator<E> {

    private final ADelegateComparator<PeekingCloseableIterator<E>> comparator = new ADelegateComparator<PeekingCloseableIterator<E>>() {
        @Override
        protected Comparable<?> getCompareCriteria(final PeekingCloseableIterator<E> e) {
            return ASortedFeedsIterator.this.getCompareCriteria(e.peek());
        }
    };
    private final SortedList<PeekingCloseableIterator<? extends E>> peekingFeeds = new SortedList<>(comparator);

    public ASortedFeedsIterator(final Iterable<? extends ICloseableIterator<? extends E>> feeds) {
        for (final ICloseableIterator<? extends E> feed : feeds) {
            final PeekingCloseableIterator<? extends E> peekingFeed = new PeekingCloseableIterator<>(feed);
            peekingFeed.peek();
            //only add feeds that are not empty
            if (peekingFeed.hasNext()) {
                peekingFeeds.add(peekingFeed);
            }
        }
    }

    protected abstract Comparable<?> getCompareCriteria(E e);

    @Override
    public boolean hasNext() {
        return !peekingFeeds.isEmpty();
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new FastNoSuchElementException("ASortedFeedsIterator hasNext() returned false");
        }
        final PeekingCloseableIterator<? extends E> peekingFeed = peekingFeeds.remove(0);
        try {
            final E next = peekingFeed.next();
            if (peekingFeed.hasNext()) {
                peekingFeeds.add(peekingFeed); //add into the list with an updated sort order
            }
            return next;
        } catch (final NoSuchElementException e) {
            //feed is empty
            return next();
        }
    }

    @Override
    public void close() {
        for (final PeekingCloseableIterator<? extends E> feed : peekingFeeds) {
            feed.close();
        }
        peekingFeeds.clear();
    }

}
