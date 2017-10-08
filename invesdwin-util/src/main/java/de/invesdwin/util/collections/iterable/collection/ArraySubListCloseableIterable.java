package de.invesdwin.util.collections.iterable.collection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
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
    public static final MethodHandle SUBLIST_OFFSET_GETTER;
    public static final MethodHandle SUBLIST_PARENT_GETTER;

    static {
        try {
            SUBLIST_CLASS = Reflections.classForName("java.util.ArrayList$SubList");
            final Field sublistOffsetField = Reflections.findField(SUBLIST_CLASS, "offset");
            Reflections.makeAccessibleFinal(sublistOffsetField);
            SUBLIST_OFFSET_GETTER = MethodHandles.lookup().unreflectGetter(sublistOffsetField);
            final Field sublistParentField = Reflections.findField(SUBLIST_CLASS, "parent");
            Reflections.makeAccessibleFinal(sublistParentField);
            SUBLIST_PARENT_GETTER = MethodHandles.lookup().unreflectGetter(sublistParentField);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
    @Override
    public ICloseableIterator<E> iterator() {
        if (cachedSize != arraySubList.size()) {
            cachedSize = arraySubList.size();
            try {
                final ArrayList<E> parent = (ArrayList<E>) SUBLIST_PARENT_GETTER.invoke(arraySubList);
                cachedArray = (E[]) ArrayListCloseableIterable.ARRAYLIST_ELEMENTDATA_GETTER.invokeExact(parent);
                cachedOffset = (Integer) SUBLIST_OFFSET_GETTER.invoke(arraySubList);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
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
