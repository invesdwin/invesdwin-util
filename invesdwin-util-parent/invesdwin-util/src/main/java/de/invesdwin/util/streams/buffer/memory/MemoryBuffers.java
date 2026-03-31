package de.invesdwin.util.streams.buffer.memory;

import javax.annotation.concurrent.Immutable;

import org.agrona.DirectBuffer;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ChronicleDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.DirectMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.MappedExpandableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.MappedMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.UnsafeMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.internal.direct.DirectExpandableMemoryBufferPool;
import de.invesdwin.util.streams.buffer.memory.internal.heap.HeapExpandableMemoryBufferPool;
import de.invesdwin.util.streams.buffer.memory.internal.mapped.MappedExpandableMemoryBufferPool;

@Immutable
public final class MemoryBuffers {

    public static final long EXPANDABLE_LENGTH = -1;

    public static final IObjectPool<ICloseableMemoryBuffer> EXPANDABLE_POOL = HeapExpandableMemoryBufferPool.INSTANCE;
    public static final IObjectPool<ICloseableMemoryBuffer> DIRECT_EXPANDABLE_POOL = DirectExpandableMemoryBufferPool.INSTANCE;
    public static final IObjectPool<ICloseableMemoryBuffer> MAPPED_EXPANDABLE_POOL = MappedExpandableMemoryBufferPool.INSTANCE;

    private MemoryBuffers() {}

    public static IMemoryBuffer allocate(final Long fixedLength) {
        if (fixedLength == null) {
            return allocateExpandable();
        } else {
            return allocate(fixedLength.intValue());
        }
    }

    public static IMemoryBuffer allocate(final long fixedLength) {
        if (fixedLength == 0) {
            return EmptyMemoryBuffer.INSTANCE;
        } else if (fixedLength < 0) {
            return allocateExpandable();
        } else {
            return allocateFixed(fixedLength);
        }
    }

    public static IMemoryBuffer allocateFixed(final long fixedLength) {
        if (fixedLength <= Integer.MAX_VALUE) {
            return wrap(ByteBuffers.allocateByteArray(ByteBuffers.checkedCast(fixedLength)));
        } else {
            /*
             * Maybe implement a segmented array memory buffer instead of going into direct memory?
             * 
             * Though we should anyhow not allocate such large buffers on heap, so we just keep using this for now.
             */
            return allocateDirectFixed(fixedLength);
        }
    }

    public static IMemoryBuffer allocateExpandable() {
        final net.openhft.chronicle.bytes.OnHeapBytes bytes = net.openhft.chronicle.bytes.Bytes.allocateElasticOnHeap();
        return new ChronicleDelegateMemoryBuffer(bytes, true);
    }

    public static IMemoryBuffer allocateExpandable(final long initialLength) {
        if (initialLength == 0) {
            return EmptyMemoryBuffer.INSTANCE;
        } else if (initialLength < 0) {
            return allocateExpandable();
        } else if (initialLength <= Integer.MAX_VALUE) {
            /*
             * Maybe this should directly go against allocateDirectExpandable so that the maximum capacity of the buffer
             * is not limited by Integer.MAX_VALUE?
             * 
             * Though we should anyhow not allocate such large buffers on heap, so we just keep using this for now.
             */
            final net.openhft.chronicle.bytes.OnHeapBytes bytes = net.openhft.chronicle.bytes.Bytes
                    .allocateElasticOnHeap(ByteBuffers.checkedCast(initialLength));
            return new ChronicleDelegateMemoryBuffer(bytes, true);
        } else {
            //hard to implement an expandable on-heap buffer that can grow beyond Integer.MAX_VALUE, so we just use the direct one for now
            return allocateDirectExpandable(initialLength);
        }
    }

    public static IMemoryBuffer allocateDirect(final Long fixedLength) {
        if (fixedLength == null) {
            return allocateDirectExpandable();
        } else {
            return allocateDirect(fixedLength.intValue());
        }
    }

    public static IMemoryBuffer allocateDirect(final long fixedLength) {
        if (fixedLength == 0) {
            return EmptyMemoryBuffer.INSTANCE;
        } else if (fixedLength < 0) {
            return allocateDirectExpandable();
        } else {
            return allocateDirectFixed(fixedLength);
        }
    }

    public static IMemoryBuffer allocateDirectFixed(final long fixedLength) {
        return new DirectMemoryBuffer(fixedLength);
    }

    public static IMemoryBuffer allocateDirectExpandable() {
        final net.openhft.chronicle.bytes.NativeBytes<Void> bytes = net.openhft.chronicle.bytes.Bytes
                .allocateElasticDirect();
        return new ChronicleDelegateMemoryBuffer(bytes, true);
    }

    public static IMemoryBuffer allocateDirectExpandable(final long initialLength) {
        final net.openhft.chronicle.bytes.NativeBytes<Void> bytes = net.openhft.chronicle.bytes.Bytes
                .allocateElasticDirect(initialLength);
        return new ChronicleDelegateMemoryBuffer(bytes, true);
    }

    public static IMemoryBuffer allocateMapped(final Long fixedLength) {
        if (fixedLength == null) {
            return allocateMappedExpandable();
        } else {
            return allocateMapped(fixedLength.intValue());
        }
    }

    public static IMemoryBuffer allocateMapped(final long fixedLength) {
        if (fixedLength == 0) {
            return EmptyMemoryBuffer.INSTANCE;
        } else if (fixedLength < 0) {
            return allocateMappedExpandable();
        } else {
            return allocateMappedFixed(fixedLength);
        }
    }

    public static IMemoryBuffer allocateMappedFixed(final long fixedLength) {
        return new MappedMemoryBuffer(fixedLength);
    }

    public static IMemoryBuffer allocateMappedExpandable() {
        return new MappedExpandableMemoryBuffer();
    }

    public static IMemoryBuffer allocateMappedExpandable(final long initialLength) {
        return new MappedExpandableMemoryBuffer(initialLength);
    }

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

    public static long newAllocateFixedLength(final Long fixedLength) {
        if (fixedLength == null) {
            return EXPANDABLE_LENGTH;
        } else {
            return fixedLength.intValue();
        }
    }

    public static Long newFixedLength(final long fixedLength) {
        if (fixedLength < 0) {
            return null;
        } else {
            return fixedLength;
        }
    }

}
