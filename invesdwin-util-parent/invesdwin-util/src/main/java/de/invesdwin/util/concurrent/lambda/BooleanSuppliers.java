package de.invesdwin.util.concurrent.lambda;

import java.util.function.BooleanSupplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@Immutable
public final class BooleanSuppliers {

    private BooleanSuppliers() {
    }

    public static void putBooleanSupplier(final IByteBuffer buffer, final int index, final BooleanSupplier value) {
        if (value == null) {
            buffer.putByte(index, Byte.MIN_VALUE);
        } else {
            buffer.putByte(index, Bytes.checkedCast(value.getAsBoolean()));
        }
    }

    public static BooleanSupplier extractBooleanSupplier(final IByteBuffer buffer, final int index) {
        final byte value = buffer.getByte(index);
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
