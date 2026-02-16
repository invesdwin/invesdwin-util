package de.invesdwin.util.collections.primitive.intkey.entry;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

@SuppressWarnings("serial")
@Immutable
public class ImmutableInt2ObjectEntry<V> extends AValueObject implements Int2ObjectMap.Entry<V> {

    private final int key;
    private final V value;

    protected ImmutableInt2ObjectEntry(final int key, final V value) {
        this.key = key;
        this.value = value;
    }

    public static <V> ImmutableInt2ObjectEntry<V> of(final int key, final V value) {
        return new ImmutableInt2ObjectEntry<V>(key, value);
    }

    @Override
    public int getIntKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIntKey(), getValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Int2ObjectMap.Entry) {
            @SuppressWarnings("rawtypes")
            final Int2ObjectMap.Entry castObj = (Int2ObjectMap.Entry) obj;
            return Objects.equals(getIntKey(), castObj.getIntKey()) && Objects.equals(getValue(), castObj.getValue());
        } else if (obj instanceof Map.Entry) {
            @SuppressWarnings("rawtypes")
            final Map.Entry castObj = (Map.Entry) obj;
            return Objects.equals(getIntKey(), castObj.getKey()) && Objects.equals(getValue(), castObj.getValue());
        } else {
            return false;
        }
    }

    @Override
    public V setValue(final V value) {
        throw new UnsupportedOperationException();
    }

}
