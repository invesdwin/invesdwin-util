package de.invesdwin.util.collections.primitive.longkey.entry;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import it.unimi.dsi.fastutil.longs.Long2LongMap;

@Immutable
public class ImmutableLong2LongEntry extends AValueObject implements Long2LongMap.Entry {

    private final long key;
    private final long value;

    protected ImmutableLong2LongEntry(final long key, final long value) {
        this.key = key;
        this.value = value;
    }

    public static ImmutableLong2LongEntry of(final long key, final long value) {
        return new ImmutableLong2LongEntry(key, value);
    }

    @Override
    public long getLongKey() {
        return key;
    }

    @Override
    public long getLongValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getLongKey(), getLongValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Long2LongMap.Entry) {
            final Long2LongMap.Entry castObj = (Long2LongMap.Entry) obj;
            return Objects.equals(getLongKey(), castObj.getLongKey())
                    && Objects.equals(getLongValue(), castObj.getLongValue());
        } else if (obj instanceof Map.Entry) {
            @SuppressWarnings("rawtypes")
            final Map.Entry castObj = (Map.Entry) obj;
            return Objects.equals(getLongKey(), castObj.getKey()) && Objects.equals(getLongValue(), castObj.getValue());
        } else {
            return false;
        }
    }

    @Override
    public long setValue(final long value) {
        throw new UnsupportedOperationException();
    }

}
