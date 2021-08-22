package de.invesdwin.util.lang.buffer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.io.DirectBufferInputStream;
import org.agrona.io.DirectBufferOutputStream;

import de.invesdwin.util.error.UnknownArgumentException;

@NotThreadSafe
public class AgronaByteBuffer implements IByteBuffer {

    private final MutableDirectBuffer delegate;

    public AgronaByteBuffer(final MutableDirectBuffer delegate) {
        this.delegate = delegate;
    }

    @Override
    public long addressOffset() {
        return delegate.addressOffset();
    }

    @Override
    public byte[] byteArray() {
        return delegate.byteArray();
    }

    @Override
    public ByteBuffer byteBuffer() {
        return delegate.byteBuffer();
    }

    @Override
    public int capacity() {
        return delegate.capacity();
    }

    @Override
    public long getLong(final int index) {
        return delegate.getLong(index);
    }

    @Override
    public int getInt(final int index) {
        return delegate.getInt(index);
    }

    @Override
    public double getDouble(final int index) {
        return delegate.getDouble(index);
    }

    @Override
    public float getFloat(final int index) {
        return delegate.getFloat(index);
    }

    @Override
    public short getShort(final int index) {
        return delegate.getShort(index);
    }

    @Override
    public char getChar(final int index) {
        return delegate.getChar(index);
    }

    @Override
    public byte getByte(final int index) {
        return delegate.getByte(index);
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int offset, final int length) {
        delegate.getBytes(index, dst, offset, length);
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final ByteBuffer dstBuffer, final int dstOffset, final int length) {
        delegate.getBytes(index, dstBuffer, dstOffset, length);
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        if (dstBuffer.directBuffer() != null) {
            delegate.getBytes(index, dstBuffer.directBuffer(), dstIndex, length);
        } else if (dstBuffer.byteBuffer() != null) {
            delegate.getBytes(index, dstBuffer.byteBuffer(), dstIndex, length);
        } else if (dstBuffer.byteArray() != null) {
            delegate.getBytes(index, dstBuffer.byteArray(), dstIndex, length);
        } else {
            throw UnknownArgumentException.newInstance(IByteBuffer.class, dstBuffer);
        }
    }

    @Override
    public int wrapAdjustment() {
        return delegate.wrapAdjustment();
    }

    @Override
    public boolean isExpandable() {
        return delegate.isExpandable();
    }

    @Override
    public int putLong(final int index, final long value) {
        delegate.putLong(index, value);
        return Long.BYTES;
    }

    @Override
    public int putInt(final int index, final int value) {
        delegate.putInt(index, value);
        return Integer.BYTES;
    }

    @Override
    public int putDouble(final int index, final double value) {
        delegate.putDouble(index, value);
        return Double.BYTES;
    }

    @Override
    public int putFloat(final int index, final float value) {
        delegate.putFloat(index, value);
        return Float.BYTES;
    }

    @Override
    public int putShort(final int index, final short value) {
        delegate.putShort(index, value);
        return Short.BYTES;
    }

    @Override
    public int putChar(final int index, final char value) {
        delegate.putChar(index, value);
        return Character.BYTES;
    }

    @Override
    public int putByte(final int index, final byte value) {
        delegate.putByte(index, value);
        return Byte.BYTES;
    }

    @Override
    public int putBytes(final int index, final byte[] src) {
        delegate.putBytes(index, src);
        return src.length;
    }

    @Override
    public int putBytes(final int index, final byte[] src, final int offset, final int length) {
        delegate.putBytes(index, src, offset, length);
        return length;
    }

    @Override
    public int putBytes(final int index, final ByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index, srcBuffer, srcIndex, length);
        return length;
    }

    @Override
    public int putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index, srcBuffer, srcIndex, length);
        return length;
    }

    @Override
    public int putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        if (srcBuffer.directBuffer() != null) {
            delegate.putBytes(index, srcBuffer.directBuffer(), srcIndex, length);
        } else if (srcBuffer.byteBuffer() != null) {
            delegate.putBytes(index, srcBuffer.byteBuffer(), srcIndex, length);
        } else if (srcBuffer.byteArray() != null) {
            delegate.putBytes(index, srcBuffer.byteArray(), srcIndex, length);
        } else {
            throw UnknownArgumentException.newInstance(IByteBuffer.class, srcBuffer);
        }
        return length;
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return delegate;
    }

    @Override
    public InputStream asInputStream(final int offset, final int length) {
        return new DirectBufferInputStream(delegate, offset, length);
    }

    @Override
    public OutputStream asOutputStream(final int offset, final int length) {
        return new DirectBufferOutputStream(delegate, offset, length);
    }

    @Override
    public byte[] asByteArray(final int offset, final int length) {
        if (wrapAdjustment() == 0 && offset == 0 && length == capacity()) {
            final byte[] bytes = byteArray();
            if (bytes != null) {
                return bytes;
            }
            final ByteBuffer byteBuffer = byteBuffer();
            if (byteBuffer != null) {
                final byte[] array = byteBuffer.array();
                if (array != null) {
                    return array;
                }
            }
        }
        final byte[] bytes = new byte[length];
        delegate.getBytes(offset, bytes, 0, length);
        return bytes;
    }

    @Override
    public IByteBuffer slice(final int offset, final int length) {
        if (offset == 0 && length == capacity()) {
            return this;
        } else {
            return new AgronaByteBuffer(new UnsafeBuffer(delegate, offset, length));
        }
    }

}
