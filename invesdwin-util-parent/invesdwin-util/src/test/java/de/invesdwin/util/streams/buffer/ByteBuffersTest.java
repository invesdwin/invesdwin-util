package de.invesdwin.util.streams.buffer;

import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.ExpandableArrayBuffer;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.streams.buffer.delegate.AgronaDelegateMutableByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.ChronicleDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.JavaDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.OrderedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.extend.ArrayExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.extend.DirectExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.extend.internal.UninitializedDirectByteBuffer;
import net.openhft.chronicle.bytes.BytesStore;

@NotThreadSafe
public class ByteBuffersTest {

    private static final int BUFFER_SIZE = 10000;

    static {
        //java 16 requires --illegal-access=permit --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED --add-opens java.base/java.nioc=ALL-UNNAMED
        //CHECKSTYLE:OFF
        System.setProperty("io.netty.tryReflectionSetAccessible", "true");
        System.setProperty("io.netty.uninitializedArrayAllocationThreshold", "1");
        //CHECKSTYLE:ON
    }

    @Test
    public void testBuffers() {
        testBufferOrdered(new JavaDelegateByteBuffer(ByteBuffers.allocateByteArray(BUFFER_SIZE)));
        testBufferOrdered(new JavaDelegateByteBuffer(java.nio.ByteBuffer.allocate(BUFFER_SIZE)));
        testBufferOrdered(new JavaDelegateByteBuffer(java.nio.ByteBuffer.allocateDirect(BUFFER_SIZE)));

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

    public void testBufferOrdered(final IByteBuffer buffer) {
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(buffer, ByteBuffers.NATIVE_ORDER));
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(buffer, ByteOrder.BIG_ENDIAN));
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(buffer, ByteOrder.LITTLE_ENDIAN));
    }

    private void testBuffer(final IByteBuffer b) {
        testPrimitives(b);
        testStringAscii(b);
        testStringUtf8(b);
    }

    private void testStringAscii(final IByteBuffer b) {
        final String str = "asdfäöüjklm";

        int write = 200;
        b.putStringAsciii(write, str);
        final int lengthAscii = ByteBuffers.newStringAsciiLength(str);
        write += lengthAscii;

        final int read = 200;
        final String strRead = b.getStringAsciii(read, lengthAscii);

        Assertions.checkEquals("asdf???jklm", strRead);
    }

    private void testStringUtf8(final IByteBuffer b) {
        final String str = "asdfäöüjklm";

        int write = 200;
        final int lengthUtf8 = b.putStringUtf8(write, str);
        write += lengthUtf8;

        final int read = 200;
        final String strRead = b.getStringUtf8(read, lengthUtf8);

        Assertions.checkEquals(str, strRead);
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

}
