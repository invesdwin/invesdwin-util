package de.invesdwin.util.lang.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.ExpandableArrayBuffer;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.buffer.delegate.AgronaDelegateByteBuffer;
import de.invesdwin.util.lang.buffer.delegate.JavaDelegateByteBuffer;
import de.invesdwin.util.lang.buffer.delegate.OrderedDelegateByteBuffer;
import de.invesdwin.util.lang.buffer.extend.ExpandableArrayByteBuffer;
import de.invesdwin.util.lang.buffer.extend.ExpandableByteBuffer;
import de.invesdwin.util.lang.buffer.extend.UnsafeByteBuffer;
import de.invesdwin.util.math.Booleans;

@NotThreadSafe
public class ByteBuffersTest {

    private static final int BUFFER_SIZE = 10000;

    @Test
    public void testBuffers() {
        testBufferOrdered(new JavaDelegateByteBuffer(new byte[BUFFER_SIZE]));
        testBufferOrdered(new JavaDelegateByteBuffer(ByteBuffer.allocate(BUFFER_SIZE)));
        testBufferOrdered(new JavaDelegateByteBuffer(ByteBuffer.allocateDirect(BUFFER_SIZE)));

        testBufferOrdered(new UnsafeByteBuffer(ByteBuffer.allocate(BUFFER_SIZE)));
        testBufferOrdered(new UnsafeByteBuffer(ByteBuffer.allocateDirect(BUFFER_SIZE)));
        testBufferOrdered(new UnsafeByteBuffer(new byte[BUFFER_SIZE]));
        testBufferOrdered(new AgronaDelegateByteBuffer(new UnsafeBuffer(ByteBuffer.allocate(BUFFER_SIZE))));
        testBufferOrdered(new AgronaDelegateByteBuffer(new UnsafeBuffer(ByteBuffer.allocateDirect(BUFFER_SIZE))));
        testBufferOrdered(new AgronaDelegateByteBuffer(new UnsafeBuffer(new byte[BUFFER_SIZE])));

        testBufferOrdered(new ExpandableArrayByteBuffer());
        testBufferOrdered(new AgronaDelegateByteBuffer(new ExpandableArrayBuffer()));

        testBufferOrdered(new ExpandableByteBuffer());
        testBufferOrdered(new AgronaDelegateByteBuffer(new ExpandableDirectByteBuffer()));
    }

    public void testBufferOrdered(final IByteBuffer buffer) {
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(ByteBuffers.NATIVE_ORDER, buffer));
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(ByteOrder.BIG_ENDIAN, buffer));
        testBuffer(OrderedDelegateByteBuffer.maybeWrap(ByteOrder.LITTLE_ENDIAN, buffer));
    }

    private void testBuffer(final IByteBuffer b) {
        Assertions.checkTrue(b.addressOffset() != 0L);
        testPrimitives(b);
        //        testStringUtf8(b);
    }

    private void testStringUtf8(final IByteBuffer b) {
        final int write = 100;
        b.putStringUtf8(100, null);
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
