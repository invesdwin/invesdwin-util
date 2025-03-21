package de.invesdwin.util.collections.eviction;

import java.util.Map;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.ADelegateMap;

@NotThreadSafe
public abstract class AClearingDelegateMap<K, V> extends ADelegateMap<K, V> implements IEvictionMap<K, V> {

    private final boolean threadSafe;
    private int maximumSize;

    public AClearingDelegateMap(final boolean threadSafe, final int maximumSize) {
        super();
        this.threadSafe = threadSafe;
        this.maximumSize = maximumSize;
    }

    protected AClearingDelegateMap(final boolean threadSafe, final int maximumSize, final Map<K, V> delegate) {
        super(delegate);
        this.threadSafe = threadSafe;
        this.maximumSize = maximumSize;
    }

    @Override
    public final boolean isThreadSafe() {
        return threadSafe;
    }

    @Override
    public V put(final K key, final V value) {
        final V existing = super.put(key, value);
        if (existing == null) {
            maybeClear();
        }
        return existing;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        if (!m.isEmpty()) {
            super.putAll(m);
            maybeClear();
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        final V existing = super.putIfAbsent(key, value);
        if (existing == null) {
            maybeClear();
        }
        return existing;
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        final V existing = super.computeIfAbsent(key, mappingFunction);
        maybeClear();
        return existing;
    }

    protected void maybeClear() {
        final Map<K, V> delegate = getDelegate();
        if (threadSafe) {
            if (delegate.size() >= maximumSize) {
                synchronized (delegate) {
                    if (delegate.size() >= maximumSize) {
                        delegate.clear();
                    }
                }
            }
        } else {
            if (delegate.size() >= maximumSize) {
                delegate.clear();
            }
        }
    }

    @Override
    public int getMaximumSize() {
        return maximumSize;
    }

    @Override
    public void setMaximumSize(final int maximumSize) {
        this.maximumSize = maximumSize;
    }

    @Override
    public EvictionMode getEvictionMode() {
        if (threadSafe) {
            return EvictionMode.ClearConcurrent;
        } else {
            return EvictionMode.Clear;
        }
    }

}
