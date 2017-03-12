package de.invesdwin.util.collections.iterable.list;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.ADelegateList;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.lang.Reflections;

@Immutable
public class ListCloseableIterable<E> implements ICloseableIterable<E> {

    private static final Field ARRAYLIST_ELEMENTDATA_FIELD;
    private static final Class<?> SUBLIST_CLASS;
    private static final Field SUBLIST_OFFSET_FIELD;
    private static final Field SUBLIST_PARENT_FIELD;

    static {
        ARRAYLIST_ELEMENTDATA_FIELD = Reflections.findField(ArrayList.class, "elementData");
        Reflections.makeAccessible(ARRAYLIST_ELEMENTDATA_FIELD);
        SUBLIST_CLASS = Reflections.classForName("java.util.ArrayList$SubList");
        SUBLIST_OFFSET_FIELD = Reflections.findField(SUBLIST_CLASS, "offset");
        Reflections.makeAccessible(SUBLIST_OFFSET_FIELD);
        SUBLIST_PARENT_FIELD = Reflections.findField(SUBLIST_CLASS, "parent");
        Reflections.makeAccessible(SUBLIST_PARENT_FIELD);
    }

    private final List<E> list;

    public ListCloseableIterable(final List<E> list) {
        if (list instanceof ADelegateList) {
            final ADelegateList<E> cList = (ADelegateList<E>) list;
            this.list = cList.getDelegate();
        } else {
            this.list = list;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ICloseableIterator<E> iterator() {
        if (list instanceof ArrayList) {
            final E[] array = (E[]) Reflections.getField(ARRAYLIST_ELEMENTDATA_FIELD, list);
            final int size = list.size();
            return new ArrayCloseableIterator<E>(array, 0, size);
        } else if (list.getClass().equals(SUBLIST_CLASS)) {
            final ArrayList<E> parent = (ArrayList<E>) Reflections.getField(SUBLIST_PARENT_FIELD, list);
            final int offset = (Integer) Reflections.getField(SUBLIST_OFFSET_FIELD, list);
            final int size = list.size();
            final E[] array = (E[]) Reflections.getField(ARRAYLIST_ELEMENTDATA_FIELD, parent);
            return new ArrayCloseableIterator<E>(array, offset, size);
        } else {
            return new ListCloseableIterator<E>(list);
        }
    }

}
