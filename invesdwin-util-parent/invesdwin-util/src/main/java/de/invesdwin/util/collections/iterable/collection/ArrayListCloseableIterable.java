package de.invesdwin.util.collections.iterable.collection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.UnsafeAccess;
import org.apache.commons.lang3.ArrayUtils;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.list.IFastToListProvider;
import de.invesdwin.util.lang.reflection.Reflections;

@SuppressWarnings("restriction")
@NotThreadSafe
public class ArrayListCloseableIterable<E> implements ICloseableIterable<E>, IFastToListProvider<E> {

    public static final long ARRAYLIST_ELEMENTDATA_FIELD_OFFSET;

    static {
        final Field arraylistElementDataField = Reflections.findField(ArrayList.class, "elementData");
        ARRAYLIST_ELEMENTDATA_FIELD_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(arraylistElementDataField);
    }

    private final ArrayList<? extends E> arrayList;
    private int cachedSize = 0;
    @SuppressWarnings("unchecked")
    private E[] cachedArray = (E[]) ArrayUtils.EMPTY_OBJECT_ARRAY;

    public ArrayListCloseableIterable(final ArrayList<? extends E> arrayList) {
        this.arrayList = arrayList;
    }

    /**
     * Redo the reflection only when the array might have been replaced internally because of a growing size. This will
     * not recognize array replacements that did not come with a size change, so be careful. You can alternatively
     * override this method to always do a refresh.
     */
    @Override
    public ICloseableIterator<E> iterator() {
        if (cachedSize != arrayList.size()) {
            cachedSize = arrayList.size();
            try {
                cachedArray = (E[]) UnsafeAccess.UNSAFE.getObject(arrayList, ARRAYLIST_ELEMENTDATA_FIELD_OFFSET);
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
    public synchronized void reset() {
        cachedSize = 0;
        cachedArray = (E[]) ArrayUtils.EMPTY_OBJECT_ARRAY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> toList() {
        return (List<E>) arrayList;
    }

    @Override
    public List<E> toList(final List<E> list) {
        list.addAll(this.arrayList);
        return list;
    }

}
