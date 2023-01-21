package de.invesdwin.util.streams.buffer.bytes;

import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.ExpandableArrayBuffer;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.streams.buffer.bytes.delegate.AgronaDelegateMutableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.ChronicleDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.NettyDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.NioDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.OrderedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.ArrayExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.internal.DirectExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.internal.UninitializedDirectByteBuffer;
import io.netty.buffer.Unpooled;
import net.openhft.chronicle.bytes.BytesStore;

@NotThreadSafe
public class ByteBuffersTest {

    private static final int BUFFER_SIZE = 10000;

    static {
        //CHECKSTYLE:OFF
        System.setProperty("io.netty.tryReflectionSetAccessible", "true");
        System.setProperty("io.netty.uninitializedArrayAllocationThreshold", "1");
        //CHECKSTYLE:ON
        //java 16 otherwise requires --illegal-access=permit --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED
        Reflections.disableJavaModuleSystemRestrictions();
    }

    @Test
    public void testAgronaBuffers() {
        testBufferOrdered(new UnsafeByteBuffer(java.nio.ByteBuffer.allocate(BUFFER_SIZE)));
        testBufferOrdered(new UnsafeByteBuffer(java.nio.ByteBuffer.allocateDirect(BUFFER_SIZE)));
        testBufferOrdered(new UnsafeByteBuffer(ByteBuffers.allocateByteArray(BUFFER_SIZE)));
        testBufferOrdered(
                new AgronaDelegateMutableByteBuffer(new UnsafeBuffer(java.nio.ByteBuffer.allocate(BUFFER_SIZE))));
        testBufferOrdered(
                new AgronaDelegateMutableByteBuffer(new UnsafeBuffer(java.nio.ByteBuffer.allocateDirect(BUFFER_SIZE))));
        testBufferOrdered(
                new AgronaDelegateMutableByteBuffer(new UnsafeBuffer(ByteBuffers.allocateByteArray(BUFFER_SIZE))));

        testBufferOrdered(new UninitializedDirectByteBuffer(BUFFER_SIZE));

        testBufferOrdered(new ArrayExpandableByteBuffer());
        testBufferOrdered(new AgronaDelegateMutableByteBuffer(new ExpandableArrayBuffer()));

        testBufferOrdered(new DirectExpandableByteBuffer());
        testBufferOrdered(new AgronaDelegateMutableByteBuffer(new ExpandableDirectByteBuffer()));
    }

    @Test
    public void testJavaBuffers() {
        testBufferOrdered(new NioDelegateByteBuffer(ByteBuffers.allocateByteArray(BUFFER_SIZE)));
        testBufferOrdered(new NioDelegateByteBuffer(java.nio.ByteBuffer.allocate(BUFFER_SIZE)));
        testBufferOrdered(new NioDelegateByteBuffer(java.nio.ByteBuffer.allocateDirect(BUFFER_SIZE)));
    }

    @Test
    public void testChronicleBuffers() {
        testBufferOrdered(new ChronicleDelegateByteBuffer(
                BytesStore.wrap(ByteBuffers.allocateByteArray(BUFFER_SIZE)).bytesForWrite()));
        testBufferOrdered(new ChronicleDelegateByteBuffer(
                BytesStore.wrap(java.nio.ByteBuffer.allocate(BUFFER_SIZE)).bytesForWrite()));
        testBufferOrdered(new ChronicleDelegateByteBuffer(
                BytesStore.wrap(java.nio.ByteBuffer.allocateDirect(BUFFER_SIZE)).bytesForWrite()));
        testBufferOrdered(new ChronicleDelegateByteBuffer(net.openhft.chronicle.bytes.Bytes.elasticByteBuffer()));
        testBufferOrdered(new ChronicleDelegateByteBuffer(
                net.openhft.chronicle.bytes.Bytes.allocateDirect(ByteBuffers.allocateByteArray(BUFFER_SIZE))));
        testBufferOrdered(
                new ChronicleDelegateByteBuffer(net.openhft.chronicle.bytes.Bytes.allocateDirect(BUFFER_SIZE)));
    }

