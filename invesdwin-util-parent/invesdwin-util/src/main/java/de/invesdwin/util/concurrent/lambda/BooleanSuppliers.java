package de.invesdwin.util.concurrent.lambda;

import java.nio.ByteBuffer;
import java.util.function.BooleanSupplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Bytes;

@Immutable
public final class BooleanSuppliers {

    private BooleanSuppliers() {
    }

    public static void putBooleanSupplier(final ByteBuffer buffer, final BooleanSupplier value) {
        if (value == null) {
            buffer.put(Byte.MIN_VALUE);
        } else {
            buffer.put(Bytes.checkedCast(value.getAsBoolean()));
        }
    }

    public static BooleanSupplier extractBooleanSupplier(final ByteBuffer buffer, final int index) {
        final byte value = buffer.get(index);
        return extractBooleanSupplier(value);
    }

    public static BooleanSupplier extractBooleanSupplier(final ByteBuffer buffer) {
        final byte value = buffer.get();
        return extractBooleanSupplier(value);
    }

    public static BooleanSupplier extractBooleanSupplier(final byte value) {
        if (value == Byte.MIN_VALUE) {
            return null;
        } else {
            return ImmutableBooleanSupplier.valueOf(Booleans.checkedCast(value));
        }
    }

}
