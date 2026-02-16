package de.invesdwin.util.collections.primitive.longkey.entry;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import it.unimi.dsi.fastutil.longs.Long2FloatMap;

@Immutable
public class ImmutableLong2FloatEntry extends AValueObject implements Long2FloatMap.Entry {

    private final long key;
    private final float value;

    protected ImmutableLong2FloatEntry(final long key, final float value) {
        this.key = key;
        this.value = value;
    }

    public static ImmutableLong2FloatEntry of(final long key, final float value) {
        return new ImmutableLong2FloatEntry(key, value);
    }

    @Override
    public long getLongKey() {
        return key;
    }

    @Override
    public float getFloatValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getLongKey(), getFloatValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Long2FloatMap.Entry) {
            final Long2FloatMap.Entry castObj = (Long2FloatMap.Entry) obj;
            return Objects.equals(getLongKey(), castObj.getLongKey())
                    && Objects.equals(getFloatValue(), castObj.getFloatValue());
        } else if (obj instanceof Map.Entry) {
            @SuppressWarnings("rawtypes")
            final Map.Entry castObj = (Map.Entry) obj;
            return Objects.equals(getLongKey(), castObj.getKey())
                    && Objects.equals(getFloatValue(), castObj.getValue());
        } else {
            return false;
        }
    }

    @Override
    public float setValue(final float value) {
        throw new UnsupportedOperationException();
    }

}
