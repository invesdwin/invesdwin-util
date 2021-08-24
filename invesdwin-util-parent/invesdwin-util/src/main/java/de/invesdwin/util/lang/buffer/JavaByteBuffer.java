package de.invesdwin.util.lang.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.io.DirectBufferInputStream;
import org.agrona.io.DirectBufferOutputStream;

import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public class JavaByteBuffer implements IByteBuffer {

    private final ByteBuffer buffer;
    private UnsafeBuffer directBuffer;

    public JavaByteBuffer(final ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.NATIVE_ORDER;
    }

    @Override
    public long addressOffset() {
        if (directBuffer != null) {
            return directBuffer.addressOffset();
        } else if (buffer.isDirect()) {
            return BufferUtil.address(buffer);
        } else {
            return BufferUtil.ARRAY_BASE_OFFSET + BufferUtil.arrayOffset(buffer);
        }
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        if (directBuffer == null) {
            directBuffer = new UnsafeBuffer(buffer);
        }
        return directBuffer;
    }

    @Override
    public byte[] byteArray() {
        if (buffer.hasArray()) {
            return buffer.array();
        } else {
            return null;
        }
    }

    @Override
    public ByteBuffer byteBuffer() {
        return buffer;
    }

    @Override
    public int capacity() {
        return buffer.capacity();
    }

    @Override
    public long getLong(final int index) {
        return buffer.getLong(index);
    }

    @Override
    public int getInt(final int index) {
        return buffer.getInt(index);
    }

    @Override
    public double getDouble(final int index) {
        return buffer.getDouble(index);
    }

    @Override
    public float getFloat(final int index) {
        return buffer.getFloat(index);
    }

    @Override
    public short getShort(final int index) {
        return buffer.getShort(index);
    }

    @Override
    public char getChar(final int index) {
        return buffer.getChar(index);
    }

    @Override
    public byte getByte(final int index) {
        return buffer.get(index);
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        buffer.get(index, dst, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        dstBuffer.putBytes(dstIndex, buffer, index, length);
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        dstBuffer.putBytes(dstIndex, buffer, index, length);
    }

    @Override
    public void getBytes(final int index, final ByteBuffer dstBuffer, final int dstIndex, final int length) {
        directBuffer().getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return directBuffer();
        } else {
            return new UnsafeBuffer(buffer, index, length);
        }
    }

    @Override
    public int wrapAdjustment() {
        final long offset = buffer.hasArray() ? BufferUtil.ARRAY_BASE_OFFSET : BufferUtil.address(buffer);
        return (int) (addressOffset() - offset);
    }

    @Override
    public boolean isExpandable() {
        return false;
    }

    @Override
    public int putLong(final int index, final long value) {
        buffer.putLong(index, value);
        return Long.BYTES;
    }

    @Override
    public int putInt(final int index, final int value) {
        buffer.putInt(index, value);
        return Integer.BYTES;
    }

    @Override
    public int putDouble(final int index, final double value) {
        buffer.putDouble(index, value);
        return Double.BYTES;
    }

    @Override
    public int putFloat(final int index, final float value) {
        buffer.putFloat(index, value);
        return Float.BYTES;
    }

    @Override
    public int putShort(final int index, final short value) {
        buffer.putShort(index, value);
        return Short.BYTES;
    }

    @Override
    public int putChar(final int index, final char value) {
        buffer.putChar(index, value);
        return Character.BYTES;
    }

    @Override
    public int putByte(final int index, final byte value) {
        buffer.put(index, value);
        return Byte.BYTES;
    }

    @Override
    public int putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        buffer.put(index, src, srcIndex, length);
        return length;
    }

    @Override
    public int putBytes(final int index, final ByteBuffer srcBuffer, final int srcIndex, final int length) {
        directBuffer().putBytes(index, srcBuffer, srcIndex, length);
        return length;
    }

    @Override
    public int putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        srcBuffer.getBytes(srcIndex, buffer, index, length);
        return length;
    }

    @Override
    public int putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        srcBuffer.getBytes(srcIndex, buffer, index, length);
        return length;
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return new DirectBufferInputStream(directBuffer(), index, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return new DirectBufferOutputStream(directBuffer(), index, length);
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        if (wrapAdjustment() == 0 && index == 0 && length == capacity()) {
            final byte[] bytes = byteArray();
            if (bytes != null) {
                return bytes;
            }
        }
        final byte[] bytes = new byte[length];
        buffer.get(index, bytes, 0, length);
        return bytes;
    }

    @Override
    public IByteBuffer slice(final int index, final int length) {
        return new JavaByteBuffer(ByteBuffers.slice(buffer, index, length));
    }

    @Override
    public String getStringAscii(final int index, final int length) {
        final byte[] bytes = new byte[length];
        buffer.get(index, bytes, 0, length);
        return ByteBuffers.newStringAscii(bytes);
    }

    @Override
    public void getStringAscii(final int index, final int length, final Appendable dst) {
        try {
            final int limit = index + length;
            for (int i = index; i < limit; i++) {
                final char c = (char) buffer.get(i);
                dst.append(c > 127 ? '?' : c);
            }
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public int putStringAscii(final int index, final CharSequence value, final int valueIndex, final int length) {
        for (int i = 0; i < length; i++) {
            char c = value.charAt(valueIndex + i);
            if (c > 127) {
                c = '?';
            }

            buffer.put(index + i, (byte) c);
        }
        return length;
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        final byte[] bytes = ByteBuffers.newStringUtf8Bytes(value);
        buffer.put(index, bytes);
        return bytes.length;
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        final byte[] bytes = new byte[length];
        buffer.get(index, bytes, 0, length);
        return ByteBuffers.newStringUtf8(bytes);
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        final String string = getStringUtf8(index, length);
        try {
            dst.append(string);
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

}
