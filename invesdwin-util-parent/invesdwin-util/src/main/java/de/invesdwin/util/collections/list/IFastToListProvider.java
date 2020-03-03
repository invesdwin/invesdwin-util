package de.invesdwin.util.collections.list;

import java.util.List;

public interface IFastToListProvider<E> {

    List<E> toList();

    List<E> toList(List<E> list);

}
