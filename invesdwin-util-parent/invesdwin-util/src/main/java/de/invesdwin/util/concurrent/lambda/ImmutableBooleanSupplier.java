package de.invesdwin.util.concurrent.lambda;

import java.util.function.BooleanSupplier;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ImmutableBooleanSupplier implements BooleanSupplier {

    private final boolean value;

    public ImmutableBooleanSupplier(final boolean value) {
        this.value = value;
    }

    @Override
    public boolean getAsBoolean() {
        return value;
    }

    public ImmutableBooleanSupplier valueOf(final BooleanSupplier supplier) {
        if (supplier == null) {
            return null;
        }
        return new ImmutableBooleanSupplier(supplier.getAsBoolean());
    }

}
