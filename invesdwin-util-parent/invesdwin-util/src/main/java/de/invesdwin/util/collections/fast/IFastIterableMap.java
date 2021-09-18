package de.invesdwin.util.collections.fast;

import java.util.Map;

import de.invesdwin.norva.marker.ISerializableValueObject;

public interface IFastIterableMap<K, V> extends Map<K, V>, ISerializableValueObject {

    V[] asValueArray(V[] emptyArray);

    K[] asKeyArray(K[] emptyArray);

    Entry<K, V>[] asEntryArray();

}
