package de.invesdwin.util.collections.factory.pool.map;

import java.util.Map;

import de.invesdwin.util.streams.closeable.ISafeCloseable;

public interface ICloseableMap<K, V> extends Map<K, V>, ISafeCloseable {

}
