package de.invesdwin.util.collections.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.math.Integers;

@NotThreadSafe
public abstract class APrimitiveConcurrentMapBuilder<T extends IPrimitiveConcurrentMap, V> {
    protected PrimitiveConcurrentMapMode mode = PrimitiveConcurrentMapMode.DEFAULT;
    protected int concurrencyLevel = ILockCollectionFactory.DEFAULT_CONCURRENCY_LEVEL;
    protected int initialCapacity = ILockCollectionFactory.DEFAULT_INITIAL_SIZE;
    protected float loadFactor = ILockCollectionFactory.DEFAULT_LOAD_FACTOR;
    protected V defaultValue;

    protected APrimitiveConcurrentMapBuilder() {}

    public final APrimitiveConcurrentMapBuilder<T, V> setConcurrencyLevel(final int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public final APrimitiveConcurrentMapBuilder<T, V> setInitialCapacity(final int initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    public final APrimitiveConcurrentMapBuilder<T, V> setLoadFactor(final float loadFactor) {
        this.loadFactor = loadFactor;
        return this;
    }

    public float getLoadFactor() {
        return loadFactor;
    }

    public final APrimitiveConcurrentMapBuilder<T, V> setMode(final PrimitiveConcurrentMapMode mode) {
        this.mode = mode;
        return this;
    }

    public PrimitiveConcurrentMapMode getMode() {
        return mode;
    }

    public final APrimitiveConcurrentMapBuilder<T, V> setDefaultValue(final V defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public abstract T build();

    @Override
    public String toString() {
        return "PrimitiveMapBuilder{mode=%s, concurrencyLevel=%d, initialCapacity=%d, loadFactor=%s, def=%s}"
                .formatted(mode, concurrencyLevel, initialCapacity, loadFactor, defaultValue);
    }

    public static int newBucketCapacity(final int initialCapacity, final int concurrencyLevel) {
        return Integers.max(initialCapacity / concurrencyLevel, ILockCollectionFactory.DEFAULT_INITIAL_SIZE);
    }
}