package de.invesdwin.util.collections.loadingcache;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ADelegateLoadingCache<K, V> implements ILoadingCache<K, V> {

    private final ILoadingCache<K, V> delegate;

    public ADelegateLoadingCache() {
        //lazy initialization is too expensive here
        this.delegate = newDelegate();
    }

    public ADelegateLoadingCache(final ILoadingCache<K, V> delegate) {
        this.delegate = delegate;
    }

    protected ILoadingCache<K, V> getDelegate() {
        return delegate;
    }

    protected abstract ILoadingCache<K, V> newDelegate();

    @Override
    public V get(final K key) {
        return getDelegate().get(key);
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public boolean containsKey(final K key) {
        return getDelegate().containsKey(key);
    }

    @Override
    public void remove(final K key) {
        getDelegate().remove(key);
    }

    @Override
    public void put(final K key, final V value) {
        getDelegate().put(key, value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return getDelegate().entrySet();
    }

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public Set<K> keySet() {
        return getDelegate().keySet();
    }

    @Override
    public Collection<V> values() {
        return getDelegate().values();
    }

    @Override
    public Map<K, V> asMap() {
        return getDelegate().asMap();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        getDelegate().increaseMaximumSize(maximumSize);
    }

    @Override
    public V getIfPresent(final K key) {
        return getDelegate().getIfPresent(key);
    }

    @Override
    public V computeIfAbsent(final K key, final Function<K, V> mappingFunction) {
        return getDelegate().computeIfAbsent(key, mappingFunction);
    }

    @Override
    public String toString() {
        return getDelegate().toString();
    }

}
