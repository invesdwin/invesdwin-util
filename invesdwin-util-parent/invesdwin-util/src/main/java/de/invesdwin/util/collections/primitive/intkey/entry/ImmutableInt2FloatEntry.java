package de.invesdwin.util.collections.primitive.intkey.entry;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;

@Immutable
public class ImmutableInt2FloatEntry extends AValueObject implements Int2FloatMap.Entry {

    private final int key;
    private final float value;

    protected ImmutableInt2FloatEntry(final int key, final float value) {
        this.key = key;
        this.value = value;
    }

    public static ImmutableInt2FloatEntry of(final int key, final float value) {
        return new ImmutableInt2FloatEntry(key, value);
    }

    @Override
    public int getIntKey() {
        return key;
    }

    @Override
    public float getFloatValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIntKey(), getFloatValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Int2FloatMap.Entry) {
            final Int2FloatMap.Entry castObj = (Int2FloatMap.Entry) obj;
            return Objects.equals(getIntKey(), castObj.getIntKey())
                    && Objects.equals(getFloatValue(), castObj.getFloatValue());
        } else if (obj instanceof Map.Entry) {
            @SuppressWarnings("rawtypes")
            final Map.Entry castObj = (Map.Entry) obj;
            return Objects.equals(getIntKey(), castObj.getKey()) && Objects.equals(getFloatValue(), castObj.getValue());
        } else {
            return false;
        }
    }

    @Override
    public float setValue(final float value) {
        throw new UnsupportedOperationException();
    }

}
