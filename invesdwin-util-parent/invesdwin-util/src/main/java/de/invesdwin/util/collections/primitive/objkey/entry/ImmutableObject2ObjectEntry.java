package de.invesdwin.util.collections.primitive.objkey.entry;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

@Immutable
public class ImmutableObject2ObjectEntry<K, V> extends AValueObject implements Object2ObjectMap.Entry<K, V> {

    private final K key;
    private final V value;

    protected ImmutableObject2ObjectEntry(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> ImmutableObject2ObjectEntry<K, V> of(final K key, final V value) {
        return new ImmutableObject2ObjectEntry<K, V>(key, value);
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
        if (obj instanceof Object2ObjectMap.Entry) {
            @SuppressWarnings("rawtypes")
            final Object2ObjectMap.Entry castObj = (Object2ObjectMap.Entry) obj;
            return Objects.equals(getKey(), castObj.getKey()) && Objects.equals(getValue(), castObj.getValue());
        } else if (obj instanceof Map.Entry) {
            @SuppressWarnings("rawtypes")
            final Map.Entry castObj = (Map.Entry) obj;
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
