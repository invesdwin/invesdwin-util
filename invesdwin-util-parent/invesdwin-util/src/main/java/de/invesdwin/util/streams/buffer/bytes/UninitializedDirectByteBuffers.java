package de.invesdwin.util.streams.buffer.bytes;

import javax.annotation.concurrent.Immutable;

import org.agrona.BitUtil;
import org.agrona.BufferUtil;
import org.agrona.UnsafeApi;

import de.invesdwin.util.streams.buffer.bytes.cleanable.SlicedCleanableDirectBuffer;
import de.invesdwin.util.streams.buffer.bytes.cleanable.WrappedCleanableDirectBuffer;

@Immutable
public final class UninitializedDirectByteBuffers {

    private static final long BYTE_BUFFER_CAPACITY_FIELD_OFFSET;
    private static final long BYTE_BUFFER_LIMIT_FIELD_OFFSET;
    private static final boolean NO_CLEANER_SUPPORTED = io.netty.util.internal.PlatformDependent
            .hasDirectBufferNoCleanerConstructor() && io.netty.util.internal.PlatformDependent.hasUnsafe()
            || io.netty.util.internal.PlatformDependent.useDirectBufferNoCleaner();

    static {
        try {
            BYTE_BUFFER_CAPACITY_FIELD_OFFSET = UnsafeApi
                    .objectFieldOffset(java.nio.Buffer.class.getDeclaredField("capacity"));
            BYTE_BUFFER_LIMIT_FIELD_OFFSET = UnsafeApi
                    .objectFieldOffset(java.nio.Buffer.class.getDeclaredField("limit"));
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private UninitializedDirectByteBuffers() {}

    public static boolean isDirectByteBufferNoCleanerSupported() {
        return NO_CLEANER_SUPPORTED;
    }

    /**
     * Might return null if this is unsupported.
     */
    public static java.nio.ByteBuffer asDirectByteBufferNoCleaner(final long address, final int length) {
        if (isDirectByteBufferNoCleanerSupported()) {
            return io.netty.util.internal.PlatformDependent.directBuffer(address, length);
        } else {
            final java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocateDirect(0);
            UnsafeApi.putLong(bb, BufferUtil.BYTE_BUFFER_ADDRESS_FIELD_OFFSET, address);
            UnsafeApi.putInt(bb, BYTE_BUFFER_CAPACITY_FIELD_OFFSET, length);
            UnsafeApi.putInt(bb, BYTE_BUFFER_LIMIT_FIELD_OFFSET, length);
            return bb;
        }
    }

    public static io.netty.util.internal.CleanableDirectBuffer allocateDirectByteBufferNoCleaner(final int capacity) {
        if (isDirectByteBufferNoCleanerSupported()) {
            return io.netty.util.internal.PlatformDependent.allocateDirect(capacity, true);
        } else if (io.netty.util.internal.PlatformDependent.hasUnsafe()) {
            final long address = io.netty.util.internal.PlatformDependent.allocateMemory(capacity);
            return new WrappedCleanableDirectBuffer(
                    io.netty.util.internal.PlatformDependent.directBuffer(address, capacity));
        } else {
            //fallback to normal cleaner
            return new WrappedCleanableDirectBuffer(java.nio.ByteBuffer.allocateDirect(capacity));
        }
    }

    public static io.netty.util.internal.CleanableDirectBuffer reallocateDirectByteBufferNoCleaner(
            final io.netty.util.internal.CleanableDirectBuffer buffer, final int newCapacity) {
        if (isDirectByteBufferNoCleanerSupported()) {
            return io.netty.util.internal.PlatformDependent.reallocateDirect(buffer, newCapacity);
        } else if (io.netty.util.internal.PlatformDependent.hasUnsafe()) {
            final long address = BufferUtil.address(buffer.buffer());
            final long newAddress = io.netty.util.internal.PlatformDependent.reallocateMemory(address, newCapacity);
            return new WrappedCleanableDirectBuffer(
                    io.netty.util.internal.PlatformDependent.directBuffer(newAddress, newCapacity));
        } else {
            final long address = BufferUtil.address(buffer.buffer());
            final java.nio.ByteBuffer newBuffer = java.nio.ByteBuffer.allocateDirect(newCapacity);
            final long newAddress = BufferUtil.address(newBuffer);
            UnsafeApi.copyMemory(address, newAddress, newCapacity);
            BufferUtil.free(buffer.buffer()); //release old buffer
            return new WrappedCleanableDirectBuffer(newBuffer);
        }
    }

    public static io.netty.util.internal.CleanableDirectBuffer allocateDirectByteBufferNoCleanerAligned(
            final int capacity, final int alignment) {
        if (!BitUtil.isPowerOfTwo(alignment)) {
            throw new IllegalArgumentException("Must be a power of 2: alignment=" + alignment);
        }

        final io.netty.util.internal.CleanableDirectBuffer buffer = allocateDirectByteBufferNoCleaner(
                capacity + alignment);

        final java.nio.ByteBuffer buf = buffer.buffer();
        final long address = BufferUtil.address(buf);
        final int remainder = (int) (address & (alignment - 1));
        final int offset = alignment - remainder;

        ByteBuffers.limit(buf, capacity + offset);
        ByteBuffers.position(buf, offset);

        final java.nio.ByteBuffer slice = buf.slice();
        return new SlicedCleanableDirectBuffer(slice, buffer);
    }

}
