package de.invesdwin.util.lang.buffer.extend;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.io.DirectBufferInputStream;
import org.agrona.io.DirectBufferOutputStream;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.buffer.ByteBuffers;
import de.invesdwin.util.lang.buffer.IByteBuffer;

@NotThreadSafe
public class ExpandableArrayByteBuffer extends ExpandableArrayBuffer implements IByteBuffer {

    public ExpandableArrayByteBuffer() {
        super(INITIAL_CAPACITY);
    }

    public ExpandableArrayByteBuffer(final int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return this;
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        if (dstBuffer.directBuffer() != null) {
            getBytes(index, dstBuffer.directBuffer(), dstIndex, length);
        } else if (dstBuffer.byteBuffer() != null) {
            getBytes(index, dstBuffer.byteBuffer(), dstIndex, length);
        } else if (dstBuffer.byteArray() != null) {
            getBytes(index, dstBuffer.byteArray(), dstIndex, length);
        } else {
            throw UnknownArgumentException.newInstance(IByteBuffer.class, dstBuffer);
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        if (srcBuffer.directBuffer() != null) {
            putBytes(index, srcBuffer.directBuffer(), srcIndex, length);
        } else if (srcBuffer.byteBuffer() != null) {
            putBytes(index, srcBuffer.byteBuffer(), srcIndex, length);
        } else if (srcBuffer.byteArray() != null) {
            putBytes(index, srcBuffer.byteArray(), srcIndex, length);
        } else {
            throw UnknownArgumentException.newInstance(IByteBuffer.class, srcBuffer);
        }
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return new DirectBufferInputStream(this, index, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        //allow output stream to actually grow the buffer
        return new DirectBufferOutputStream(this, index, ExpandableArrayBuffer.MAX_ARRAY_LENGTH - index);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new UnsafeBuffer(this, index, length);
        }
    }

    @Override
    public IByteBuffer slice(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new UnsafeByteBuffer(this, index, length);
        }
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        return getStringWithoutLengthAscii(index, length);
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        getStringWithoutLengthAscii(index, length, dst);
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        putStringWithoutLengthAscii(index, value, valueIndex, length);
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        return getStringWithoutLengthUtf8(index, length);
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        return putStringWithoutLengthUtf8(index, value);
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        final String string = getStringWithoutLengthUtf8(index, length);
        try {
            dst.append(string);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putLong(final int index, final long value) {
        putLong(index, value, getOrder());
    }

    @Override
    public void putInt(final int index, final int value) {
        putInt(index, value, getOrder());
    }

    @Override
    public void putDouble(final int index, final double value) {
        putDouble(index, value, getOrder());
    }

    @Override
    public void putFloat(final int index, final float value) {
        putFloat(index, value, getOrder());
    }

    @Override
    public void putShort(final int index, final short value) {
        putShort(index, value, getOrder());
    }

    @Override
    public void putChar(final int index, final char value) {
        putChar(index, value, getOrder());
    }

    @Override
    public long getLong(final int index) {
        return getLong(index, getOrder());
    }

    @Override
    public int getInt(final int index) {
        return getInt(index, getOrder());
    }

    @Override
    public double getDouble(final int index) {
        return getDouble(index, getOrder());
    }

    @Override
    public float getFloat(final int index) {
        return getFloat(index, getOrder());
    }

    @Override
    public short getShort(final int index) {
        return getShort(index, getOrder());
    }

    @Override
    public char getChar(final int index) {
        return getChar(index, getOrder());
    }

    @Deprecated
    @Override
    public byte[] asByteArray() {
        throw ExpandableByteBuffer.newAsByteArrayUnsupported();
    }

    @Deprecated
    @Override
    public byte[] asByteArrayCopy() {
        throw ExpandableByteBuffer.newAsByteArrayUnsupported();
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return byteArray();
        }
        return ByteBuffers.asByteArrayCopyGet(this, index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return byteArray().clone();
        }
        return ByteBuffers.asByteArrayCopyGet(this, index, length);
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        dst.write(byteArray(), index, length);
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        dst.write(byteArray(), index, length);
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        checkLimit(index + length);
        src.readFully(byteArray(), index, length);
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        final byte[] array = byteArray();
        final int end = index + length;
        int remaining = length;
        while (remaining > 0) {
            final int location = end - remaining;
            final int count = src.read(array, location, remaining);
            if (count == -1) { // EOF
                break;
            }
            remaining -= count;
        }
        if (remaining > 0) {
            throw ByteBuffers.newPutBytesToEOF();
        }
    }

}
