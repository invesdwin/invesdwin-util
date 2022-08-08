package de.invesdwin.util.collections.attributes;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Collections;

@Immutable
public final class EmptyAttributesMap implements IAttributesMap {

    public static final EmptyAttributesMap INSTANCE = new EmptyAttributesMap();

    private EmptyAttributesMap() {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean containsKey(final Object key) {
        return false;
    }

    @Override
    public boolean containsValue(final Object value) {
        return false;
    }

    @Override
    public Object get(final Object key) {
        return null;
    }

    @Override
    public Object put(final String key, final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Object> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
    }

    @Override
    public Set<String> keySet() {
        return Collections.emptySet();
    }

    @Override
    public Collection<Object> values() {
        return Collections.emptyList();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return Collections.emptySet();
    }

    @Override
    public <T> T getOrCreate(final String key, final Supplier<T> createSupplier) {
        throw new UnsupportedOperationException();
    }

}
