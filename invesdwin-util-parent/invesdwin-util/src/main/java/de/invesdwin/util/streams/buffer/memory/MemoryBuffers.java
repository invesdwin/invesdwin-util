package de.invesdwin.util.streams.buffer.memory;

import java.util.Arrays;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@Immutable
public final class MemoryBuffers {

    private MemoryBuffers() {
    }

    public static byte[] asByteArrayCopyGet(final IMemoryBuffer buffer, final long index, final int length) {
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
        buffer.getBytes(index, bytes, 0, length);
        return bytes;
    }

    public static String toString(final IMemoryBuffer buffer) {
        final byte[] byteArray = buffer.asByteArrayCopy(0,
                (int) Longs.min(ByteBuffers.MAX_TO_STRING_COUNT, buffer.capacity()));
        return Objects.toStringHelper(buffer)
                .add("addressOffset", buffer.addressOffset())
                .add("capacity", buffer.capacity())
                .add("wrapAdjustment", buffer.wrapAdjustment())
                .with(Arrays.toString(byteArray))
                .toString();
    }

    public static void ensureCapacity(final IMemoryBuffer buffer, final long desiredCapacity) {
        final long capacity = buffer.capacity();
        if (desiredCapacity > capacity) {
            throw new IndexOutOfBoundsException(
                    "desiredCapacity=" + desiredCapacity + " is beyond capacity=" + capacity);
        }
    }

    public static long calculateExpansion(final long requestedSize) {
        return 1L << (64 - Long.numberOfLeadingZeros(requestedSize - 1L));
    }

}
