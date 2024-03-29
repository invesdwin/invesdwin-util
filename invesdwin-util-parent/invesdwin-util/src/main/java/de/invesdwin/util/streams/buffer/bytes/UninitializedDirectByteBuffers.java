package de.invesdwin.util.streams.buffer.bytes;

import javax.annotation.concurrent.Immutable;

import org.agrona.BitUtil;
import org.agrona.BufferUtil;

import de.invesdwin.util.lang.reflection.Reflections;

@SuppressWarnings("restriction")
@Immutable
public final class UninitializedDirectByteBuffers {

    private static final long BYTE_BUFFER_CAPACITY_FIELD_OFFSET;
    private static final long BYTE_BUFFER_LIMIT_FIELD_OFFSET;

    static {
        try {
            BYTE_BUFFER_CAPACITY_FIELD_OFFSET = Reflections.getUnsafe()
                    .objectFieldOffset(java.nio.Buffer.class.getDeclaredField("capacity"));
            BYTE_BUFFER_LIMIT_FIELD_OFFSET = Reflections.getUnsafe()
                    .objectFieldOffset(java.nio.Buffer.class.getDeclaredField("limit"));
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private UninitializedDirectByteBuffers() {}

    public static boolean isDirectByteBufferNoCleanerSupported() {
        return io.netty.util.internal.PlatformDependent.hasDirectBufferNoCleanerConstructor()
                || io.netty.util.internal.PlatformDependent.useDirectBufferNoCleaner();
    }

    /**
     * Might return null if this is unsupported.
     */
    @SuppressWarnings("restriction")
    public static java.nio.ByteBuffer asDirectByteBufferNoCleaner(final long address, final int length) {
        if (io.netty.util.internal.PlatformDependent.hasDirectBufferNoCleanerConstructor()) {
            return io.netty.util.internal.PlatformDependent.directBuffer(address, length);
        } else {
            final java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocateDirect(0);
            Reflections.getUnsafe().putLong(bb, BufferUtil.BYTE_BUFFER_ADDRESS_FIELD_OFFSET, address);
            Reflections.getUnsafe().putInt(bb, BYTE_BUFFER_CAPACITY_FIELD_OFFSET, length);
            Reflections.getUnsafe().putInt(bb, BYTE_BUFFER_LIMIT_FIELD_OFFSET, length);
            return bb;
        }
    }

    public static java.nio.ByteBuffer allocateDirectByteBufferNoCleaner(final int capacity) {
        if (io.netty.util.internal.PlatformDependent.useDirectBufferNoCleaner()) {
            return io.netty.util.internal.PlatformDependent.allocateDirectNoCleaner(capacity);
        } else if (io.netty.util.internal.PlatformDependent.hasDirectBufferNoCleanerConstructor()) {
            final long address = io.netty.util.internal.PlatformDependent.allocateMemory(capacity);
            return io.netty.util.internal.PlatformDependent.directBuffer(address, capacity);
        } else {
            //fallback to normal cleaner
            return java.nio.ByteBuffer.allocateDirect(capacity);
        }
    }

    @SuppressWarnings("restriction")
    public static java.nio.ByteBuffer reallocateDirectByteBufferNoCleaner(final java.nio.ByteBuffer buffer,
            final int newCapacity) {
        if (io.netty.util.internal.PlatformDependent.useDirectBufferNoCleaner()) {
            return io.netty.util.internal.PlatformDependent.reallocateDirectNoCleaner(buffer, newCapacity);
        } else if (io.netty.util.internal.PlatformDependent.hasDirectBufferNoCleanerConstructor()) {
            final long address = BufferUtil.address(buffer);
            final long newAddress = io.netty.util.internal.PlatformDependent.reallocateMemory(address, newCapacity);
            return io.netty.util.internal.PlatformDependent.directBuffer(newAddress, newCapacity);
        } else {
            final long address = BufferUtil.address(buffer);
            final java.nio.ByteBuffer newBuffer = java.nio.ByteBuffer.allocateDirect(newCapacity);
            final long newAddress = BufferUtil.address(newBuffer);
            Reflections.getUnsafe().copyMemory(address, newAddress, newCapacity);
            BufferUtil.free(buffer); //release old buffer
            return newBuffer;
        }
    }

    public static void freeDirectByteBufferNoCleaner(final java.nio.ByteBuffer buffer) {
        if (io.netty.util.internal.PlatformDependent.useDirectBufferNoCleaner()) {
            io.netty.util.internal.PlatformDependent.freeDirectNoCleaner(buffer);
        } else {
            //use the internal cleaner
            BufferUtil.free(buffer);
        }
    }

    public static java.nio.ByteBuffer allocateDirectByteBufferNoCleanerAligned(final int capacity,
            final int alignment) {
        if (!BitUtil.isPowerOfTwo(alignment)) {
            throw new IllegalArgumentException("Must be a power of 2: alignment=" + alignment);
        }

        final java.nio.ByteBuffer buffer = allocateDirectByteBufferNoCleaner(capacity + alignment);

        final long address = BufferUtil.address(buffer);
        final int remainder = (int) (address & (alignment - 1));
        final int offset = alignment - remainder;

        ByteBuffers.limit(buffer, capacity + offset);
        ByteBuffers.position(buffer, offset);

        final java.nio.ByteBuffer slice = buffer.slice();
        return slice;
    }

}
