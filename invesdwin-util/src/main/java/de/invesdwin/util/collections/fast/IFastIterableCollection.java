package de.invesdwin.util.collections.fast;

public interface IFastIterableCollection<E> extends IFastIterable<E> {

    boolean add(E value);

    boolean remove(E value);

    void clear();

}
