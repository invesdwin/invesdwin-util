package de.invesdwin.util.streams.buffer.memory;

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

import org.agrona.MutableDirectBuffer;
import org.apache.arrow.memory.ArrowBuf;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGenerators;
import de.invesdwin.util.streams.DelegateDataInput;
import de.invesdwin.util.streams.DelegateDataOutput;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ArrowDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ChronicleDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ListMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.OrderedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.UnsafeMemoryBuffer;
import de.invesdwin.util.time.Instant;
import net.openhft.chronicle.bytes.BytesStore;

@NotThreadSafe
public class MemoryBuffersTest {

    private static final int BUFFER_SIZE = 1000;

    static {
        //CHECKSTYLE:OFF
        System.setProperty("io.netty.tryReflectionSetAccessible", "true");
        System.setProperty("io.netty.uninitializedArrayAllocationThreshold", "1");
        //CHECKSTYLE:ON
        //java 16 otherwise requires --illegal-access=permit --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED
        Reflections.disableJavaModuleSystemRestrictions();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testChronicleBuffers() throws IllegalStateException, IOException {
        final net.openhft.chronicle.bytes.VanillaBytes direct = net.openhft.chronicle.bytes.Bytes
                .allocateDirect(ByteBuffers.allocateByteArray(BUFFER_SIZE));
        direct.writePosition(0);
        testBufferOrdered(new ChronicleDelegateMemoryBuffer(direct));
        testBufferOrdered(new ChronicleDelegateMemoryBuffer(
                BytesStore.wrap(ByteBuffers.allocateByteArray(BUFFER_SIZE)).bytesForWrite()));
        testBufferOrdered(new ChronicleDelegateMemoryBuffer(
                BytesStore.wrap(java.nio.ByteBuffer.allocate(BUFFER_SIZE)).bytesForWrite()));
        testBufferOrdered(new ChronicleDelegateMemoryBuffer(
                BytesStore.wrap(java.nio.ByteBuffer.allocateDirect(BUFFER_SIZE)).bytesForWrite()));
        testBufferOrdered(new ChronicleDelegateMemoryBuffer(net.openhft.chronicle.bytes.Bytes.elasticByteBuffer()));
        testBufferOrdered(
                new ChronicleDelegateMemoryBuffer(net.openhft.chronicle.bytes.Bytes.allocateDirect(BUFFER_SIZE)));
    }

    @Test
    public void testArrowBuffers() throws IOException {
        try (BufferAllocator bufferAllocator = new RootAllocator(8 * 1024)) {
            try (ArrowBuf arrowBuf = bufferAllocator.buffer(BUFFER_SIZE)) {
                testBufferOrdered(new ArrowDelegateMemoryBuffer(arrowBuf));
            }
        }
    }

    @Test
    public void testMemoryBuffers() throws IOException {
        testBufferOrdered(new UnsafeMemoryBuffer(ByteBuffers.allocate(BUFFER_SIZE).byteArray()));
        testBufferOrdered(new UnsafeMemoryBuffer(ByteBuffers.allocateDirect(BUFFER_SIZE).nioByteBuffer()));
    }

    @Test
    public void testListBuffer() throws IOException {
        for (int chunkSize = 1; chunkSize <= 20; chunkSize++) {
            //CHECKSTYLE:OFF
            System.out.println("ListByteBuffer[" + chunkSize + "]");
            //CHECKSTYLE:ON
            final Instant start = new Instant();
            final ListMemoryBuffer buffer = new ListMemoryBuffer();
            for (int i = 0; i < BUFFER_SIZE; i += chunkSize) {
                buffer.getList().add(new UnsafeMemoryBuffer(ByteBuffers.allocate(chunkSize).byteArray()));
            }
            testBufferOrdered(buffer);
            //CHECKSTYLE:OFF
            System.out.println("ListByteBuffer[" + chunkSize + "] finished after " + start);
            //CHECKSTYLE:ON
        }
    }

    public void testBufferOrdered(final IMemoryBuffer buffer) throws IOException {
        testBuffer(OrderedDelegateMemoryBuffer.maybeWrap(buffer, ByteBuffers.NATIVE_ORDER));
        testBuffer(OrderedDelegateMemoryBuffer.maybeWrap(buffer, ByteOrder.BIG_ENDIAN));
        testBuffer(OrderedDelegateMemoryBuffer.maybeWrap(buffer, ByteOrder.LITTLE_ENDIAN));
    }

    private void testBuffer(final IMemoryBuffer b) throws IOException {
        testPrimitives(b);
        b.clear();
        testPrimitivesReverse(b);
        b.clear();
        testStringAscii(b);
        b.clear();
        testStringUtf8(b);
        b.clear();
        testPrimitivesStream(b);
        b.clear();

        b.ensureCapacity(BUFFER_SIZE);
        Assertions.assertThat(b.capacity()).isGreaterThanOrEqualTo(BUFFER_SIZE);
        Assertions.assertThat(b.remaining(b.capacity() - 200)).isEqualTo(200);
        testCopy(b);
    }

    private void testCopy(final IMemoryBuffer b) throws IOException, FileNotFoundException {
        final int capacity = Integers.checkedCast(b.capacity());
        final byte[] zero = new byte[capacity];
        final byte[] random = new byte[capacity];
        final byte[] get = new byte[capacity];
        final IRandomGenerator r = PseudoRandomGenerators.getThreadLocalPseudoRandom();
        r.nextBytes(random);
        b.clear();
        Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, zero));
        b.putBytes(0, random);
        Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
        b.getBytes(0, get);
        Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
        Assertions.checkTrue(MemoryBuffers.constantTimeEquals(get, random));
        final IMemoryBuffer clone = b.clone(0, Integers.checkedCast(b.capacity()));
        Assertions.checkTrue(MemoryBuffers.constantTimeEquals(clone, random));
        Assertions.checkTrue(
                MemoryBuffers.constantTimeEquals(b.asByteArrayCopy(0, Integers.checkedCast(b.capacity())), random));
        java.nio.ByteBuffer nioBuffer = clone.asNioByteBuffer(0, Integers.checkedCast(b.capacity()));
        Assertions.checkTrue(MemoryBuffers.constantTimeEquals(MemoryBuffers.wrap(nioBuffer), random));
        MutableDirectBuffer directBuffer = clone.asDirectBuffer(0, Integers.checkedCast(b.capacity()));
        Assertions.checkTrue(MemoryBuffers.constantTimeEquals(MemoryBuffers.wrap(directBuffer), random));
        IByteBuffer byteBuffer = clone.asByteBuffer(0, Integers.checkedCast(b.capacity()));
        Assertions.checkTrue(MemoryBuffers.constantTimeEquals(byteBuffer.asByteArrayCopy(0, capacity), random));
        Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b.slice(0, capacity), random));
        Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b.newSlice(0, capacity), random));

        final File file = File.createTempFile("asdf", "ghjk");
        try {
            //asInputStream/asOutputStream
            try (FileOutputStream fos = new FileOutputStream(file)) {
                IOUtils.copy(b.asInputStream(), fos);
            }
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, zero));
            try (FileInputStream fis = new FileInputStream(file)) {
                IOUtils.copy(fis, b.asOutputStream());
            }
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));

            //InputStream/OutputStream
            try (FileOutputStream fos = new FileOutputStream(file)) {
                b.getBytes(0, fos);
            }
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, zero));
            try (FileInputStream fis = new FileInputStream(file)) {
                b.putBytes(0, fis);
            }
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));

            //ReadableByteChannel/WritableByteChannel
            try (FileOutputStream fos = new FileOutputStream(file)) {
                b.getBytes(0, fos.getChannel());
            }
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, zero));
            try (FileInputStream fis = new FileInputStream(file)) {
                b.putBytes(0, fis.getChannel());
            }
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));

            //DataInputStream/DataOutputStream
            try (FileOutputStream fos = new FileOutputStream(file)) {
                b.getBytes(0, new DataOutputStream(fos));
            }
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, zero));
            try (FileInputStream fis = new FileInputStream(file)) {
                b.putBytes(0, new DataInputStream(fis));
            }
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));

            //DirectBuffer
            clone.clear();
            directBuffer = clone.asDirectBuffer(0, Integers.checkedCast(b.capacity()));
            b.getBytes(0, directBuffer);
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, zero));
            b.putBytes(0, directBuffer);
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));

            //NioBuffer
            clone.clear();
            nioBuffer = clone.asNioByteBuffer(0, Integers.checkedCast(b.capacity()));
            b.getBytes(0, nioBuffer);
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, zero));
            b.putBytes(0, nioBuffer);
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));

            //MemoryBuffer
            clone.clear();
            byteBuffer = clone.asByteBuffer(0, Integers.checkedCast(b.capacity()));
            b.getBytes(0, byteBuffer, 0, capacity);
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, zero));
            b.putBytes(0, byteBuffer, 0, capacity);
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));

            //ByteBuffer
            clone.clear();
            b.getBytes(0, clone);
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, zero));
            b.putBytes(0, clone);
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));

            //DataInput/DataOutput
            try (FileOutputStream fos = new FileOutputStream(file)) {
                b.getBytes(0, new DelegateDataOutput(new DataOutputStream(fos)));
            }
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
            b.clear();
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, zero));
            try (FileInputStream fis = new FileInputStream(file)) {
                b.putBytes(0, new DelegateDataInput(new DataInputStream(fis)));
            }
            Assertions.checkTrue(MemoryBuffers.constantTimeEquals(b, random));
        } finally {
            Files.deleteQuietly(file);
        }

        Assertions.checkEquals(capacity, b.capacity());
    }

    private void testStringAscii(final IMemoryBuffer b) {
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

    private void testStringUtf8(final IMemoryBuffer b) {
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

    private void testPrimitives(final IMemoryBuffer b) {
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

    private void testPrimitivesReverse(final IMemoryBuffer b) {
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

    private void testPrimitivesStream(final IMemoryBuffer b) throws IOException {
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
