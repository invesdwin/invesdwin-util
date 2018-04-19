package de.invesdwin.util.collections.concurrent;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.collections.iterable.ICloseableIterable;

public interface IFastIterable<E> extends ICloseableIterable<E>, ISerializableValueObject {

    boolean isEmpty();

    E[] asArray(Class<E> type);

}
