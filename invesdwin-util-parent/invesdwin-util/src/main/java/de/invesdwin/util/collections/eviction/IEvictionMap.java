package de.invesdwin.util.collections.eviction;

import java.util.Map;

public interface IEvictionMap<K, V> extends Map<K, V> {

    EvictionMode getEvictionMode();

    void setMaximumSize(int maximumSize);

    int getMaximumSize();

    boolean isThreadSafe();

}
