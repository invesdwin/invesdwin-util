package de.invesdwin.util.collections.delegate.unmodifiable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class UnmodifiableMap<K, V> extends AUnmodifiableMap<K, V> {

    private final Map<K, V> delegate;
    private final Set<K> keySet = newKeySet();
    private final Collection<V> values = newValues();
    private final Set<Entry<K, V>> entrySet = newEntrySet();

    public UnmodifiableMap(final Map<K, V> delegate) {
        this.delegate = delegate;
    }

    protected Set<K> newKeySet() {
        return new UnmodifiableSet<K>(null) {
            @Override
            public Set<K> getDelegate() {
                return UnmodifiableMap.this.getDelegate().keySet();
            }
        };
    }

    protected Collection<V> newValues() {
        return new UnmodifiableCollection<V>(null) {
            @Override
            public Collection<V> getDelegate() {
                return UnmodifiableMap.this.getDelegate().values();
            }
        };
    }

    protected Set<Entry<K, V>> newEntrySet() {
        return new UnmodifiableSet<Entry<K, V>>(null) {
            @Override
            public Set<Entry<K, V>> getDelegate() {
                return UnmodifiableMap.this.getDelegate().entrySet();
            }
        };
    }

    public Map<K, V> getDelegate() {
        return delegate;
    }

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        return getDelegate().equals(o);
    }

    @Override
    public int hashCode() {
        return getDelegate().hashCode();
    }

    @Override
    public String toString() {
        return getDelegate().toString();
    }

    @Override
    public boolean containsKey(final Object key) {
        return getDelegate().containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return getDelegate().containsValue(value);
    }

    @Override
    public V get(final Object key) {
        return getDelegate().get(key);
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public Collection<V> values() {
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }

}
