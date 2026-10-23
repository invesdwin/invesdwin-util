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

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.UnsafeApi;
import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.concurrent.loop.spinwait.ASpinWait;
import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.FixedMutableSlicedDelegateCloseableByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.stream.ByteBufferInputStream;
import de.invesdwin.util.streams.buffer.bytes.stream.ByteBufferOutputStream;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ByteDelegateMemoryBuffer;
import de.invesdwin.util.time.date.millis.FDateNanos;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class UnsafeByteBuffer extends UnsafeBuffer implements ICloseableByteBuffer {

    protected IMutableSlicedDelegateCloseableByteBufferFactory mutableSliceFactory;

    public UnsafeByteBuffer() {
        super();
    }

    public UnsafeByteBuffer(final byte[] buffer) {
        super(ByteBuffers.assertBuffer(buffer));
    }

    public UnsafeByteBuffer(final byte[] buffer, final int offset, final int length) {
        super(ByteBuffers.assertBuffer(buffer), offset, length);
    }

    public UnsafeByteBuffer(final java.nio.ByteBuffer buffer) {
        super(ByteBuffers.assertBuffer(buffer));
    }

    public UnsafeByteBuffer(final java.nio.ByteBuffer buffer, final int offset, final int length) {
        super(ByteBuffers.assertBuffer(buffer), offset, length);
    }

    public UnsafeByteBuffer(final DirectBuffer buffer) {
        super(ByteBuffers.assertBuffer(buffer));
    }

    public UnsafeByteBuffer(final DirectBuffer buffer, final int offset, final int length) {
        super(ByteBuffers.assertBuffer(buffer), offset, length);
    }

    public UnsafeByteBuffer(final long address, final int length) {
        super(ByteBuffers.assertBuffer(address), length);
    }

    @Override
    public int getId() {
        return Objects.hashCode(addressOffset(), capacity());
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
    public OutputStream asOutputStream(final int index, final int length) {
        return new ByteBufferOutputStream(this, index, length);
    }

    @Override
    public byte[] asByteArray() {
        if (wrapAdjustment() == 0) {
            final byte[] bytes = byteArray();
            if (bytes != null) {
                return bytes;
            }
            final java.nio.ByteBuffer byteBuffer = nioByteBuffer();
            if (byteBuffer != null && byteBuffer.hasArray()) {
                final byte[] array = byteBuffer.array();
                if (array != null) {
                    return array;
                }
            }
        }
        return ByteBuffers.asByteArrayCopyGet(this, 0, capacity());
    }

    @Override
    public byte[] asByteArrayCopy() {
        if (wrapAdjustment() == 0) {
            final byte[] bytes = byteArray();
            if (bytes != null) {
                return bytes.clone();
            }
            final java.nio.ByteBuffer byteBuffer = nioByteBuffer();
            if (byteBuffer != null && byteBuffer.hasArray()) {
                final byte[] array = byteBuffer.array();
                if (array != null) {
                    return array.clone();
                }
            }
        }
        return ByteBuffers.asByteArrayCopyGet(this, 0, capacity());
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

    private IMutableSlicedDelegateCloseableByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = new FixedMutableSlicedDelegateCloseableByteBufferFactory(this);
        }
        return mutableSliceFactory;
    }

    @Override
    public ICloseableByteBuffer sliceFrom(final int index) {
        return getMutableSliceFactory().sliceFrom(index);
    }

    @Override
    public ICloseableByteBuffer slice(final int index, final int length) {
        return getMutableSliceFactory().slice(index, length);
    }

    @Override
    public IByteBuffer newSliceFrom(final int index) {
        if (index == 0) {
            return this;
        }
        return new UnsafeByteBuffer(this, index, remaining(index));
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
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
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else {
            final int limit = index + length;
            for (int i = index; i < limit; i++) {
                final byte b = getByte(i);
                dst.write(b);
            }
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
            final int limit = index + length;
            for (int i = index; i < limit; i++) {
                final byte b = getByte(i);
                dst.write(b);
            }
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else {
            final int limit = index + length;
            for (int i = index; i < limit; i++) {
                final byte b = src.readByte();
                putByte(i, b);
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

            final int limit = index + length;
            for (int i = index; i < limit;) {
                final int result = src.read();
                if (result < 0) { // EOF
                    throw ByteBuffers.newEOF();
                }
                if (result == 0 && timeout != null) {
                    if (zeroCountNanos == -1) {
                        zeroCountNanos = FDateNanos.elapsedNanos();
                    } else if (timeout.isLessThanNanos(FDateNanos.elapsedNanos() - zeroCountNanos)) {
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
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        OutputStreams.writeFullyNoTimeout(dst, asNioByteBuffer(index, length));
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        InputStreams.readFullyNoTimeout(src, asNioByteBuffer(index, length));
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
    public char getCharReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return super.getChar(index);
        }
    }
    @Override
    public char getCharVolatile(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShortVolatile(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return super.getCharVolatile(index);
        }
    }
    @Override
    public char getCharVolatileReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShortVolatile(index);
            return (char) Short.reverseBytes(bits);
        } else {
            return super.getCharVolatile(index);
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
    public void putCharReverse(final int index, final char value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            super.putShort(index, bits);
        } else {
            super.putChar(index, value);
        }
    }
    @Override
    public void putCharVolatile(final int index, final char value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            super.putShortVolatile(index, bits);
        } else {
            super.putCharVolatile(index, value);
        }
    }
    @Override
    public void putCharVolatileReverse(final int index, final char value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes((short) value);
            super.putShortVolatile(index, bits);
        } else {
            super.putCharVolatile(index, value);
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
    public short getShortReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShort(index);
            return Short.reverseBytes(bits);
        } else {
            return super.getShort(index);
        }
    }
    @Override
    public short getShortVolatile(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShortVolatile(index);
            return Short.reverseBytes(bits);
        } else {
            return super.getShortVolatile(index);
        }
    }
    @Override
    public short getShortVolatileReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = super.getShortVolatile(index);
            return Short.reverseBytes(bits);
        } else {
            return super.getShortVolatile(index);
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
    public void putShortReverse(final int index, final short value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            super.putShort(index, bits);
        } else {
            super.putShort(index, value);
        }
    }
    @Override
    public void putShortVolatile(final int index, final short value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            super.putShortVolatile(index, bits);
        } else {
            super.putShortVolatile(index, value);
        }
    }
    @Override
    public void putShortVolatileReverse(final int index, final short value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final short bits = Short.reverseBytes(value);
            super.putShortVolatile(index, bits);
        } else {
            super.putShortVolatile(index, value);
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
    public int getIntReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Integer.reverseBytes(bits);
        } else {
            return super.getInt(index);
        }
    }
    @Override
    public int getIntVolatile(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getIntVolatile(index);
            return Integer.reverseBytes(bits);
        } else {
            return super.getIntVolatile(index);
        }
    }
    @Override
    public int getIntVolatileReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getIntVolatile(index);
            return Integer.reverseBytes(bits);
        } else {
            return super.getIntVolatile(index);
        }
    }
    @Override
    public int getIntAcquire(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getIntAcquire(index);
            return Integer.reverseBytes(bits);
        } else {
            return super.getIntAcquire(index);
        }
    }
    @Override
    public int getIntAcquireReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getIntAcquire(index);
            return Integer.reverseBytes(bits);
        } else {
            return super.getIntAcquire(index);
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
    public void putIntReverse(final int index, final int value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            super.putInt(index, bits);
        } else {
            super.putInt(index, value);
        }
    }
    @Override
    public void putIntVolatile(final int index, final int value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            super.putIntVolatile(index, bits);
        } else {
            super.putIntVolatile(index, value);
        }
    }
    @Override
    public void putIntVolatileReverse(final int index, final int value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            super.putIntVolatile(index, bits);
        } else {
            super.putIntVolatile(index, value);
        }
    }
    @Override
    public void putIntRelease(final int index, final int value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            super.putIntRelease(index, bits);
        } else {
            super.putIntRelease(index, value);
        }
    }
    @Override
    public void putIntReleaseReverse(final int index, final int value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(value);
            super.putIntRelease(index, bits);
        } else {
            super.putIntRelease(index, value);
        }
    }
    /**
     * Ordered store: plain store with a release-ordered fence (agrona idiom). Routing through this class's own plain
     * writer guarantees the identical byte layout at an index that the volatile and release family produce, which the
     * delegate's ordered primitive alone does not.
     */
    @Override
    public void putIntOrdered(final int index, final int value) {
        putInt(index, value);
        UnsafeApi.storeFence();
    }
    /**
     * Ordered store: value-swapped plain store with a release-ordered fence (agrona idiom), the little-endian twin of
     * putIntOrdered(int, int) on this class - routing through this class's own Reverse writer guarantees the identical
     * byte layout at an index that the volatile and release Reverse twins produce.
     */
    @Override
    public void putIntOrderedReverse(final int index, final int value) {
        putIntReverse(index, value);
        UnsafeApi.storeFence();
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
    public long getLongReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Long.reverseBytes(bits);
        } else {
            return super.getLong(index);
        }
    }
    @Override
    public long getLongVolatile(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLongVolatile(index);
            return Long.reverseBytes(bits);
        } else {
            return super.getLongVolatile(index);
        }
    }
    @Override
    public long getLongVolatileReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLongVolatile(index);
            return Long.reverseBytes(bits);
        } else {
            return super.getLongVolatile(index);
        }
    }
    @Override
    public long getLongAcquire(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLongAcquire(index);
            return Long.reverseBytes(bits);
        } else {
            return super.getLongAcquire(index);
        }
    }
    @Override
    public long getLongAcquireReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLongAcquire(index);
            return Long.reverseBytes(bits);
        } else {
            return super.getLongAcquire(index);
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
    public void putLongReverse(final int index, final long value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            super.putLong(index, bits);
        } else {
            super.putLong(index, value);
        }
    }
    @Override
    public void putLongVolatile(final int index, final long value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            super.putLongVolatile(index, bits);
        } else {
            super.putLongVolatile(index, value);
        }
    }
    @Override
    public void putLongVolatileReverse(final int index, final long value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            super.putLongVolatile(index, bits);
        } else {
            super.putLongVolatile(index, value);
        }
    }
    @Override
    public void putLongRelease(final int index, final long value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            super.putLongRelease(index, bits);
        } else {
            super.putLongRelease(index, value);
        }
    }
    @Override
    public void putLongReleaseReverse(final int index, final long value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(value);
            super.putLongRelease(index, bits);
        } else {
            super.putLongRelease(index, value);
        }
    }
    /**
     * Ordered store: plain store with a release-ordered fence (agrona idiom). Routing through this class's own plain
     * writer guarantees the identical byte layout at an index that the volatile and release family produce, which the
     * delegate's ordered primitive alone does not.
     */
    @Override
    public void putLongOrdered(final int index, final long value) {
        putLong(index, value);
        UnsafeApi.storeFence();
    }
    /**
     * Ordered store: value-swapped plain store with a release-ordered fence (agrona idiom), the little-endian twin of
     * putLongOrdered(int, long) on this class - routing through this class's own Reverse writer guarantees the
     * identical byte layout at an index that the volatile and release Reverse twins produce.
     */
    @Override
    public void putLongOrderedReverse(final int index, final long value) {
        putLongReverse(index, value);
        UnsafeApi.storeFence();
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
    public float getFloatReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getInt(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            return super.getFloat(index);
        }
    }
    @Override
    public float getFloatVolatile(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getIntVolatile(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            //agrona has no volatile float - raw bits over the volatile int keeps the full fence and the NaN payload
            return Float.intBitsToFloat(super.getIntVolatile(index));
        }
    }
    @Override
    public float getFloatVolatileReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = super.getIntVolatile(index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        } else {
            //agrona has no volatile float - raw bits over the volatile int keeps the full fence and the NaN payload
            return Float.intBitsToFloat(super.getIntVolatile(index));
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
    public void putFloatReverse(final int index, final float value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            super.putInt(index, bits);
        } else {
            super.putFloat(index, value);
        }
    }
    @Override
    public void putFloatVolatile(final int index, final float value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            super.putIntVolatile(index, bits);
        } else {
            //agrona has no volatile float - raw bits over the volatile int keeps the full fence and the NaN payload
            super.putIntVolatile(index, Float.floatToRawIntBits(value));
        }
    }
    @Override
    public void putFloatVolatileReverse(final int index, final float value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            super.putIntVolatile(index, bits);
        } else {
            //agrona has no volatile float - raw bits over the volatile int keeps the full fence and the NaN payload
            super.putIntVolatile(index, Float.floatToRawIntBits(value));
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
    public double getDoubleReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLong(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            return super.getDouble(index);
        }
    }
    @Override
    public double getDoubleVolatile(final int index) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLongVolatile(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            //agrona has no volatile double - raw bits over the volatile long keeps the full fence and the NaN payload
            return Double.longBitsToDouble(super.getLongVolatile(index));
        }
    }
    @Override
    public double getDoubleVolatileReverse(final int index) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = super.getLongVolatile(index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        } else {
            //agrona has no volatile double - raw bits over the volatile long keeps the full fence and the NaN payload
            return Double.longBitsToDouble(super.getLongVolatile(index));
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
    public void putDoubleReverse(final int index, final double value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            super.putLong(index, bits);
        } else {
            super.putDouble(index, value);
        }
    }
    @Override
    public void putDoubleVolatile(final int index, final double value) {
        if (ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            super.putLongVolatile(index, bits);
        } else {
            //agrona has no volatile double - raw bits over the volatile long keeps the full fence and the NaN payload
            super.putLongVolatile(index, Double.doubleToRawLongBits(value));
        }
    }
    @Override
    public void putDoubleVolatileReverse(final int index, final double value) {
        if (ByteBuffers.LITTLE_ENDIAN_REVERSAL_NEEDED) {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            super.putLongVolatile(index, bits);
        } else {
            //agrona has no volatile double - raw bits over the volatile long keeps the full fence and the NaN payload
            super.putLongVolatile(index, Double.doubleToRawLongBits(value));
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

    @Override
    public int wrapAdjustment() {
        if (byteArray() == null && byteBuffer() == null) {
            //nullpointer workaround when we have no byteBuffer, thus wrapAdjustment=0
            return 0;
        } else {
            return super.wrapAdjustment();
        }
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer() {
        final java.nio.ByteBuffer byteBuffer = nioByteBuffer();
        final int wrapAdjustment = wrapAdjustment();
        if (byteBuffer != null) {
            if (wrapAdjustment == 0 && capacity() == byteBuffer.capacity()) {
                return byteBuffer;
            } else {
                return ByteBuffers.slice(byteBuffer, wrapAdjustment, capacity());
            }
        }
        final byte[] array = byteArray();
        if (array != null) {
            final java.nio.ByteBuffer arrayBuffer = java.nio.ByteBuffer.wrap(array, wrapAdjustment, capacity());
            return arrayBuffer;
        }
        final long address = addressOffset();
        return UninitializedDirectByteBuffers.asDirectByteBufferNoCleaner(address, capacity());
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        final java.nio.ByteBuffer buffer = asNioByteBuffer();
        if (index == 0 && length == capacity()) {
            return buffer;
        } else {
            return ByteBuffers.slice(buffer, index, length);
        }
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        super.setMemory(index, length, value);
    }

    @Override
    public String toString() {
        return ByteBuffers.toString(this);
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
    public ICloseableByteBuffer ensureCapacity(final int desiredCapacity) {
        if (desiredCapacity > capacity()) {
            checkLimit(desiredCapacity);
        }
        return this;
    }

    @Override
    public ICloseableByteBuffer asImmutableSlice() {
        return this;
    }

    @Override
    public void close() {
        wrap(Bytes.EMPTY_ARRAY);
    }

}
