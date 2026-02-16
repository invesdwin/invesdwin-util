package de.invesdwin.util.collections.primitive.intkey.entry;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import it.unimi.dsi.fastutil.ints.Int2LongMap;

@Immutable
public class ImmutableInt2LongEntry extends AValueObject implements Int2LongMap.Entry {

    private final int key;
    private final long value;

    protected ImmutableInt2LongEntry(final int key, final long value) {
        this.key = key;
        this.value = value;
    }

    public static ImmutableInt2LongEntry of(final int key, final long value) {
        return new ImmutableInt2LongEntry(key, value);
    }

    @Override
    public int getIntKey() {
        return key;
    }

    @Override
    public long getLongValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIntKey(), getLongValue());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Int2LongMap.Entry) {
            final Int2LongMap.Entry castObj = (Int2LongMap.Entry) obj;
            return Objects.equals(getIntKey(), castObj.getIntKey())
                    && Objects.equals(getLongValue(), castObj.getLongValue());
        } else if (obj instanceof Map.Entry) {
            @SuppressWarnings("rawtypes")
            final Map.Entry castObj = (Map.Entry) obj;
            return Objects.equals(getIntKey(), castObj.getKey()) && Objects.equals(getLongValue(), castObj.getValue());
        } else {
            return false;
        }
    }

    @Override
    public long setValue(final long value) {
        throw new UnsupportedOperationException();
    }

}
