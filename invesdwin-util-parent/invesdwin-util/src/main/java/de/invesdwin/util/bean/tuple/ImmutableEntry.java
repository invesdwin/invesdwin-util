package de.invesdwin.util.bean.tuple;

import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;

@Immutable
public class ImmutableEntry<K, V> extends AValueObject implements Entry<K, V> {

    private final K key;
    private final V value;

    protected ImmutableEntry(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> ImmutableEntry<K, V> of(final K key, final V value) {
        return new ImmutableEntry<K, V>(key, value);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getKey(), getValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Entry) {
            final Entry<?, ?> castObj = (Entry<?, ?>) obj;
            return Objects.equals(getKey(), castObj.getKey()) && Objects.equals(getValue(), castObj.getValue());
        } else {
            return false;
        }
    }

    @Override
    public V setValue(final V value) {
        throw new UnsupportedOperationException();
    }

}
