package de.invesdwin.util.lang.comparator;

import java.util.Comparator;

import de.invesdwin.norva.marker.ISerializableValueObject;

@FunctionalInterface
public interface ISerializableComparator<E> extends Comparator<E>, ISerializableValueObject {

}
