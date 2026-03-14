package de.invesdwin.util.collections.eviction;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class JavaLeastRecentlyUsedMap<K, V> extends java.util.LinkedHashMap<K, V> implements IEvictionMap<K, V> {

    private int maximumSize;

    public JavaLeastRecentlyUsedMap(final int maximumSize) {
        this.maximumSize = maximumSize;
    }

    @Override
    public EvictionMode getEvictionMode() {
        return EvictionMode.LeastRecentlyUsed;
    }

    @Override
    public void setMaximumSize(final int maximumSize) {
        this.maximumSize = maximumSize;
    }

    @Override
    public int getMaximumSize() {
        return maximumSize;
    }

    @Override
    protected boolean removeEldestEntry(final java.util.Map.Entry<K, V> eldest) {
        return size() > maximumSize;
    }

    @Override
    public boolean isThreadSafe() {
        return false;
    }

}
