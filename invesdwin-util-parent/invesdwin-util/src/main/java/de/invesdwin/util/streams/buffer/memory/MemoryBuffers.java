package de.invesdwin.util.streams.buffer.memory;

import javax.annotation.concurrent.Immutable;

import org.agrona.DirectBuffer;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.UnsafeMemoryBuffer;

@Immutable
public final class MemoryBuffers {

    private MemoryBuffers() {}

    public static IMemoryBuffer wrap(final long address, final long length) {
        return new UnsafeMemoryBuffer(address, length);
    }

    public static IMemoryBuffer wrap(final DirectBuffer buffer) {
        /*
         * We do not check if the instance might be a IByteBuffer already since we can not risk to share the mutable
         * slices between threads. So we don't unwrap that here.
         */
        return new UnsafeMemoryBuffer(buffer);
    }

    public static IMemoryBuffer wrap(final IByteBuffer buffer) {
        /*
         * We do not check if the instance might be a IByteBuffer already since we can not risk to share the mutable
         * slices between threads. So we don't unwrap that here.
         */
        return buffer.asMemoryBuffer();
    }

    public static IMemoryBuffer wrap(final byte[] bytes) {
        return new UnsafeMemoryBuffer(bytes);
    }

    public static IMemoryBuffer wrap(final java.nio.ByteBuffer buffer) {
        if (buffer.hasArray() && ByteBuffers.wrapAdjustment(buffer) == 0) {
            return wrap(buffer.array());
        } else {
            return new UnsafeMemoryBuffer(buffer);
        }
    }

    public static IMemoryBuffer wrapFrom(final byte[] bytes, final int index) {
        return wrap(bytes, index, bytes.length - index);
    }

    public static IMemoryBuffer wrapTo(final byte[] bytes, final int length) {
        return wrap(bytes, 0, length);
    }

    public static IMemoryBuffer wrap(final byte[] bytes, final int index, final int length) {
        if (index == 0 && length == bytes.length) {
            return wrap(bytes);
        } else {
            return new UnsafeMemoryBuffer(bytes, index, length);
        }
    }

    public static IMemoryBuffer wrapFrom(final java.nio.ByteBuffer buffer, final int index) {
        return wrap(buffer, index, buffer.capacity() - index);
    }

    public static IMemoryBuffer wrapTo(final java.nio.ByteBuffer buffer, final int length) {
        return wrap(buffer, 0, length);
    }

    public static IMemoryBuffer wrap(final java.nio.ByteBuffer buffer, final int index, final int length) {
        if (index == 0 && length == buffer.capacity()) {
            return wrap(buffer);
        } else {
            return new UnsafeMemoryBuffer(buffer, index, length);
        }
    }

    public static IMemoryBuffer wrapFrom(final DirectBuffer buffer, final int index) {
        return wrap(buffer, index, buffer.capacity() - index);
    }

    public static IMemoryBuffer wrapTo(final DirectBuffer buffer, final int length) {
        return wrap(buffer, 0, length);
    }

    public static IMemoryBuffer wrap(final DirectBuffer buffer, final int index, final int length) {
        if (index == 0 && length == buffer.capacity()) {
            return wrap(buffer);
        } else {
            return new UnsafeMemoryBuffer(buffer, index, length);
        }
    }

    public static IMemoryBuffer wrapFrom(final IByteBuffer buffer, final int index) {
        return wrap(buffer, index, buffer.capacity() - index);
    }

    public static IMemoryBuffer wrapTo(final IByteBuffer buffer, final int length) {
        return wrap(buffer, 0, length);
    }

