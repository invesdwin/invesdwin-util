package de.invesdwin.util.streams.buffer.bytes;

import javax.annotation.concurrent.Immutable;

import org.agrona.BufferUtil;

import de.invesdwin.util.lang.reflection.Reflections;

@Immutable
public final class UninitializedDirectByteBuffers {

    private UninitializedDirectByteBuffers() {
    }

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
            Reflections.getUnsafe().putInt(bb, BufferUtil.BYTE_BUFFER_OFFSET_FIELD_OFFSET, length);
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

}
