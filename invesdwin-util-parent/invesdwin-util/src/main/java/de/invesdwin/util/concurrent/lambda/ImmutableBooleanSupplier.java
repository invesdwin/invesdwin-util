package de.invesdwin.util.concurrent.lambda;

import java.util.function.BooleanSupplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;

@Immutable
public final class ImmutableBooleanSupplier implements BooleanSupplier, ISerializableValueObject {

    private final boolean value;

    private ImmutableBooleanSupplier(final boolean value) {
        this.value = value;
    }

    @Override
    public boolean getAsBoolean() {
        return value;
    }

    public static ImmutableBooleanSupplier valueOf(final Boolean value) {
        if (value == null) {
            return null;
        }
        return new ImmutableBooleanSupplier(value.booleanValue());
    }

    public static ImmutableBooleanSupplier valueOf(final boolean value) {
        return new ImmutableBooleanSupplier(value);
    }

    public static ImmutableBooleanSupplier valueOf(final BooleanSupplier supplier) {
        if (supplier == null) {
            return null;
        }
        return new ImmutableBooleanSupplier(supplier.getAsBoolean());
    }

}
