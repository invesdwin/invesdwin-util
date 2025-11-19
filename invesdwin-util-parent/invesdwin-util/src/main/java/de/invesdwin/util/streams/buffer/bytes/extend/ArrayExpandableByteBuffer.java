package de.invesdwin.util.streams.buffer.bytes.extend;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.ExpandableMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.extend.internal.UninitializedExpandableArrayBufferBase;
import de.invesdwin.util.streams.buffer.bytes.stream.ByteBufferInputStream;
import de.invesdwin.util.streams.buffer.bytes.stream.ByteBufferOutputStream;
import de.invesdwin.util.streams.buffer.bytes.stream.ExpandableByteBufferOutputStream;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ByteDelegateMemoryBuffer;

/**
 * Uninitialized can be default here since we don't have to register a cleaner anyway
 */
@NotThreadSafe
public class ArrayExpandableByteBuffer extends UninitializedExpandableArrayBufferBase implements IByteBuffer {

    protected IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

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
    public int getId() {
        return System.identityHashCode(this);
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        return byteBuffer();
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
        final MutableDirectBuffer directBuffer = dstBuffer.directBuffer();
        if (directBuffer != null) {
            getBytes(index, directBuffer, dstIndex + dstBuffer.wrapAdjustment() - directBuffer.wrapAdjustment(),
                    length);
        } else if (dstBuffer.nioByteBuffer() != null) {
            getBytes(index, dstBuffer.nioByteBuffer(), dstIndex + dstBuffer.wrapAdjustment(), length);
        } else if (dstBuffer.byteArray() != null) {
            getBytes(index, dstBuffer.byteArray(), dstIndex + dstBuffer.wrapAdjustment(), length);
        } else {
            for (int i = 0; i < length; i++) {
                dstBuffer.putByte(dstIndex + i, getByte(index + i));
            }
        }
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        for (int i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, getByte(index + i));
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        final MutableDirectBuffer directBuffer = srcBuffer.directBuffer();
        if (directBuffer != null) {
            putBytes(index, directBuffer, srcIndex + srcBuffer.wrapAdjustment() - directBuffer.wrapAdjustment(),
                    length);
        } else if (srcBuffer.nioByteBuffer() != null) {
            putBytes(index, srcBuffer.nioByteBuffer(), srcIndex + srcBuffer.wrapAdjustment(), length);
        } else if (srcBuffer.byteArray() != null) {
            putBytes(index, srcBuffer.byteArray(), srcIndex + srcBuffer.wrapAdjustment(), length);
        } else {
            for (int i = 0; i < length; i++) {
                putByte(index + i, srcBuffer.getByte(srcIndex + i));
            }
        }
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        for (int i = 0; i < length; i++) {
            putByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return new ByteBufferInputStream(this, index, length);
    }

    @Override
    public OutputStream asOutputStream() {
        return new ExpandableByteBufferOutputStream(this);
    }

    @Override
    public OutputStream asOutputStreamFrom(final int index) {
        return new ExpandableByteBufferOutputStream(this, index);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return new ByteBufferOutputStream(this, index, length);
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

    @Override
    public IMemoryBuffer asMemoryBuffer() {
        return new ByteDelegateMemoryBuffer(this);
    }

    @Override
    public IMemoryBuffer asMemoryBufferFrom(final int index) {
        return new ByteDelegateMemoryBuffer(this).newSliceFrom(index);
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        return new ByteDelegateMemoryBuffer(this).newSlice(index, length);
    }

    protected IMutableSlicedDelegateByteBufferFactory getMutableSliceFactory() {
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
        if (index == 0) {
            return this;
        }
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
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            super.putLong(index, bits);
        } else {
            super.putLong(index, value);
        }
    }

    @Override
    public void putInt(final int index, final int value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            super.putInt(index, bits);
        } else {
            super.putInt(index, value);
        }
    }