    @Test
    public void testNettyBuffers() {
        testBufferOrdered(
                new NettyDelegateByteBuffer(Unpooled.wrappedBuffer(ByteBuffers.allocateByteArray(BUFFER_SIZE))));
        testBufferOrdered(
                new NettyDelegateByteBuffer(Unpooled.wrappedBuffer(java.nio.ByteBuffer.allocate(BUFFER_SIZE))));
        testBufferOrdered(
                new NettyDelegateByteBuffer(Unpooled.wrappedBuffer(java.nio.ByteBuffer.allocateDirect(BUFFER_SIZE))));
        testBufferOrdered(new NettyDelegateByteBuffer(Unpooled.buffer(BUFFER_SIZE)));
        testBufferOrdered(new NettyDelegateByteBuffer(Unpooled.directBuffer(BUFFER_SIZE)));
    }

    public void testBufferOrdered(final IByteBuffer buffer) {
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(buffer, ByteBuffers.NATIVE_ORDER));
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(buffer, ByteOrder.BIG_ENDIAN));
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(buffer, ByteOrder.LITTLE_ENDIAN));
    }

    private void testBuffer(final IByteBuffer b) {
        Assertions.assertThat(b.capacity()).isEqualTo(BUFFER_SIZE);
        Assertions.assertThat(b.remaining(BUFFER_SIZE - 200)).isEqualTo(200);

        testPrimitives(b);
        testPrimitivesReverse(b);
        testStringAscii(b);
        testStringUtf8(b);

        //        default void getBytes(final int index, final byte[] dst) {
        //            getBytesTo(index, dst, dst.length);
        //        }
        //
        //        void getBytes(final int index, final byte[] dst, final int dstIndex, final int length);
        //
        //        default void getBytes(final int index, final MutableDirectBuffer dstBuffer) {
        //            getBytesTo(index, dstBuffer, dstBuffer.capacity());
        //        }
        //
        //        void getBytes(int index, MutableDirectBuffer dstBuffer, int dstIndex, int length);
        //
        //        default void getBytes(final int index, final IByteBuffer dstBuffer) {
        //            getBytesTo(index, dstBuffer, dstBuffer.capacity());
        //        }
        //
        //        void getBytes(int index, IByteBuffer dstBuffer, int dstIndex, int length);
        //
        //        void getBytes(int index, IMemoryBuffer dstBuffer, long dstIndex, int length);
        //
        //        default void getBytes(final int index, final java.nio.ByteBuffer dstBuffer) {
        //            getBytesTo(index, dstBuffer, dstBuffer.remaining());
        //        }
        //
        //        void getBytes(int index, java.nio.ByteBuffer dstBuffer, int dstIndex, int length);
        //
        //        default void putBytes(final int index, final byte[] src) {
        //            putBytesTo(index, src, src.length);
        //        }
        //
        //
        //        void putBytes(int index, byte[] src, int srcIndex, int length);
        //
        //        default void putBytes(final int index, final java.nio.ByteBuffer srcBuffer) {
        //            putBytesTo(index, srcBuffer, srcBuffer.capacity());
        //        }
        //
        //        void putBytes(int index, java.nio.ByteBuffer srcBuffer, int srcIndex, int length);
        //
        //        default void putBytes(final int index, final DirectBuffer srcBuffer) {
        //            putBytesTo(index, srcBuffer, srcBuffer.capacity());
        //        }
        //
        //        void putBytes(int index, DirectBuffer srcBuffer, int srcIndex, int length);
        //
        //        default void putBytes(final int index, final IByteBuffer srcBuffer) {
        //            putBytesTo(index, srcBuffer, srcBuffer.capacity());
        //        }
        //
        //        void putBytes(int index, IByteBuffer srcBuffer, int srcIndex, int length);
        //
        //        void putBytes(int index, IMemoryBuffer srcBuffer, long srcIndex, int length);
        //
        //        InputStream asInputStream(int index, int length);
        //
        //        OutputStream asOutputStream(int index, int length);
        //
        //        /**
        //         * Might return the actual underlying array. Thus make sure to clone() it if the buffer is to be reused. Or just use
        //         * asByteArrayCopy instead to make sure a copy is returned always and clone() is not used redundantly.
        //         *
        //         * WARNING: be aware that expandable buffers might have a larger capacity than was was added to the buffer, thus
        //         * always prefer to use asByteArrayTo(length) instead of this capacity bounded version. Or make sure to only call
        //         * this method on buffers that have been slice(from, to)'d since that sets the capacity as a contraint to the
        //         * underlying actual backing array capacity.
        //         */
        //        default byte[] asByteArray() {
        //            return asByteArrayTo(capacity());
        //        }
        //
        //        /**
        //         * Might return the actual underlying array. Thus make sure to clone() it if the buffer is to be reused. Or just use
        //         * asByteArrayCopy instead to make sure a copy is returned always and clone() is not used redundantly.
        //         */
        //        byte[] asByteArray(int index, int length);
        //
        //        default java.nio.ByteBuffer asNioByteBuffer() {
        //            return asNioByteBufferTo(capacity());
        //        }
        //
        //        /**
        //         * Might return the actual underlying array. Thus make sure to clone() it if the buffer is to be reused. Or just use
        //         * asByteArrayCopy instead to make sure a copy is returned always and clone() is not used redundantly.
        //         */
        //        java.nio.ByteBuffer asNioByteBuffer(int index, int length);
        //
        //        /**
        //         * Always returns a new copy as a byte array regardless of the underlying storage.
        //         */
        //        byte[] asByteArrayCopy(int index, int length);
        //
        //        /**
        //         * Either returns the underlying array or copies the underlying storage into an array. Note that changes to the
        //         * array might or might not be reflected in the underlying storage.
        //         */
        //        MutableDirectBuffer asDirectBuffer(int index, int length);
        //
        //        /**
        //         * Either returns the underlying array or copies the underlying storage into an array. Note that changes to the
        //         * array might or might not be reflected in the underlying storage.
        //         */
        //        IMemoryBuffer asMemoryBuffer(int index, int length);
        //
        //        /**
        //         * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
        //         * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
        //         * multiple separate slices at the same it, it is better to call newSlice... instead.
        //         */
        //        IByteBuffer sliceFrom(int index);
        //
        //        /**
        //         * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
        //         * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
        //         * multiple separate slices at the same it, it is better to call newSlice... instead.
        //         */
        //        default IByteBuffer sliceTo(final int length) {
        //            return slice(0, length);
        //        }
        //
        //        /**
        //         * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
        //         * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
        //         * multiple separate slices at the same it, it is better to call newSlice... instead.
        //         */
        //        IByteBuffer slice(int index, int length);
        //
        //        /**
        //         * This always creates a new object for the slice, thus slice instances are not reused.
        //         */
        //        IByteBuffer newSliceFrom(int index);
        //
        //        /**
        //         * This always creates a new object for the slice, thus slice instances are not reused.
        //         */
        //        default IByteBuffer newSliceTo(final int length) {
        //            return newSlice(0, length);
        //        }
        //
        //        /**
        //         * This always creates a new object for the slice, thus slice instances are not reused.
        //         */
        //        IByteBuffer newSlice(int index, int length);
        //
        //
        //        default void getBytes(final int index, final DataOutputStream dst) throws IOException {
        //            getBytesTo(index, dst, capacity());
        //        }
        //
        //        default void getBytesTo(final int index, final DataOutputStream dst, final int length) throws IOException {
        //            getBytesTo(index, (OutputStream) dst, length);
        //        }
        //
        //        default void getBytes(final int index, final DataOutput dst) throws IOException {
        //            getBytesTo(index, dst, capacity());
        //        }
        //
        //        void getBytesTo(int index, DataOutput dst, int length) throws IOException;
        //
        //        default void getBytes(final int index, final OutputStream dst) throws IOException {
        //            getBytesTo(index, dst, remaining(index));
        //        }
        //
        //        void getBytesTo(int index, OutputStream dst, int length) throws IOException;
        //
        //        default void getBytes(final int index, final WritableByteChannel dst) throws IOException {
        //            getBytesTo(index, dst, remaining(index));
        //        }
        //
        //        default void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        //            OutputStreams.writeFully(dst, asNioByteBuffer(index, length));
        //        }
        //
        //        default void putBytes(final int index, final DataInputStream src) throws IOException {
        //            putBytesTo(index, src, remaining(index));
        //        }
        //
        //        default void putBytesTo(final int index, final DataInputStream src, final int length) throws IOException {
        //            putBytesTo(index, (InputStream) src, length);
        //        }
        //
        //        default void putBytes(final int index, final DataInput src) throws IOException {
        //            putBytesTo(index, src, remaining(index));
        //        }
        //
        //        void putBytesTo(int index, DataInput src, int length) throws IOException;
        //
        //        default void putBytes(final int index, final InputStream src) throws IOException {
        //            putBytesTo(index, src, remaining(index));
        //        }
        //
        //        void putBytesTo(int index, InputStream src, int length) throws IOException;
        //
        //        default void putBytes(final int index, final ReadableByteChannel src) throws IOException {
        //            putBytesTo(index, src, remaining(index));
        //        }
        //
        //        default void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        //            InputStreams.readFully(src, asNioByteBuffer(index, length));
        //        }
        //
        //        IByteBuffer clone(int index, int length);
        //
        //        void clear(byte value, int index, int length);
    }

    private void testStringAscii(final IByteBuffer b) {
        final String str = "asdfäöüjklm";

        int write = 200;
        b.putStringAscii(write, str);
        final int lengthAscii = ByteBuffers.newStringAsciiLength(str);
        write += lengthAscii;

        final int read = 200;
        final String strRead = b.getStringAscii(read, lengthAscii);

        Assertions.checkEquals("asdf???jklm", strRead);

        final StringBuilder appendable = new StringBuilder();
        b.getStringAscii(read, lengthAscii, appendable);
        Assertions.checkEquals(str, appendable.toString());
    }

    private void testStringUtf8(final IByteBuffer b) {
        final String str = "asdfäöüjklm";

        int write = 200;
        final int lengthUtf8 = b.putStringUtf8(write, str);
        write += lengthUtf8;

        final int read = 200;
        final String strRead = b.getStringUtf8(read, lengthUtf8);

        Assertions.checkEquals(str, strRead);

        final StringBuilder appendable = new StringBuilder();
        b.getStringUtf8(read, lengthUtf8, appendable);
        Assertions.checkEquals(str, appendable.toString());
    }

    private void testPrimitives(final IByteBuffer b) {
        int write = 200;
        b.putBoolean(write, true);
        write += Booleans.BYTES;
        b.putByte(write, (byte) 2);
        write += Byte.BYTES;
        b.putChar(write, 'A');
        write += Character.BYTES;
        b.putShort(write, Short.MAX_VALUE);
        write += Short.BYTES;
        b.putInt(write, Integer.MIN_VALUE);
        write += Integer.BYTES;
        b.putLong(write, Long.MAX_VALUE);
        write += Long.BYTES;
        b.putFloat(write, Float.MIN_VALUE);
        write += Float.SIZE;
        b.putDouble(write, Double.MAX_VALUE);
        write += Double.SIZE;

        int read = 200;
        Assertions.checkEquals(true, b.getBoolean(read));
        read += Booleans.BYTES;
        Assertions.checkEquals((byte) 2, b.getByte(read));
        read += Byte.BYTES;
        Assertions.checkEquals('A', b.getChar(read));
        read += Character.BYTES;
        Assertions.checkEquals(Short.MAX_VALUE, b.getShort(read));
        read += Short.BYTES;
        Assertions.checkEquals(Integer.MIN_VALUE, b.getInt(read));
        read += Integer.BYTES;
        Assertions.checkEquals(Long.MAX_VALUE, b.getLong(read));
        read += Long.BYTES;
        Assertions.checkEquals(Float.MIN_VALUE, b.getFloat(read));
        read += Float.SIZE;
        Assertions.checkEquals(Double.MAX_VALUE, b.getDouble(read));
        read += Double.SIZE;
    }

    private void testPrimitivesReverse(final IByteBuffer b) {
        int write = 200;
        b.putCharReverse(write, 'A');
        write += Character.BYTES;
        b.putShortReverse(write, Short.MAX_VALUE);
        write += Short.BYTES;
        b.putIntReverse(write, Integer.MIN_VALUE);
        write += Integer.BYTES;
        b.putLongReverse(write, Long.MAX_VALUE);
        write += Long.BYTES;
        b.putFloatReverse(write, Float.MIN_VALUE);
        write += Float.SIZE;
        b.putDoubleReverse(write, Double.MAX_VALUE);
        write += Double.SIZE;

        int read = 200;
        Assertions.checkEquals('A', b.getCharReverse(read));
        read += Character.BYTES;
        Assertions.checkEquals(Short.MAX_VALUE, b.getShortReverse(read));
        read += Short.BYTES;
        Assertions.checkEquals(Integer.MIN_VALUE, b.getIntReverse(read));
        read += Integer.BYTES;
        Assertions.checkEquals(Long.MAX_VALUE, b.getLongReverse(read));
        read += Long.BYTES;
        Assertions.checkEquals(Float.MIN_VALUE, b.getFloatReverse(read));
        read += Float.SIZE;
        Assertions.checkEquals(Double.MAX_VALUE, b.getDoubleReverse(read));
        read += Double.SIZE;
    }

}
