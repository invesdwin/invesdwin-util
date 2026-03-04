package de.invesdwin.util.streams.buffer.bytes.delegate;

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
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.stream.ByteBufferOutputStream;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class DataOutputDelegateByteBuffer implements IByteBuffer {
    private final DataOutput out;

    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;
    private int position = 0;

    public DataOutputDelegateByteBuffer(final DataOutput out) {
        this.out = out;
    }

    @Override
    public int capacity() {
        return Integer.MAX_VALUE;
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
    public IByteBuffer ensureCapacity(final int capacity) {
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
    public MutableDirectBuffer directBuffer() {
        throw newUnsupportedOperationException();
    }

    @Override
    public byte[] byteArray() {
        throw newUnsupportedOperationException();
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        throw newUnsupportedOperationException();
    }

    @Override
    public byte getByte(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public long getLong(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public long getLongReverse(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public int getInt(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public int getIntReverse(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public double getDouble(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public double getDoubleReverse(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public float getFloat(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public float getFloatReverse(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public short getShort(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public short getShortReverse(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public char getChar(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public char getCharReverse(final int index) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public int wrapAdjustment() {
        throw newUnsupportedOperationException();
    }

    @Override
    public boolean isExpandable() {
        return true;
    }

    private void checkPosition(final int index, final int size) {
        checkPosition(index);
        updatePosition(size);
    }

    private void checkPosition(final int index) {
        if (index != position) {
            throw new IllegalArgumentException("Index must be equal to current position: " + position);
        }
    }

    private void updatePosition(final int size) {
        position += size;
    }

    @Override
    public void putByte(final int index, final byte value) {
        checkPosition(index, Byte.BYTES);
        try {
            out.writeByte(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putLong(final int index, final long value) {
        checkPosition(index, Long.BYTES);
        try {
            out.writeLong(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        checkPosition(index, Long.BYTES);
        try {
            final long bits = Long.reverseBytes(value);
            out.writeLong(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putInt(final int index, final int value) {
        checkPosition(index, Integer.BYTES);
        try {
            out.writeInt(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        checkPosition(index, Integer.BYTES);
        try {
            final int bits = Integer.reverseBytes(value);
            out.writeInt(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putDouble(final int index, final double value) {
        checkPosition(index, Double.BYTES);
        try {
            out.writeDouble(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        checkPosition(index, Double.BYTES);
        try {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            out.writeLong(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putFloat(final int index, final float value) {
        checkPosition(index, Float.BYTES);
        try {
            out.writeFloat(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        checkPosition(index, Float.BYTES);
        try {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            out.writeInt(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putShort(final int index, final short value) {
        checkPosition(index, Short.BYTES);
        try {
            out.writeShort(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        checkPosition(index, Short.BYTES);
        try {
            final short bits = Short.reverseBytes(value);
            out.writeShort(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putChar(final int index, final char value) {
        checkPosition(index, Character.BYTES);
        try {
            out.writeChar(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        checkPosition(index, Character.BYTES);
        try {
            final short bits = Short.reverseBytes((short) value);
            out.writeShort(bits);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        checkPosition(index, length);
        try {
            out.write(src, srcIndex, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        checkPosition(index, length);
        try {
            final IByteBuffer srcWrapped = ByteBuffers.wrap(srcBuffer);
            srcWrapped.getBytesTo(srcIndex, out, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        checkPosition(index, length);
        try {
            final IByteBuffer srcWrapped = ByteBuffers.wrap(srcBuffer);
            srcWrapped.getBytesTo(srcIndex, out, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        checkPosition(index, length);
        try {
            srcBuffer.getBytesTo(srcIndex, out, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        checkPosition(index, length);
        try {
            srcBuffer.getBytesTo(srcIndex, out, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        checkPosition(index, length);
        try {
            OutputStreams.writeAscii(out, value, valueIndex, length);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
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
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else {
            checkPosition(index, length);
            int i = index;
            final int targetIndex = index + length;
            while (i < targetIndex) {
                final byte b = src.readByte();
                putByte(i, b);
                i++;
            }
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
            final Duration timeout = URIs.getDefaultNetworkTimeout();
            long zeroCountNanos = -1L;

            checkPosition(index, length);
            int i = index;
            final int targetIndex = index + length;
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
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        checkPosition(index, length);

    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return new ByteBufferOutputStream(this, index, length);
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        throw newUnsupportedOperationException();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public IMemoryBuffer asMemoryBuffer() {
        throw newUnsupportedOperationException();
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        throw newUnsupportedOperationException();
    }

    private IMutableSlicedDelegateByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateByteBufferFactory.newInstance(this);
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
        } else {
            return new SlicedFromDelegateByteBuffer(this, index);
        }
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new SlicedDelegateByteBuffer(this, index, length);
        }
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        throw newUnsupportedOperationException();
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        throw newUnsupportedOperationException();
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        throw newUnsupportedOperationException();
    }

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        throw newUnsupportedOperationException();
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        throw newUnsupportedOperationException();
    }

    @Override
    public IByteBuffer asImmutableSlice() {
        return this;
    }
}