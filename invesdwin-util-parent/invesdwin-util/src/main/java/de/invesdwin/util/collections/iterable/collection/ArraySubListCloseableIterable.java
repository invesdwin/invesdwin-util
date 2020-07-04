package de.invesdwin.util.collections.iterable.collection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.ArrayUtils;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.IFastToListProvider;
import de.invesdwin.util.lang.reflection.Reflections;

@NotThreadSafe
public class ArraySubListCloseableIterable<E> implements ICloseableIterable<E>, IFastToListProvider<E> {

    public static final Class<?> SUBLIST_CLASS;
    public static final MethodHandle SUBLIST_SIZE_GETTER;
    public static final MethodHandle SUBLIST_OFFSET_GETTER;
    public static final MethodHandle SUBLIST_PARENT_GETTER;

    static {
        try {
            SUBLIST_CLASS = Reflections.classForName("java.util.ArrayList$SubList");
            final Field sublistSizeField = Reflections.findField(SUBLIST_CLASS, "size");
            Reflections.makeAccessible(sublistSizeField);
            SUBLIST_SIZE_GETTER = MethodHandles.lookup().unreflectGetter(sublistSizeField);
            final Field sublistOffsetField = Reflections.findField(SUBLIST_CLASS, "offset");
            Reflections.makeAccessible(sublistOffsetField);
            SUBLIST_OFFSET_GETTER = MethodHandles.lookup().unreflectGetter(sublistOffsetField);
            final Field sublistRootField = Reflections.findField(SUBLIST_CLASS, "root");
            if (sublistRootField != null) {
                //java 11, we can directly access the root
                Reflections.makeAccessible(sublistRootField);
                SUBLIST_PARENT_GETTER = MethodHandles.lookup().unreflectGetter(sublistRootField);
            } else {
                //before java 11
                final Field sublistParentField = Reflections.findField(SUBLIST_CLASS, "parent");
                Reflections.makeAccessible(sublistParentField);
                SUBLIST_PARENT_GETTER = MethodHandles.lookup().unreflectGetter(sublistParentField);
            }
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
    @SuppressWarnings("unchecked")
    @Override
    public ICloseableIterator<E> iterator() {
        try {
            final int size = (int) SUBLIST_SIZE_GETTER.invoke(arraySubList);
            if (cachedSize != size) {
                cachedSize = size;
                List<E> parent = (List<E>) arraySubList;
                while (parent.getClass().equals(SUBLIST_CLASS)) {
                    parent = (List<E>) SUBLIST_PARENT_GETTER.invoke(parent);
                }
                final ArrayList<E> arrayListParent = (ArrayList<E>) parent;
                cachedArray = (E[]) ArrayListCloseableIterable.ARRAYLIST_ELEMENTDATA_GETTER
                        .invokeExact(arrayListParent);
                cachedOffset = (Integer) SUBLIST_OFFSET_GETTER.invoke(arraySubList);
            }
        } catch (final Throwable e) {
            throw new RuntimeException(e);
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
