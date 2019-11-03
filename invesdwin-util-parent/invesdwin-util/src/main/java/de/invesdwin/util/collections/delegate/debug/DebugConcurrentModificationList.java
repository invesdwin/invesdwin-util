package de.invesdwin.util.collections.delegate.debug;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.DelegateList;
import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.lang.description.TextDescription;

@NotThreadSafe
public class DebugConcurrentModificationList<E> extends DelegateList<E> {

    private final String id;
    private final AtomicLong openReaders = new AtomicLong();

    public DebugConcurrentModificationList(final List<E> delegate, final String id) {
        super(delegate);
        this.id = id;
    }

    @Override
    public boolean add(final E e) {
        assertNoOpenReaders();
        return super.add(e);
    }

    private void assertNoOpenReaders() {
        final long openReadersCount = openReaders.get();
        if (openReadersCount > 0) {
            throw new IllegalStateException(id + ": " + openReadersCount + " > 0");
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        assertNoOpenReaders();
        return super.addAll(c);
    }

    @Override
    public boolean remove(final Object o) {
        assertNoOpenReaders();
        return super.remove(o);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        assertNoOpenReaders();
        return super.removeAll(c);
    }

    @Override
    public E remove(final int index) {
        assertNoOpenReaders();
        return super.remove(index);
    }

    @Override
    public boolean removeIf(final Predicate<? super E> filter) {
        assertNoOpenReaders();
        return super.removeIf(filter);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        assertNoOpenReaders();
        return super.retainAll(c);
    }

    @Override
    public void add(final int index, final E element) {
        assertNoOpenReaders();
        super.add(index, element);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        assertNoOpenReaders();
        return super.addAll(index, c);
    }

    @Override
    public E set(final int index, final E element) {
        assertNoOpenReaders();
        return super.set(index, element);
    }

    @Override
    public void replaceAll(final UnaryOperator<E> operator) {
        assertNoOpenReaders();
        super.replaceAll(operator);
    }

    @Override
    public void clear() {
        assertNoOpenReaders();
        super.clear();
    }

    @Override
    public ICloseableIterator<E> iterator() {
        openReaders.incrementAndGet();
        return new ACloseableIterator<E>(
                new TextDescription("%s: iterator()", DebugConcurrentModificationList.class.getSimpleName())) {

            private final Iterator<E> delegate = DebugConcurrentModificationList.super.iterator();

            @Override
            protected boolean innerHasNext() {
                return delegate.hasNext();
            }

            @Override
            protected E innerNext() {
                return delegate.next();
            }

            @Override
            public void close() {
                super.close();
                openReaders.decrementAndGet();
            }
        };
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return new CloseableDelegateList<E>(
                new TextDescription("%s: subList()", DebugConcurrentModificationList.class.getSimpleName()),
                super.subList(fromIndex, toIndex), openReaders);
    }

}
