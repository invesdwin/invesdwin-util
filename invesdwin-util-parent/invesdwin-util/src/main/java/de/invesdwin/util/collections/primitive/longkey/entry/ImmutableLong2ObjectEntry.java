package de.invesdwin.util.collections.primitive.longkey.entry;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

@SuppressWarnings("serial")
@Immutable
public class ImmutableLong2ObjectEntry<V> extends AValueObject implements Long2ObjectMap.Entry<V> {

    private final long key;
    private final V value;

    protected ImmutableLong2ObjectEntry(final long key, final V value) {
        this.key = key;
        this.value = value;
    }

    public static <V> ImmutableLong2ObjectEntry<V> of(final long key, final V value) {
        return new ImmutableLong2ObjectEntry<V>(key, value);
    }

    @Override
    public long getLongKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getLongKey(), getValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Long2ObjectMap.Entry) {
            @SuppressWarnings("rawtypes")
            final Long2ObjectMap.Entry castObj = (Long2ObjectMap.Entry) obj;
            return Objects.equals(getLongKey(), castObj.getLongKey()) && Objects.equals(getValue(), castObj.getValue());
        } else if (obj instanceof Map.Entry) {
            @SuppressWarnings("rawtypes")
            final Map.Entry castObj = (Map.Entry) obj;
            return Objects.equals(getLongKey(), castObj.getKey()) && Objects.equals(getValue(), castObj.getValue());
        } else {
            return false;
        }
    }

    @Override
    public V setValue(final V value) {
        throw new UnsupportedOperationException();
    }

}
