package de.invesdwin.util.collections.primitive;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class APrimitiveConcurrentMapBuilder<T extends IPrimitiveConcurrentKeyMap, V> {
    protected PrimitiveConcurrentMapMode mapMode = PrimitiveConcurrentMapMode.DEFAULT;
    protected int buckets = 8;
    protected int initialCapacity = 100_000;
    protected float loadFactor = 0.8f;
    protected V defaultValue;

    protected APrimitiveConcurrentMapBuilder() {}

    public final APrimitiveConcurrentMapBuilder<T, V> setBuckets(final int buckets) {
        this.buckets = buckets;
        return this;
    }

    public final APrimitiveConcurrentMapBuilder<T, V> setInitialCapacity(final int initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    public final APrimitiveConcurrentMapBuilder<T, V> setLoadFactor(final float loadFactor) {
        this.loadFactor = loadFactor;
        return this;
    }

    public final APrimitiveConcurrentMapBuilder<T, V> setMode(final PrimitiveConcurrentMapMode mapMode) {
        this.mapMode = mapMode;
        return this;
    }

    public final APrimitiveConcurrentMapBuilder<T, V> setDefaultValue(final V defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public abstract T build();

    

    @Override
    public String toString() {
        return "PrimitiveMapBuilder{mapMode=%s, buckets=%d, initialCapacity=%d, loadFactor=%s, def=%s}"
                .formatted(mapMode, buckets, initialCapacity, loadFactor, defaultValue);
    }
}