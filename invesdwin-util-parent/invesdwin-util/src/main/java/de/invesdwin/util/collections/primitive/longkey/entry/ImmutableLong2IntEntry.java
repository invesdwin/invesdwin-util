package de.invesdwin.util.collections.primitive.longkey.entry;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import it.unimi.dsi.fastutil.longs.Long2IntMap;

@Immutable
public class ImmutableLong2IntEntry extends AValueObject implements Long2IntMap.Entry {

    private final long key;
    private final int value;

    protected ImmutableLong2IntEntry(final long key, final int value) {
        this.key = key;
        this.value = value;
    }

    public static ImmutableLong2IntEntry of(final long key, final int value) {
        return new ImmutableLong2IntEntry(key, value);
    }

    @Override
    public long getLongKey() {
        return key;
    }

    @Override
    public int getIntValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getLongKey(), getIntValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Long2IntMap.Entry) {
            final Long2IntMap.Entry castObj = (Long2IntMap.Entry) obj;
            return Objects.equals(getLongKey(), castObj.getLongKey())
                    && Objects.equals(getIntValue(), castObj.getIntValue());
        } else if (obj instanceof Map.Entry) {
            @SuppressWarnings("rawtypes")
            final Map.Entry castObj = (Map.Entry) obj;
            return Objects.equals(getLongKey(), castObj.getKey()) && Objects.equals(getIntValue(), castObj.getValue());
        } else {
            return false;
        }
    }

    @Override
    public int setValue(final int value) {
        throw new UnsupportedOperationException();
    }

}
