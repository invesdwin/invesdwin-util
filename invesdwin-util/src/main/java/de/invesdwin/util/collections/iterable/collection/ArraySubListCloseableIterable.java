package de.invesdwin.util.collections.iterable.collection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.ArrayUtils;

import de.invesdwin.util.collections.IFastToListProvider;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.lang.Reflections;

@NotThreadSafe
public class ArraySubListCloseableIterable<E> implements ICloseableIterable<E>, IFastToListProvider<E> {

    public static final Class<?> SUBLIST_CLASS;
    public static final Field SUBLIST_OFFSET_FIELD;
    public static final Field SUBLIST_PARENT_FIELD;

    static {
        SUBLIST_CLASS = Reflections.classForName("java.util.ArrayList$SubList");
        SUBLIST_OFFSET_FIELD = Reflections.findField(SUBLIST_CLASS, "offset");
        Reflections.makeAccessible(SUBLIST_OFFSET_FIELD);
        SUBLIST_PARENT_FIELD = Reflections.findField(SUBLIST_CLASS, "parent");
        Reflections.makeAccessible(SUBLIST_PARENT_FIELD);
    }

    private final List<? extends E> arraySubList;
    private int cachedSize = 0;
    @SuppressWarnings("unchecked")
    private E[] cachedArray = (E[]) ArrayUtils.EMPTY_OBJECT_ARRAY;
    private int cachedOffset = 0;

    public ArraySubListCloseableIterable(final List<? extends E> arraySubList) {
        if (!arraySubList.getClass().equals(SUBLIST_CLASS)) {
            throw new IllegalArgumentException(
                    "Not an instance of [" + SUBLIST_CLASS.getName() + "]: " + arraySubList.getClass().getName());
        }
        this.arraySubList = arraySubList;
    }

    /**
     * Redo the reflection only when the array might have been replaced internally because of a growing size. This will
     * not recognize array replacements that did not come with a size change, so be careful. You can alternatively
     * override this method to always do a refresh.
     */
    @SuppressWarnings("unchecked")
    @Override
    public ICloseableIterator<E> iterator() {
        if (cachedSize != arraySubList.size()) {
            cachedSize = arraySubList.size();
            final ArrayList<E> parent = (ArrayList<E>) Reflections.getField(SUBLIST_PARENT_FIELD, arraySubList);
            cachedArray = (E[]) Reflections.getField(ArrayListCloseableIterable.ARRAYLIST_ELEMENTDATA_FIELD, parent);
            cachedOffset = (Integer) Reflections.getField(SUBLIST_OFFSET_FIELD, arraySubList);
        }
        return new ArrayCloseableIterator<E>(cachedArray, cachedOffset, cachedSize) {
            @Override
            public List<E> toList() {
                return ArraySubListCloseableIterable.this.toList();
            }

            @Override
            public List<E> toList(final List<E> list) {
                return ArraySubListCloseableIterable.this.toList();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public synchronized void reset() {
        cachedSize = 0;
        cachedArray = (E[]) ArrayUtils.EMPTY_OBJECT_ARRAY;
        cachedOffset = 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> toList() {
        return (List<E>) arraySubList;
    }

    @Override
    public List<E> toList(final List<E> list) {
        list.addAll(this.arraySubList);
        return list;
    }

}
