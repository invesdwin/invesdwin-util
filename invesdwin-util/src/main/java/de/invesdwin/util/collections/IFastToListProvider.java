package de.invesdwin.util.collections;

import java.util.List;

public interface IFastToListProvider<E> {

    List<E> toList();

    List<E> toList(List<E> list);

}