    public static IMemoryBuffer wrap(final IByteBuffer buffer, final int index, final int length) {
        if (index == 0 && length == buffer.capacity()) {
            return wrap(buffer);
        } else {
            return buffer.asMemoryBuffer(index, length);
        }
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
            throw FastIndexOutOfBoundsException.getInstance("desiredCapacity=%s is beyond capacity=%s", desiredCapacity,
                    capacity);
        }
    }

    public static long calculateExpansion(final long requestedSize) {
        return 1L << (64 - Long.numberOfLeadingZeros(requestedSize - 1L));
    }

    public static <T> T assertBuffer(final T buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer should not be null (this can cause a jvm crash)");
        }
        return buffer;
    }

    public static boolean constantTimeEquals(final byte[] digesta, final byte[] digestb) {
        return ByteBuffers.constantTimeEquals(digesta, digestb);
    }

    public static boolean constantTimeEquals(final IMemoryBuffer digesta, final byte[] digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final long lenA = digesta.capacity();
        final int lenB = digestb.length;

        if (lenB == 0) {
            return lenA == 0;
        }

        int result = 0;
        result |= lenA - lenB;

        // time-constant comparison
        for (int i = 0; i < lenA; i++) {
            // If i >= lenB, indexB is 0; otherwise, i.
            final int indexB = ((i - lenB) >>> 31) * i;
            result |= digesta.getByte(i) ^ digestb[indexB];
        }
        return result == 0;
    }

    public static boolean constantTimeEquals(final IMemoryBuffer digesta, final byte[] digestb, final int digestbOffset,
            final int digestbLength) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final long lenA = digesta.capacity();
        final int lenB = digestbLength;

        if (lenB == 0) {
            return lenA == 0;
        }

        int result = 0;
        result |= lenA - lenB;

        // time-constant comparison
        for (int i = 0; i < lenA; i++) {
            // If i >= lenB, indexB is 0; otherwise, i.
            final int indexB = digestbOffset + ((i - lenB) >>> 31) * i;
            result |= digesta.getByte(i) ^ digestb[indexB];
        }
        return result == 0;
    }

    public static boolean constantTimeEquals(final byte[] digesta, final IMemoryBuffer digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final int lenA = digesta.length;
        final long lenB = digestb.capacity();

        if (lenB == 0) {
            return lenA == 0;
        }

        int result = 0;
        result |= lenA - lenB;

        // time-constant comparison
        for (int i = 0; i < lenB; i++) {
            // If i >= lenB, indexB is 0; otherwise, i.
            final int indexA = ((i - lenA) >>> 31) * i;
            result |= digesta[indexA] ^ digestb.getByte(i);
        }
        return result == 0;
    }

    public static boolean constantTimeEquals(final IMemoryBuffer digesta, final IMemoryBuffer digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final long lenA = digesta.capacity();
        final long lenB = digestb.capacity();

        if (lenB == 0) {
            return lenA == 0;
        }

        int result = 0;
        result |= lenA - lenB;

        // time-constant comparison
        for (int i = 0; i < lenA; i++) {
            // If i >= lenB, indexB is 0; otherwise, i.
            final long indexB = ((i - lenB) >>> 31) * i;
            result |= digesta.getByte(i) ^ digestb.getByte(indexB);
        }
        return result == 0;
    }

    public static boolean equals(final byte[] digesta, final byte[] digestb) {
        return ByteBuffers.equals(digesta, digestb);
    }

    public static boolean equals(final IMemoryBuffer digesta, final byte[] digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final long lenA = digesta.capacity();
        final int lenB = digestb.length;

        if (lenB == 0) {
            return lenA == 0;
        }

        if (lenA != lenB) {
            return false;
        }

        for (int i = 0; i < lenA; i++) {
            if (digesta.getByte(i) != digestb[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(final byte[] digesta, final IMemoryBuffer digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final int lenA = digesta.length;
        final long lenB = digestb.capacity();

        if (lenB == 0) {
            return lenA == 0;
        }

        if (lenA != lenB) {
            return false;
        }

        for (int i = 0; i < lenA; i++) {
            if (digesta[i] != digestb.getByte(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(final IMemoryBuffer digesta, final IMemoryBuffer digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final long lenA = digesta.capacity();
        final long lenB = digestb.capacity();

        if (lenB == 0) {
            return lenA == 0;
        }

        if (lenA != lenB) {
            return false;
        }

        for (long i = 0; i < lenA; i++) {
            if (digesta.getByte(i) != digestb.getByte(i)) {
                return false;
            }
        }
        return true;
    }

}
