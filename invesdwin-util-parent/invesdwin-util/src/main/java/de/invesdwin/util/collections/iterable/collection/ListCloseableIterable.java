package de.invesdwin.util.collections.iterable.collection;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.Lists;

@Immutable
public class ListCloseableIterable<E> implements IListCloseableIterable<E> {

    private final List<? extends E> list;

    public ListCloseableIterable(final List<? extends E> list) {
        this.list = list;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        /*
         * try to save us the effort for checking the mod count and bounds all the time by trying to directly go to the
         * underlying array
         */
        return new ListCloseableIterator<E>(list);
    }

    @Override
    public ICloseableIterator<E> iterator(final int lowIndex, final int highIndex) {
        return new ListCloseableIterator<E>(list, lowIndex, highIndex - lowIndex + 1) {
            @Override
            public List<E> toList() {
                return ListCloseableIterable.this.toList(lowIndex, highIndex);
            }

            @Override
            public List<E> toList(final List<E> list) {
                return ListCloseableIterable.this.toList(lowIndex, highIndex);
            }
        };
    }

    @Override
    public ICloseableIterator<E> reverseIterator() {
        return new ReverseListCloseableIterator<E>(list);
    }

    @Override
    public ICloseableIterator<E> reverseIterator(final int highIndex, final int lowIndex) {
        return new ReverseListCloseableIterator<E>(list, highIndex, highIndex - lowIndex + 1) {
            @Override
            public List<E> toList() {
                return Lists.reverse(ListCloseableIterable.this.toList(lowIndex, highIndex));
            }

            @Override
            public List<E> toList(final List<E> list) {
                return Lists.reverse(ListCloseableIterable.this.toList(lowIndex, highIndex));
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> toList() {
        return (List<E>) list;
    }

    @Override
    public List<E> toList(final List<E> list) {
        list.addAll(this.list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public List<E> toList(final int lowIndex, final int highIndex) {
        return (List<E>) list.subList(lowIndex, highIndex + 1);
    }

    @Override
    public List<? extends E> getList() {
        return list;
    }

}
