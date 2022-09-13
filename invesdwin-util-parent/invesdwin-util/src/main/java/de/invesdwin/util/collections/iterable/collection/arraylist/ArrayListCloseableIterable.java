package de.invesdwin.util.collections.iterable.collection.arraylist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.collection.ArrayCloseableIterator;
import de.invesdwin.util.collections.iterable.collection.ReverseArrayCloseableIterator;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.reflection.Reflections;

@SuppressWarnings("restriction")
@NotThreadSafe
public class ArrayListCloseableIterable<E> implements IArrayListCloseableIterable<E> {

    public static final long ARRAYLIST_ELEMENTDATA_FIELD_OFFSET;

    static {
        final Field arraylistElementDataField = Reflections.findField(ArrayList.class, "elementData");
        ARRAYLIST_ELEMENTDATA_FIELD_OFFSET = Reflections.getUnsafe().objectFieldOffset(arraylistElementDataField);
    }

    private final ArrayList<? extends E> arrayList;
    private int cachedSize = 0;
    @SuppressWarnings("unchecked")
    private E[] cachedArray = (E[]) Objects.EMPTY_ARRAY;

    public ArrayListCloseableIterable(final ArrayList<? extends E> arrayList) {
        this.arrayList = arrayList;
    }

    /**
     * Redo the reflection only when the array might have been replaced internally because of a growing size. This will
     * not recognize array replacements that did not come with a size change, so be careful. You can alternatively
     * override this method to always do a refresh.
     */
    @SuppressWarnings("unchecked")
    @Override
    public ICloseableIterator<E> iterator() {
        if (cachedSize != arrayList.size()) {
            cachedSize = arrayList.size();
            try {
                cachedArray = (E[]) Reflections.getUnsafe().getObject(arrayList, ARRAYLIST_ELEMENTDATA_FIELD_OFFSET);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayCloseableIterator<E>(cachedArray, 0, cachedSize) {
            @Override
            public List<E> toList() {
                return ArrayListCloseableIterable.this.toList();
            }

            @Override
            public List<E> toList(final List<E> list) {
                return ArrayListCloseableIterable.this.toList();
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public ICloseableIterator<E> iterator(final int lowIndex, final int highIndex) {
        if (cachedSize != arrayList.size()) {
            cachedSize = arrayList.size();
            try {
                cachedArray = (E[]) Reflections.getUnsafe().getObject(arrayList, ARRAYLIST_ELEMENTDATA_FIELD_OFFSET);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayCloseableIterator<E>(cachedArray, lowIndex, highIndex - lowIndex + 1) {
            @Override
            public List<E> toList() {
                return ArrayListCloseableIterable.this.toList(lowIndex, highIndex);
            }

            @Override
            public List<E> toList(final List<E> list) {
                return ArrayListCloseableIterable.this.toList(lowIndex, highIndex);
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public ICloseableIterator<E> reverseIterator() {
        if (cachedSize != arrayList.size()) {
            cachedSize = arrayList.size();
            try {
                cachedArray = (E[]) Reflections.getUnsafe().getObject(arrayList, ARRAYLIST_ELEMENTDATA_FIELD_OFFSET);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return new ReverseArrayCloseableIterator<E>(cachedArray, cachedSize - 1, cachedSize) {
            @Override
            public List<E> toList() {
                return Lists.reverse(ArrayListCloseableIterable.this.toList());
            }

            @Override
            public List<E> toList(final List<E> list) {
                return Lists.reverse(ArrayListCloseableIterable.this.toList());
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public ICloseableIterator<E> reverseIterator(final int highIndex, final int lowIndex) {
        if (cachedSize != arrayList.size()) {
            cachedSize = arrayList.size();
            try {
                cachedArray = (E[]) Reflections.getUnsafe().getObject(arrayList, ARRAYLIST_ELEMENTDATA_FIELD_OFFSET);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return new ReverseArrayCloseableIterator<E>(cachedArray, highIndex, highIndex - lowIndex + 1) {
            @Override
            public List<E> toList() {
                return Lists.reverse(ArrayListCloseableIterable.this.toList(lowIndex, highIndex));
            }

            @Override
            public List<E> toList(final List<E> list) {
                return Lists.reverse(ArrayListCloseableIterable.this.toList(lowIndex, highIndex));
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public void reset() {
        cachedSize = 0;
        cachedArray = (E[]) Objects.EMPTY_ARRAY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> toList() {
        return (List<E>) arrayList;
    }

    @SuppressWarnings("unchecked")
    public List<E> toList(final int lowIndex, final int highIndex) {
        return (List<E>) arrayList.subList(lowIndex, highIndex + 1);
    }

    @Override
    public List<E> toList(final List<E> list) {
        list.addAll(this.arrayList);
        return list;
    }

    @Override
    public ArrayList<? extends E> getArrayList() {
        return arrayList;
    }

}
