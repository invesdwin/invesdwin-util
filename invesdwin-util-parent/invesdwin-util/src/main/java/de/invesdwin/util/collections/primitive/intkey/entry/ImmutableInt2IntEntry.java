package de.invesdwin.util.collections.primitive.intkey.entry;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

@Immutable
public class ImmutableInt2IntEntry extends AValueObject implements Int2IntMap.Entry {

    private final int key;
    private final int value;

    protected ImmutableInt2IntEntry(final int key, final int value) {
        this.key = key;
        this.value = value;
    }

    public static ImmutableInt2IntEntry of(final int key, final int value) {
        return new ImmutableInt2IntEntry(key, value);
    }

    @Override
    public int getIntKey() {
        return key;
    }

    @Override
    public int getIntValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIntKey(), getIntValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Int2IntMap.Entry) {
            final Int2IntMap.Entry castObj = (Int2IntMap.Entry) obj;
            return Objects.equals(getIntKey(), castObj.getIntKey())
                    && Objects.equals(getIntValue(), castObj.getIntValue());
        } else if (obj instanceof Map.Entry) {
            @SuppressWarnings("rawtypes")
            final Map.Entry castObj = (Map.Entry) obj;
            return Objects.equals(getIntKey(), castObj.getKey()) && Objects.equals(getIntValue(), castObj.getValue());
        } else {
            return false;
        }
    }

    @Override
    public int setValue(final int value) {
        throw new UnsupportedOperationException();
    }

}
