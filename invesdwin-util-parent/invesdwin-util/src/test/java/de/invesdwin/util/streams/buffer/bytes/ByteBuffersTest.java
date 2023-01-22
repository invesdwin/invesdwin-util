package de.invesdwin.util.streams.buffer.bytes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.ExpandableArrayBuffer;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGenerators;
import de.invesdwin.util.streams.DelegateDataInput;
import de.invesdwin.util.streams.DelegateDataOutput;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.delegate.AgronaDelegateMutableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.ChronicleDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.ListByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.NettyDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.NioDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.OrderedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.ArrayExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.internal.DirectExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.internal.UninitializedDirectByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
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
    public void testAgronaBuffers() throws IOException {
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
    public void testJavaBuffers() throws IOException {
        testBufferOrdered(new NioDelegateByteBuffer(ByteBuffers.allocateByteArray(BUFFER_SIZE)));
        testBufferOrdered(new NioDelegateByteBuffer(java.nio.ByteBuffer.allocate(BUFFER_SIZE)));
        testBufferOrdered(new NioDelegateByteBuffer(java.nio.ByteBuffer.allocateDirect(BUFFER_SIZE)));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testChronicleBuffers() throws IllegalStateException, IOException {
        final net.openhft.chronicle.bytes.VanillaBytes direct = net.openhft.chronicle.bytes.Bytes
                .allocateDirect(ByteBuffers.allocateByteArray(BUFFER_SIZE));
        direct.writePosition(0);
        testBufferOrdered(new ChronicleDelegateByteBuffer(direct));
        testBufferOrdered(new ChronicleDelegateByteBuffer(
                BytesStore.wrap(ByteBuffers.allocateByteArray(BUFFER_SIZE)).bytesForWrite()));
        testBufferOrdered(new ChronicleDelegateByteBuffer(
                BytesStore.wrap(java.nio.ByteBuffer.allocate(BUFFER_SIZE)).bytesForWrite()));
        testBufferOrdered(new ChronicleDelegateByteBuffer(
                BytesStore.wrap(java.nio.ByteBuffer.allocateDirect(BUFFER_SIZE)).bytesForWrite()));
        testBufferOrdered(new ChronicleDelegateByteBuffer(net.openhft.chronicle.bytes.Bytes.elasticByteBuffer()));
        testBufferOrdered(
                new ChronicleDelegateByteBuffer(net.openhft.chronicle.bytes.Bytes.allocateDirect(BUFFER_SIZE)));
    }

    @Test
    public void testNettyBuffers() throws IOException {
        testBufferOrdered(
                new NettyDelegateByteBuffer(Unpooled.wrappedBuffer(ByteBuffers.allocateByteArray(BUFFER_SIZE))));
        testBufferOrdered(
                new NettyDelegateByteBuffer(Unpooled.wrappedBuffer(java.nio.ByteBuffer.allocate(BUFFER_SIZE))));
        testBufferOrdered(
                new NettyDelegateByteBuffer(Unpooled.wrappedBuffer(java.nio.ByteBuffer.allocateDirect(BUFFER_SIZE))));
        testBufferOrdered(new NettyDelegateByteBuffer(Unpooled.buffer(BUFFER_SIZE)));
        testBufferOrdered(new NettyDelegateByteBuffer(Unpooled.directBuffer(BUFFER_SIZE)));
    }

    @Test
    public void testByteBuffers() throws IOException {
        testBufferOrdered(ByteBuffers.allocate(BUFFER_SIZE));
        testBufferOrdered(ByteBuffers.allocateDirect(BUFFER_SIZE));
        testBufferOrdered(ByteBuffers.allocateExpandable(BUFFER_SIZE));
        testBufferOrdered(ByteBuffers.allocateDirectExpandable(BUFFER_SIZE));
        testBufferOrdered(ByteBuffers.allocateExpandable());
        testBufferOrdered(ByteBuffers.allocateDirectExpandable());
    }

    @Test
    public void testListBuffer() throws IOException {
        for (int chunkSize = 1; chunkSize <= 20; chunkSize++) {
            System.out.println(chunkSize);
            final ListByteBuffer buffer = new ListByteBuffer();
            for (int i = 0; i < BUFFER_SIZE; i += chunkSize) {
                buffer.getList().add(ByteBuffers.allocate(chunkSize));
            }
            testBufferOrdered(buffer);
        }
    }

    public void testBufferOrdered(final IByteBuffer buffer) throws IOException {
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(buffer, ByteBuffers.NATIVE_ORDER));
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(buffer, ByteOrder.BIG_ENDIAN));
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(buffer, ByteOrder.LITTLE_ENDIAN));
    }

    private void testBuffer(final IByteBuffer b) throws IOException {
        testPrimitivesStream(b);
        b.clear();
        testPrimitives(b);
        b.clear();
        testPrimitivesReverse(b);
        b.clear();
        testStringAscii(b);
        b.clear();
        testStringUtf8(b);
        b.clear();

        if (b.isExpandable()) {
            b.ensureCapacity(BUFFER_SIZE);
            Assertions.assertThat(b.capacity()).isGreaterThanOrEqualTo(BUFFER_SIZE);
            Assertions.assertThat(b.remaining(b.capacity() - 200)).isEqualTo(200);
        } else {
            b.ensureCapacity(BUFFER_SIZE);
            Assertions.assertThat(b.capacity()).isEqualTo(BUFFER_SIZE);
            Assertions.assertThat(b.remaining(BUFFER_SIZE - 200)).isEqualTo(200);
        }
        testCopy(b);
    }

    private void testCopy(final IByteBuffer b) throws IOException, FileNotFoundException {
        final int capacity = b.capacity();
        final byte[] zero = new byte[capacity];
        final byte[] random = new byte[capacity];
        final byte[] get = new byte[capacity];
        final IRandomGenerator r = PseudoRandomGenerators.getThreadLocalPseudoRandom();
        r.nextBytes(random);
        b.clear();
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, zero));
        b.putBytes(0, random);
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
        b.getBytes(0, get);
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(get, random));
        final IByteBuffer clone = b.clone(0, b.capacity());
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(clone, random));
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(b.asByteArray(0, b.capacity()), random));
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(b.asByteArrayCopy(0, b.capacity()), random));
        java.nio.ByteBuffer nioBuffer = clone.asNioByteBuffer();
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(ByteBuffers.wrap(nioBuffer), random));
        MutableDirectBuffer directBuffer = clone.asDirectBuffer();
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(ByteBuffers.wrap(directBuffer), random));
        IMemoryBuffer memoryBuffer = clone.asMemoryBuffer();
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(memoryBuffer.asByteArrayCopy(0, capacity), random));
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(b.slice(0, capacity), random));
        Assertions.checkTrue(ByteBuffers.constantTimeEquals(b.newSlice(0, capacity), random));

        final File file = File.createTempFile("asdf", "ghjk");
        try {
            //asInputStream/asOutputStream
            try (FileOutputStream fos = new FileOutputStream(file)) {
                IOUtils.copy(b.asInputStream(), fos);
            }
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, zero));
            try (FileInputStream fis = new FileInputStream(file)) {
                IOUtils.copy(fis, b.asOutputStream());
            }
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));

            //InputStream/OutputStream
            try (FileOutputStream fos = new FileOutputStream(file)) {
                b.getBytes(0, fos);
            }
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, zero));
            try (FileInputStream fis = new FileInputStream(file)) {
                b.putBytes(0, fis);
            }
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));

            //ReadableByteChannel/WritableByteChannel
            try (FileOutputStream fos = new FileOutputStream(file)) {
                b.getBytes(0, fos.getChannel());
            }
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, zero));
            try (FileInputStream fis = new FileInputStream(file)) {
                b.putBytes(0, fis.getChannel());
            }
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));

            //DataInputStream/DataOutputStream
            try (FileOutputStream fos = new FileOutputStream(file)) {
                b.getBytes(0, new DataOutputStream(fos));
            }
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, zero));
            try (FileInputStream fis = new FileInputStream(file)) {
                b.putBytes(0, new DataInputStream(fis));
            }
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));

            //DirectBuffer
            clone.clear();
            directBuffer = clone.asDirectBuffer();
            b.getBytes(0, directBuffer);
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, zero));
            b.putBytes(0, directBuffer);
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));

            //NioBuffer
            clone.clear();
            nioBuffer = clone.asNioByteBuffer();
            b.getBytes(0, nioBuffer);
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, zero));
            b.putBytes(0, nioBuffer);
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));

            //MemoryBuffer
            clone.clear();
            memoryBuffer = clone.asMemoryBuffer();
            b.getBytes(0, memoryBuffer, 0, capacity);
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, zero));
            b.putBytes(0, memoryBuffer, 0, capacity);
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));

            //ByteBuffer
            clone.clear();
            b.getBytes(0, clone);
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, zero));
            b.putBytes(0, clone);
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));

            //DataInput/DataOutput
            try (FileOutputStream fos = new FileOutputStream(file)) {
                b.getBytes(0, new DelegateDataOutput(new DataOutputStream(fos)));
            }
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, zero));
            try (FileInputStream fis = new FileInputStream(file)) {
                b.putBytes(0, new DelegateDataInput(new DataInputStream(fis)));
            }
            Assertions.checkTrue(ByteBuffers.constantTimeEquals(b, random));
        } finally {
            Files.deleteQuietly(file);
        }

        Assertions.checkEquals(capacity, b.capacity());
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

        final StringBuilder appendable = new StringBuilder();
        b.getStringAsciii(read, lengthAscii, appendable);
        Assertions.checkEquals("asdf???jklm", appendable.toString());
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
        write += Float.BYTES;
        b.putDouble(write, Double.MAX_VALUE);
        write += Double.BYTES;

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
        read += Float.BYTES;
        Assertions.checkEquals(Double.MAX_VALUE, b.getDouble(read));
        read += Double.BYTES;
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
        write += Float.BYTES;
        b.putDoubleReverse(write, Double.MAX_VALUE);
        write += Double.BYTES;

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
        read += Float.BYTES;
        Assertions.checkEquals(Double.MAX_VALUE, b.getDoubleReverse(read));
        read += Double.BYTES;
    }

    private void testPrimitivesStream(final IByteBuffer b) throws IOException {
        final int write = 200;
        try (OutputStream out = b.asOutputStreamFrom(write)) {
            OutputStreams.writeBoolean(out, true);
            OutputStreams.write(out, (byte) 2);
            OutputStreams.writeChar(out, 'A');
            OutputStreams.writeShort(out, Short.MAX_VALUE);
            OutputStreams.writeInt(out, Integer.MIN_VALUE);
            OutputStreams.writeLong(out, Long.MAX_VALUE);
            OutputStreams.writeFloat(out, Float.MIN_VALUE);
            OutputStreams.writeDouble(out, Double.MAX_VALUE);
        }

        int read = 200;
        try (InputStream in = b.asInputStreamFrom(read)) {
            Assertions.checkEquals(true, InputStreams.readBoolean(in));
            Assertions.checkEquals(true, b.getBoolean(read));
            read += Booleans.BYTES;
            Assertions.checkEquals((byte) 2, InputStreams.readByte(in));
            Assertions.checkEquals((byte) 2, b.getByte(read));
            read += Byte.BYTES;
            Assertions.checkEquals('A', InputStreams.readChar(in));
            if (b.getOrder() == ByteOrder.BIG_ENDIAN) {
                Assertions.checkEquals('A', b.getChar(read));
            } else {
                Assertions.checkEquals('A', b.getCharReverse(read));
            }
            read += Character.BYTES;
            Assertions.checkEquals(Short.MAX_VALUE, InputStreams.readShort(in));
            if (b.getOrder() == ByteOrder.BIG_ENDIAN) {
                Assertions.checkEquals(Short.MAX_VALUE, b.getShort(read));
            } else {
                Assertions.checkEquals(Short.MAX_VALUE, b.getShortReverse(read));
            }
            read += Short.BYTES;
            Assertions.checkEquals(Integer.MIN_VALUE, InputStreams.readInt(in));
            if (b.getOrder() == ByteOrder.BIG_ENDIAN) {
                Assertions.checkEquals(Integer.MIN_VALUE, b.getInt(read));
            } else {
                Assertions.checkEquals(Integer.MIN_VALUE, b.getIntReverse(read));
            }
            read += Integer.BYTES;
            Assertions.checkEquals(Long.MAX_VALUE, InputStreams.readLong(in));
            if (b.getOrder() == ByteOrder.BIG_ENDIAN) {
                Assertions.checkEquals(Long.MAX_VALUE, b.getLong(read));
            } else {
                Assertions.checkEquals(Long.MAX_VALUE, b.getLongReverse(read));
            }
            read += Long.BYTES;
            Assertions.checkEquals(Float.MIN_VALUE, InputStreams.readFloat(in));
            if (b.getOrder() == ByteOrder.BIG_ENDIAN) {
                Assertions.checkEquals(Float.MIN_VALUE, b.getFloat(read));
            } else {
                Assertions.checkEquals(Float.MIN_VALUE, b.getFloatReverse(read));
            }
            read += Float.BYTES;
            Assertions.checkEquals(Double.MAX_VALUE, InputStreams.readDouble(in));
            if (b.getOrder() == ByteOrder.BIG_ENDIAN) {
                Assertions.checkEquals(Double.MAX_VALUE, b.getDouble(read));
            } else {
                Assertions.checkEquals(Double.MAX_VALUE, b.getDoubleReverse(read));
            }
            read += Double.BYTES;
        }
    }

}
