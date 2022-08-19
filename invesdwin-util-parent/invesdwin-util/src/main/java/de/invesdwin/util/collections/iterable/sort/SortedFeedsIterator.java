package de.invesdwin.util.collections.iterable.sort;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.PeekingCloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.lang.comparator.IComparator;

@NotThreadSafe
public class SortedFeedsIterator<E> implements ICloseableIterator<E> {

    private final List<PeekingCloseableIterator<? extends E>> peekingFeeds;
    private final IComparator<? super E> comparator;

    @SuppressWarnings("unchecked")
    public SortedFeedsIterator(final IComparator<? super E> comparator,
            final ICloseableIterator<? extends E>... feeds) {
        this(comparator, Arrays.asList(feeds));
    }

    public SortedFeedsIterator(final IComparator<? super E> comparator,
            final Iterable<? extends ICloseableIterator<? extends E>> feeds) {
        this.comparator = comparator;
        this.peekingFeeds = new ArrayList<>();
        for (final ICloseableIterator<? extends E> feed : feeds) {
            final PeekingCloseableIterator<? extends E> peekingFeed = new PeekingCloseableIterator<>(feed);
            //only add feeds that are not empty
            if (peekingFeed.hasNext()) {
                peekingFeeds.add(peekingFeed);
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !peekingFeeds.isEmpty();
    }

    @Override
    public E next() {
        E max = null;
        PeekingCloseableIterator<? extends E> maxFeed = null;
        int maxFeedIndex = -1;
        for (int i = 0; i < peekingFeeds.size(); i++) {
            final PeekingCloseableIterator<? extends E> peekingFeed = peekingFeeds.get(i);
            try {
                final E peek = peekingFeed.peek();
                if (max == null || comparator.compareTyped(max, peek) > 0) {
                    max = peek;
                    maxFeed = peekingFeed;
                    maxFeedIndex = i;
                }
            } catch (final NoSuchElementException e) {
                peekingFeeds.remove(i);
                i--;
            }
        }
        if (max == null) {
            throw FastNoSuchElementException.getInstance("ASortedFeedsIterator reached end");
        }
        maxFeed.next();
        if (!maxFeed.hasNext()) {
            peekingFeeds.remove(maxFeedIndex);
        }
        return max;
    }

    @Override
    public void close() {
        for (int i = 0; i < peekingFeeds.size(); i++) {
            peekingFeeds.get(i).close();
        }
        peekingFeeds.clear();
    }

}
