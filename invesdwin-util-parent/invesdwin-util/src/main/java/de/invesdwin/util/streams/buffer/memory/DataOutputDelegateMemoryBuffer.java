package de.invesdwin.util.streams.buffer.memory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.concurrent.loop.spinwait.ASpinWait;
import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedFromDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.stream.MemoryBufferOutputStream;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class DataOutputDelegateMemoryBuffer implements IMemoryBuffer {
    private final DataOutput out;

    private IMutableSlicedDelegateMemoryBufferFactory mutableSliceFactory;
    private long position = 0;

    public DataOutputDelegateMemoryBuffer(final DataOutput out) {
        this.out = out;
    }

    @Override
    public long capacity() {
        return Long.MAX_VALUE;
    }

    private UnsupportedOperationException newUnsupportedOperationException() {
        return new UnsupportedOperationException("Cannot read from output stream");
    }

    @Override
    public int getId() {
        return System.identityHashCode(out);
    }

    @Override
    public <T> T unwrap(final Class<T> type) {
        throw newUnsupportedOperationException();
    }

    @Override
    public IMemoryBuffer ensureCapacity(final long capacity) {
        return this;
    }

    @Override
    public ByteOrder getOrder() {
        throw newUnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public long addressOffset() {
        throw newUnsupportedOperationException();
    }

    @Override
    public byte getByte(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public long getLong(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public long getLongReverse(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public int getInt(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public int getIntReverse(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public double getDouble(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public double getDoubleReverse(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public float getFloat(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public float getFloatReverse(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public short getShort(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public short getShortReverse(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public char getChar(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public char getCharReverse(final long index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytes(final long index, final byte[] dst, final int dstIndex, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public long wrapAdjustment() {
        throw newUnsupportedOperationException();
    }

    @Override
    public boolean isExpandable() {
        return true;
    }

    private void checkPosition(final long index, final long size) {
        checkPosition(index);
        updatePosition(size);
    }

    private void checkPosition(final long index) {
        if (index != position) {
            throw new IllegalArgumentException("Index must be equal to current position: " + position);
        }
    }

    private void updatePosition(final long size) {
        position += size;
    }

    @Override
    public void putByte(final long index, final byte value) {
        checkPosition(index, Byte.BYTES);
        try {
            out.writeByte(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putLong(final long index, final long value) {
        checkPosition(index, Long.BYTES);
        try {
            out.writeLong(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        checkPosition(index, Long.BYTES);
        try {
            final long bits = Long.reverseBytes(value);
            out.writeLong(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putInt(final long index, final int value) {
        checkPosition(index, Integer.BYTES);
        try {
            out.writeInt(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        checkPosition(index, Integer.BYTES);
        try {
            final int bits = Integer.reverseBytes(value);
            out.writeInt(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putDouble(final long index, final double value) {
        checkPosition(index, Double.BYTES);
        try {
            out.writeDouble(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        checkPosition(index, Double.BYTES);
        try {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            out.writeLong(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putFloat(final long index, final float value) {
        checkPosition(index, Float.BYTES);
        try {
            out.writeFloat(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        checkPosition(index, Float.BYTES);
        try {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            out.writeInt(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putShort(final long index, final short value) {
        checkPosition(index, Short.BYTES);
        try {
            out.writeShort(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        checkPosition(index, Short.BYTES);
        try {
            final short bits = Short.reverseBytes(value);
            out.writeShort(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putChar(final long index, final char value) {
        checkPosition(index, Character.BYTES);
        try {
            out.writeChar(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        checkPosition(index, Character.BYTES);
        try {
            final short bits = Short.reverseBytes((short) value);
            out.writeShort(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        checkPosition(index, length);
        try {
            out.write(src, srcIndex, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        checkPosition(index, length);
        try {
            final IByteBuffer srcWrapped = ByteBuffers.wrap(srcBuffer);
            srcWrapped.getBytesTo(srcIndex, out, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        checkPosition(index, length);
        try {
            final IByteBuffer srcWrapped = ByteBuffers.wrap(srcBuffer);
            srcWrapped.getBytesTo(srcIndex, out, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        checkPosition(index, length);
        try {
            srcBuffer.getBytesTo(srcIndex, out, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        checkPosition(index, length);
        try {
            srcBuffer.getBytesTo(srcIndex, out, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {
        checkPosition(index, length);
        try {
            OutputStreams.writeAscii(out, value, valueIndex, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        checkPosition(index);
        try {
            final int length = OutputStreams.writeUTF(out, value);
            updatePosition(length);
            return length;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else {
            checkPosition(index, length);
            long i = index;
            final long targetIndex = index + length;
            while (i < targetIndex) {
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

            checkPosition(index, length);
            long i = index;
            final long targetIndex = index + length;
            while (i < targetIndex) {
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
                    out.writeByte((byte) result);
                    i++;
                }
            }
        }
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        checkPosition(index, length);
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        return new MemoryBufferOutputStream(this, index, length);
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        throw newUnsupportedOperationException();
    }

    private IMutableSlicedDelegateMemoryBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateMemoryBufferFactory.newInstance(this);
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
        } else {
            return new SlicedFromDelegateMemoryBuffer(this, index);
        }
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new SlicedDelegateMemoryBuffer(this, index, length);
        }
    }

    @Override
    public String getStringAsciii(final long index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getStringAsciii(final long index, final int length, final Appendable dst) {
        throw newUnsupportedOperationException();
    }

    @Override
    public String getStringUtf8(final long index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getStringUtf8(final long index, final int length, final Appendable dst) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) throws IOException {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) throws IOException {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
        throw newUnsupportedOperationException();
    }

    @Override
    public IMemoryBuffer clone(final long index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public IMemoryBuffer asImmutableSlice() {
        return this;
    }
}