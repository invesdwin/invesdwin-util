package de.invesdwin.util.collections.fast;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.collections.iterable.ICloseableIterable;

public interface IFastIterable<E> extends ICloseableIterable<E>, ISerializableValueObject {

    boolean isEmpty();

    boolean contains(Object value);

    E[] asArray(E[] emptyArray);

}
