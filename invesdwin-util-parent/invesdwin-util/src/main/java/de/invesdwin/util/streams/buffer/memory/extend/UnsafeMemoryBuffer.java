package de.invesdwin.util.streams.buffer.memory.extend;

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

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.concurrent.loop.ASpinWait;
import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.delegate.MemoryDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.ArrayExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.FixedMutableSlicedDelegateMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.extend.internal.UnsafeMemoryBase;
import de.invesdwin.util.streams.buffer.memory.stream.MemoryBufferInputStream;
import de.invesdwin.util.streams.buffer.memory.stream.MemoryBufferOutputStream;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class UnsafeMemoryBuffer extends UnsafeMemoryBase implements IMemoryBuffer {

    private IMutableSlicedDelegateMemoryBufferFactory mutableSliceFactory;

    public UnsafeMemoryBuffer() {
        super();
    }

    public UnsafeMemoryBuffer(final byte[] buffer) {
        super(ByteBuffers.assertBuffer(buffer));
    }

    public UnsafeMemoryBuffer(final byte[] buffer, final int offset, final int length) {
        super(ByteBuffers.assertBuffer(buffer), offset, length);
    }

    public UnsafeMemoryBuffer(final java.nio.ByteBuffer buffer) {
        super(ByteBuffers.assertBuffer(buffer));
    }

    public UnsafeMemoryBuffer(final java.nio.ByteBuffer buffer, final long offset, final long length) {
        super(ByteBuffers.assertBuffer(buffer), offset, length);
    }

    public UnsafeMemoryBuffer(final DirectBuffer buffer) {
        super(ByteBuffers.assertBuffer(buffer));
    }

    public UnsafeMemoryBuffer(final DirectBuffer buffer, final long offset, final long length) {
        super(ByteBuffers.assertBuffer(buffer), offset, length);
    }

    public UnsafeMemoryBuffer(final IMemoryBuffer buffer, final long offset, final long length) {
        super(MemoryBuffers.assertBuffer(buffer), offset, length);
    }

    public UnsafeMemoryBuffer(final long address, final long length) {
        super(ByteBuffers.assertBuffer(address), length);
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
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
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
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        for (long i = 0; i < length; i++) {
            dstBuffer.putByte(dstIndex + i, getByte(index + i));
        }
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        //wrapadjustment only needed for byteArray
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
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        for (long i = 0; i < length; i++) {
            putByte(index + i, srcBuffer.getByte(srcIndex + i));
        }
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        return new MemoryBufferInputStream(this, index, length);
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        return new MemoryBufferOutputStream(this, index, length);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        return asUnsafeBuffer(index, length);
    }

    private UnsafeByteBuffer asUnsafeBuffer(final long index, final int length) {
        final byte[] byteArray = byteArray();
        if (byteArray != null) {
            return new UnsafeByteBuffer(byteArray, Integers.checkedCast(index), length);
        } else {
            return new UnsafeByteBuffer(addressOffset() + wrapAdjustment() + index, length);
        }
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        if (index + length < ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH) {
            return asUnsafeBuffer(index, length);
        } else {
            return new MemoryDelegateByteBuffer(newSlice(index, length));
        }
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        return MemoryBuffers.asByteArrayCopyGet(this, index, length);
    }

    private IMutableSlicedDelegateMemoryBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = new FixedMutableSlicedDelegateMemoryBufferFactory(this);
        }
        return mutableSliceFactory;
    }

    @Override
    public IMemoryBuffer sliceFrom(final long index) {
        return getMutableSliceFactory().sliceFrom(index);
    }

    @Override
    public IMemoryBuffer slice(final long index, final long length) {
        return getMutableSliceFactory().slice(index, length);
    }

    @Override
    public IMemoryBuffer newSliceFrom(final long index) {
        if (index == 0) {
            return this;
        }
        return new UnsafeMemoryBuffer(this, index, remaining(index));
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new UnsafeMemoryBuffer(this, index, length);
        }
    }

    @Override
    public String getStringAsciii(final long index, final int length) {
        return getStringWithoutLengthAscii(index, length);
    }

    @Override
    public void getStringAsciii(final long index, final int length, final Appendable dst) {
        getStringWithoutLengthAscii(index, length, dst);
    }

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {
        putStringWithoutLengthAscii(index, value, valueIndex, length);
    }

    @Override
    public String getStringUtf8(final long index, final int length) {
        return getStringWithoutLengthUtf8(index, length);
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        return putStringWithoutLengthUtf8(index, value);
    }

    @Override
    public void getStringUtf8(final long index, final int length, final Appendable dst) {
        final String string = getStringWithoutLengthUtf8(index, length);
        try {
            dst.append(string);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else {
            long i = index;
            while (i < length) {
                final byte b = getByte(i);
                dst.write(b);
                i++;
            }
        }
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else if (dst instanceof FileOutputStream && ((FileOutputStream) dst).getChannel() != null) {
            final FileOutputStream cDst = (FileOutputStream) dst;
            getBytesTo(index, cDst.getChannel(), length);
        } else if (dst instanceof DataOutput) {
            getBytesTo(index, (DataOutput) dst, length);
        } else {
            long i = index;
            while (i < length) {
                final byte b = getByte(i);
                dst.write(b);
                i++;
            }
        }
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else {
            long i = index;
            while (i < length) {
                final byte b = src.readByte();
                putByte(i, b);
                i++;
            }
        }
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else if (src instanceof FileInputStream && ((FileInputStream) src).getChannel() != null) {
            final FileInputStream cSrc = (FileInputStream) src;
            putBytesTo(index, cSrc.getChannel(), length);
        } else if (src instanceof DataInput) {
            putBytesTo(index, (DataInput) src, length);
        } else {
            final Duration timeout = URIs.getDefaultNetworkTimeout();
            long zeroCountNanos = -1L;

            final long limit = index + length;
            for (long i = index; i < limit;) {
                final int result = src.read();
                if (result < 0) { // EOF
                    throw ByteBuffers.newEOF();
                }
                if (result == 0 && timeout != null) {
                    if (zeroCountNanos == -1) {
                        zeroCountNanos = System.nanoTime();
                    } else if (timeout.isLessThanNanos(System.nanoTime() - zeroCountNanos)) {
                        throw FastEOFException.getInstance("write timeout exceeded");
                    }
                    ASpinWait.onSpinWaitStatic();
                } else {
                    zeroCountNanos = -1L;
                    putByte(i, (byte) result);
                    i++;
                }
            }
        }
    }

    @Override
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
        if (length > ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH) {
            long remaining = length;
            while (remaining > 0L) {
                final long chunk = Longs.min(remaining, ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH);
                remaining -= chunk;
                OutputStreams.writeFully(dst, asNioByteBuffer(index, (int) chunk));
            }
        } else {
            OutputStreams.writeFully(dst, asNioByteBuffer(index, (int) length));
        }
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        if (length > ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH) {
            long remaining = length;
            while (remaining > 0L) {
                final long chunk = Longs.min(remaining, ArrayExpandableByteBuffer.MAX_ARRAY_LENGTH);
                remaining -= chunk;
                InputStreams.readFully(src, asNioByteBuffer(index, (int) chunk));
            }
        } else {
            InputStreams.readFully(src, asNioByteBuffer(index, (int) length));
        }
    }

    @Override
    public void putLong(final long index, final long value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            super.putLong(index, bits);
        } else {
            super.putLong(index, value);
        }
    }

    @Override
    public void putInt(final long index, final int value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            super.putInt(index, bits);
        } else {
            super.putInt(index, value);
        }
    }

    @Override
    public void putDouble(final long index, final double value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            super.putLong(index, bits);
        } else {
            super.putDouble(index, value);
        }
    }

    @Override
    public void putFloat(final long index, final float value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            super.putInt(index, bits);
        } else {
            super.putFloat(index, value);
        }
    }

    @Override
    public void putShort(final long index, final short value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            super.putShort(index, bits);
        } else {
            super.putShort(index, value);
        }
    }

    @Override
    public void putChar(final long index, final char value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            super.putShort(index, bits);
        } else {
            super.putChar(index, value);
        }
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            super.putLong(index, bits);
        } else {
            super.putLong(index, value);
        }
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            super.putInt(index, bits);
        } else {
            super.putInt(index, value);
        }
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            super.putLong(index, bits);
        } else {
            super.putDouble(index, value);
        }
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            super.putInt(index, bits);
        } else {
            super.putFloat(index, value);
        }
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            super.putShort(index, bits);
        } else {
            super.putShort(index, value);
        }
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            super.putShort(index, bits);
        } else {
            super.putChar(index, value);
        }
    }

    @Override
    public long getLong(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Long.reverseBytes(bits);
        } else {
            return super.getLong(index);
        }
    }

    @Override
    public int getInt(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return super.getInt(index);
        }
    }

    @Override
    public double getDouble(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return super.getDouble(index);
        }
    }

    @Override
    public float getFloat(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return super.getFloat(index);
        }
    }

    @Override
    public short getShort(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return Short.reverseBytes(bits);
        } else {
            return super.getShort(index);
        }
    }

    @Override
    public char getChar(final long index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return super.getChar(index);
        }
    }

    @Override
    public long getLongReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Long.reverseBytes(bits);
        } else {
            return super.getLong(index);
        }
    }

    @Override
    public int getIntReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return super.getInt(index);
        }
    }

    @Override
    public double getDoubleReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return super.getDouble(index);
        }
    }

    @Override
    public float getFloatReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return super.getFloat(index);
        }
    }

    @Override
    public short getShortReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return Short.reverseBytes(bits);
        } else {
            return super.getShort(index);
        }
    }

    @Override
    public char getCharReverse(final long index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return super.getChar(index);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        return null;
    }

    public java.nio.ByteBuffer asNioByteBuffer() {
        final java.nio.ByteBuffer byteBuffer = byteBuffer();
        final long wrapAdjustment = wrapAdjustment();
        final int intCapacity = Integers.checkedCastNoOverflow(capacity());
        if (byteBuffer != null) {
            if (wrapAdjustment == 0 && capacity() == byteBuffer.capacity()) {
                return byteBuffer;
            } else {
                return ByteBuffers.slice(byteBuffer, Integers.checkedCast(wrapAdjustment), intCapacity);
            }
        }
        final byte[] array = byteArray();
        if (array != null) {
            final java.nio.ByteBuffer arrayBuffer = java.nio.ByteBuffer.wrap(array,
                    Integers.checkedCast(wrapAdjustment), intCapacity);
            return arrayBuffer;
        }
        final long address = addressOffset();
        return UninitializedDirectByteBuffers.asDirectByteBufferNoCleaner(address, intCapacity);
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        if (index == 0) {
            final java.nio.ByteBuffer buffer = asNioByteBuffer();
            final int intCapacity = Integers.checkedCastNoOverflow(capacity());
            if (length == intCapacity) {
                return buffer;
            } else {
                return ByteBuffers.slice(buffer, 0, length);
            }
        } else {
            final long address = addressOffset() + index;
            return UninitializedDirectByteBuffers.asDirectByteBufferNoCleaner(address, length);
        }
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        super.setMemory(index, length, value);
    }

    @Override
    public String toString() {
        return MemoryBuffers.toString(this);
    }

    @Override
    public IMemoryBuffer ensureCapacity(final long desiredCapacity) {
        if (desiredCapacity > capacity()) {
            checkLimit(desiredCapacity);
        }
        return this;
    }

    @Override
    public IMemoryBuffer clone(final long index, final int length) {
        return MemoryBuffers.wrap(asByteArrayCopy(index, length));
    }

    @Override
    public IMemoryBuffer asImmutableSlice() {
        return this;
    }

}
