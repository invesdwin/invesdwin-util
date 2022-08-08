package de.invesdwin.util.collections.iterable.collection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.IFastToListProvider;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.reflection.Reflections;

@SuppressWarnings("restriction")
@NotThreadSafe
public class ArraySubListCloseableIterable<E> implements ICloseableIterable<E>, IFastToListProvider<E> {

    public static final Class<?> SUBLIST_CLASS;
    public static final long SUBLIST_SIZE_FIELD_OFFSET;
    public static final long SUBLIST_OFFSET_FIELD_OFFSET;
    public static final long SUBLIST_PARENT_FIELD_OFFSET;

    static {
        SUBLIST_CLASS = Reflections.classForName("java.util.ArrayList$SubList");
        final Field sublistSizeField = Reflections.findField(SUBLIST_CLASS, "size");
        SUBLIST_SIZE_FIELD_OFFSET = Reflections.getUnsafe().objectFieldOffset(sublistSizeField);
        final Field sublistOffsetField = Reflections.findField(SUBLIST_CLASS, "offset");
        SUBLIST_OFFSET_FIELD_OFFSET = Reflections.getUnsafe().objectFieldOffset(sublistOffsetField);
        final Field sublistRootField = Reflections.findField(SUBLIST_CLASS, "root");
        if (sublistRootField != null) {
            //java 11, we can directly access the root
            SUBLIST_PARENT_FIELD_OFFSET = Reflections.getUnsafe().objectFieldOffset(sublistRootField);
        } else {
            //before java 11
            final Field sublistParentField = Reflections.findField(SUBLIST_CLASS, "parent");
            SUBLIST_PARENT_FIELD_OFFSET = Reflections.getUnsafe().objectFieldOffset(sublistParentField);
        }
    }

    private final List<? extends E> arraySubList;
    private int cachedSize = 0;
    @SuppressWarnings("unchecked")
    private E[] cachedArray = (E[]) Objects.EMPTY_ARRAY;
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
            final int size = Reflections.getUnsafe().getInt(arraySubList, SUBLIST_SIZE_FIELD_OFFSET);
            if (cachedSize != size) {
                cachedSize = size;
                List<E> parent = (List<E>) arraySubList;
                while (parent.getClass().equals(SUBLIST_CLASS)) {
                    parent = (List<E>) Reflections.getUnsafe().getObject(parent, SUBLIST_PARENT_FIELD_OFFSET);
                }
                final ArrayList<E> arrayListParent = (ArrayList<E>) parent;
                cachedArray = (E[]) Reflections.getUnsafe()
                        .getObject(arrayListParent, ArrayListCloseableIterable.ARRAYLIST_ELEMENTDATA_FIELD_OFFSET);
                cachedOffset = Reflections.getUnsafe().getInt(arraySubList, SUBLIST_OFFSET_FIELD_OFFSET);
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
        cachedArray = (E[]) Objects.EMPTY_ARRAY;
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
