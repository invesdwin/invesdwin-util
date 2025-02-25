package de.invesdwin.util.collections.array;

public interface IGenericArrayAccessor<E> {

    E get(int index);

    int size();

    boolean isEmpty();

}