    @Override
    public void putDouble(final int index, final double value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            super.putLong(index, bits);
        } else {
            super.putDouble(index, value);
        }
    }

    @Override
    public void putFloat(final int index, final float value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            super.putInt(index, bits);
        } else {
            super.putFloat(index, value);
        }
    }

    @Override
    public void putShort(final int index, final short value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            super.putShort(index, bits);
        } else {
            super.putShort(index, value);
        }
    }

    @Override
    public void putChar(final int index, final char value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            super.putShort(index, bits);
        } else {
            super.putChar(index, value);
        }
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            super.putLong(index, bits);
        } else {
            super.putLong(index, value);
        }
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            super.putInt(index, bits);
        } else {
            super.putInt(index, value);
        }
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            super.putLong(index, bits);
        } else {
            super.putDouble(index, value);
        }
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            super.putInt(index, bits);
        } else {
            super.putFloat(index, value);
        }
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            super.putShort(index, bits);
        } else {
            super.putShort(index, value);
        }
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            super.putShort(index, bits);
        } else {
            super.putChar(index, value);
        }
    }

    @Override
    public long getLong(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Long.reverseBytes(bits);
        } else {
            return super.getLong(index);
        }
    }

    @Override
    public int getInt(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return super.getInt(index);
        }
    }

    @Override
    public double getDouble(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return super.getDouble(index);
        }
    }

    @Override
    public float getFloat(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return super.getFloat(index);
        }
    }

    @Override
    public short getShort(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return Short.reverseBytes(bits);
        } else {
            return super.getShort(index);
        }
    }

    @Override
    public char getChar(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return super.getChar(index);
        }
    }

    @Override
    public long getLongReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Long.reverseBytes(bits);
        } else {
            return super.getLong(index);
        }
    }

    @Override
    public int getIntReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return super.getInt(index);
        }
    }

    @Override
    public double getDoubleReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return super.getDouble(index);
        }
    }

    @Override
    public float getFloatReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return super.getFloat(index);
        }
    }

    @Override
    public short getShortReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return Short.reverseBytes(bits);
        } else {
            return super.getShort(index);
        }
    }

    @Override
    public char getCharReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return super.getChar(index);
        }
    }

    @Override
    public byte[] asByteArray() {
        return asByteArrayTo(capacity());
    }

    @Override
    public byte[] asByteArrayCopy() {
        return asByteArrayCopyTo(capacity());
    }

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        return ByteBuffers.wrap(asByteArrayCopy());
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        return ByteBuffers.wrap(asByteArrayCopy(index, length));
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        return ByteBuffers.asByteArray(this, index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        return ByteBuffers.asByteArrayCopy(this, index, length);
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else {
            dst.write(byteArray(), index, length);
        }
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else if (dst instanceof FileOutputStream && ((FileOutputStream) dst).getChannel() != null) {
            final FileOutputStream cDst = (FileOutputStream) dst;
            getBytesTo(index, cDst.getChannel(), length);
        } else if (dst instanceof DataOutput) {
            getBytesTo(index, (DataOutput) dst, length);
        } else {
            dst.write(byteArray(), index, length);
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else {
            ensureCapacity(index + length);
            src.readFully(byteArray(), index, length);
        }
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else if (src instanceof FileInputStream && ((FileInputStream) src).getChannel() != null) {
            final FileInputStream cSrc = (FileInputStream) src;
            putBytesTo(index, cSrc.getChannel(), length);
        } else if (src instanceof DataInput) {
            putBytesTo(index, (DataInput) src, length);
        } else {
            ensureCapacity(index + length);
            final byte[] array = byteArray();
            InputStreams.readFullyNoTimeout(src, array, index, length);
        }
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        OutputStreams.writeFullyNoTimeout(dst, asNioByteBuffer(index, length));
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        InputStreams.readFullyNoTimeout(src, asNioByteBuffer(index, length));
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
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
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
    public IByteBuffer ensureCapacity(final int desiredCapacity) {
        if (desiredCapacity > capacity()) {
            //we need this workaround to prevent growth when capacity matches on the last bit
            checkLimit(desiredCapacity - BitUtil.SIZE_OF_BYTE);
        }
        return this;
    }

    @Override
    public IByteBuffer asImmutableSlice() {
        return this;
    }

}
