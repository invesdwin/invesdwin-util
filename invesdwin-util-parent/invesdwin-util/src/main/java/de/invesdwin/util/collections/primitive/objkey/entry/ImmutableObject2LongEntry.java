package de.invesdwin.util.collections.primitive.objkey.entry;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

@Immutable
public class ImmutableObject2LongEntry<K> extends AValueObject implements Object2LongMap.Entry<K> {

    private final K key;
    private final long value;

    protected ImmutableObject2LongEntry(final K key, final long value) {
        this.key = key;
        this.value = value;
    }

    public static <K> ImmutableObject2LongEntry<K> of(final K key, final long value) {
        return new ImmutableObject2LongEntry<K>(key, value);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public long getLongValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getKey(), getLongValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Object2LongMap.Entry) {
            @SuppressWarnings("rawtypes")
            final Object2LongMap.Entry castObj = (Object2LongMap.Entry) obj;
            return Objects.equals(getKey(), castObj.getKey()) && Objects.equals(getLongValue(), castObj.getLongValue());
        } else if (obj instanceof Map.Entry) {
            @SuppressWarnings("rawtypes")
            final Map.Entry castObj = (Map.Entry) obj;
            return Objects.equals(getKey(), castObj.getKey()) && Objects.equals(getLongValue(), castObj.getValue());
        } else {
            return false;
        }
    }

    @Override
    public long setValue(final long value) {
        throw new UnsupportedOperationException();
    }

}
