package de.invesdwin.util.streams.buffer.extend;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.BitUtil;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.io.DirectBufferInputStream;
import org.agrona.io.DirectBufferOutputStream;

import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.ExpandableMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.extend.internal.ExpandableUninitializedArrayBuffer;

@NotThreadSafe
public class ArrayExpandableByteBuffer extends ExpandableUninitializedArrayBuffer implements IByteBuffer {

    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public ArrayExpandableByteBuffer() {
        super(INITIAL_CAPACITY);
    }

    public ArrayExpandableByteBuffer(final int initialCapacity) {
        super(initialCapacity);
    }

    public ArrayExpandableByteBuffer(final byte[] byteArray) {
        super(byteArray);
    }

    @Override
    public boolean isReadOnly() {
        return false;
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
        //no wrapadjustment needed, since expandable always is a direct reference
        if (dstBuffer.directBuffer() != null) {
            getBytes(index, dstBuffer.directBuffer(), dstIndex, length);
        } else if (dstBuffer.byteBuffer() != null) {
            getBytes(index, dstBuffer.byteBuffer(), dstIndex, length);
        } else if (dstBuffer.byteArray() != null) {
            getBytes(index, dstBuffer.byteArray(), dstIndex, length);
        } else {
            for (int i = 0; i < length; i++) {
                dstBuffer.putByte(dstIndex + i, getByte(index + i));
            }
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        //no wrapadjustment needed, since expandable always is a direct reference
        if (srcBuffer.directBuffer() != null) {
            putBytes(index, srcBuffer.directBuffer(), srcIndex, length);
        } else if (srcBuffer.byteBuffer() != null) {
            putBytes(index, srcBuffer.byteBuffer(), srcIndex, length);
        } else if (srcBuffer.byteArray() != null) {
            putBytes(index, srcBuffer.byteArray(), srcIndex, length);
        } else {
            for (int i = 0; i < length; i++) {
                putByte(index + i, srcBuffer.getByte(srcIndex + i));
            }
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
    public MutableDirectBuffer asDirectBuffer() {
        return directBuffer();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new UnsafeBuffer(this, index, length);
        }
    }

    private IMutableSlicedDelegateByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = new ExpandableMutableSlicedDelegateByteBufferFactory(this);
        }
        return mutableSliceFactory;
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        return getMutableSliceFactory().sliceFrom(index);
    }

    @Override
    public IByteBuffer slice(final int index, final int length) {
        return getMutableSliceFactory().slice(index, length);
    }

    @Override
    public IByteBuffer newSliceFrom(final int index) {
        return new SlicedFromDelegateByteBuffer(this, index);
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            ensureCapacity(index + length);
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
        throw DirectExpandableByteBuffer.newAsByteArrayUnsupported();
    }

    @Deprecated
    @Override
    public byte[] asByteArrayCopy() {
        throw DirectExpandableByteBuffer.newAsByteArrayUnsupported();
    }

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        throw DirectExpandableByteBuffer.newAsByteArrayUnsupported();
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        return ByteBuffers.wrap(asByteArrayCopy(index, length));
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
        ensureCapacity(index + length);
        src.readFully(byteArray(), index, length);
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        ensureCapacity(index + length);
        final byte[] array = byteArray();
        InputStreams.readFully(src, array, index, length);
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        setMemory(index, length, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        return null;
    }

    @Override
    public java.nio.ByteBuffer asByteBuffer(final int index, final int length) {
        ensureCapacity(index + length);
        final java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(byteArray());
        if (index == 0 && length == capacity()) {
            return buffer;
        } else {
            return ByteBuffers.slice(buffer, index, length);
        }
    }

    @Override
    public String toString() {
        return ByteBuffers.toString(this);
    }

    @Override
    public void ensureCapacity(final int desiredCapacity) {
        //we need this workaround to prevent growth when capacity matches on the last bit
        checkLimit(desiredCapacity - BitUtil.SIZE_OF_BYTE);
    }

}