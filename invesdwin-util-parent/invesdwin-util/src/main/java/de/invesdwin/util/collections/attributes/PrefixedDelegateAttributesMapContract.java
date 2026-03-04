package de.invesdwin.util.collections.attributes;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

@Immutable
public class PrefixedDelegateAttributesMapContract implements IAttributesMapContract {

    protected final IAttributesMapContract delegate;
    protected final String prefix;

    public PrefixedDelegateAttributesMapContract(final IAttributesMapContract delegate, final String prefix) {
        this.delegate = delegate;
        this.prefix = prefix;
    }

    @Override
    public <T> T getOrCreate(final String key, final Supplier<T> createSupplier) {
        return delegate.getOrCreate(prefix + key, createSupplier);
    }

    @Override
    public Object computeIfAbsent(final String key, final Function<? super String, ? extends Object> mappingFunction) {
        return delegate.computeIfAbsent(prefix + key, mappingFunction);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return delegate.containsKey(prefix + key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public Object get(final Object key) {
        return delegate.get(prefix + key);
    }

    @Override
    public Object put(final String key, final Object value) {
        return delegate.put(prefix + key, value);
    }

    @Override
    public Object remove(final Object key) {
        return delegate.remove(prefix + key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Object> m) {
        m.forEach((k, v) -> delegate.put(prefix + k, v));
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Object> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException();
    }

}
