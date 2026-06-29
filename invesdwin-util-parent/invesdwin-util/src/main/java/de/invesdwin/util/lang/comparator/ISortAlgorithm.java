package de.invesdwin.util.lang.comparator;

import java.util.Comparator;
import java.util.List;

import de.invesdwin.norva.marker.ISerializableValueObject;

public interface ISortAlgorithm extends ISerializableValueObject {

    <T> void sort(List<? extends T> list, Comparator<? super T> comparator);

    <T extends Comparable<? super T>> void sort(List<? extends T> list);

}
