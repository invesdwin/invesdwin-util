package de.invesdwin.util.streams.buffer.bytes.delegate;

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
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.collections.array.base.IBaseArrayId;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.stream.ByteBufferInputStream;
import de.invesdwin.util.streams.buffer.bytes.stream.ByteBufferOutputStream;
import de.invesdwin.util.streams.buffer.bytes.stream.ExpandableByteBufferOutputStream;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ByteDelegateMemoryBuffer;

@NotThreadSafe
public class SegmentedByteBuffer implements IByteBuffer {

    private final IByteBuffer[] segments;
    private final int segmentSize;

    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public SegmentedByteBuffer(final IByteBuffer[] segments) {
        this(segments, segments[0].capacity());
    }

    public SegmentedByteBuffer(final IByteBuffer[] segments, final int segmentSize) {
        if (segments.length == 0) {
            throw new IllegalArgumentException("At least one segment is required, even if it is just an empty segment");
        }
        this.segments = segments;
        this.segmentSize = segmentSize;
        //just document expectations but skip this check internally
        //        assertSegments(this.segments, this.segmentSize);
    }

    public static void assertSegments(final IByteBuffer[] segments, final int segmentSize) {
        final int lastSegmentIndex = segments.length - 1;
        for (int i = 0; i <= lastSegmentIndex; i++) {
            final IByteBuffer segment = segments[i];
            if (i < lastSegmentIndex) {
                final long segmentCapacity = segment.capacity();
                if (segmentCapacity != segmentSize) {
                    throw new IllegalArgumentException(
                            "All segments except last one must have the same capacity: segmentIndex=" + i
                                    + " segmentCapacity=" + segmentCapacity + " expectedSegmentSize=" + segmentSize);
                }
            }
            final ByteOrder segmentOrder = segment.getOrder();
            if (segmentOrder != ByteBuffers.DEFAULT_ORDER) {
                throw new IllegalArgumentException("All segments must have the same byte order: segmentIndex=" + i
                        + " segmentByteOrder=" + segmentOrder + " expectedByteOrder=" + ByteBuffers.DEFAULT_ORDER);
            }
        }
    }

    @Override
    public int getId() {
        return IBaseArrayId.newId(segments);
    }

    public IByteBuffer[] getSegments() {
        return segments;
    }

    private int getSegmentSize() {
        return segmentSize;
    }

    private int getSegmentIndex(final int index) {
        return getSegmentIndex(index, segmentSize);
    }

    private int getSegmentOffset(final int index) {
        return getSegmentOffset(index, segmentSize);
    }

    public static int getSegmentIndex(final int index, final int segmentSize) {
        return index / segmentSize;
    }

    public static int getSegmentOffset(final int index, final int segmentSize) {
        return index % segmentSize;
    }

    public static int getSegmentIndex(final long index, final int segmentSize) {
        return ByteBuffers.checkedCast(index / segmentSize);
    }

    public static int getSegmentOffset(final long index, final int segmentSize) {
        return ByteBuffers.checkedCast(index % segmentSize);
    }

    public static int getSegmentOffset(final long index, final long segmentSize) {
        return ByteBuffers.checkedCast(index % segmentSize);
    }

    private IMutableSlicedDelegateByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateByteBufferFactory.newInstance(this);
        }
        return mutableSliceFactory;
    }

    private void ensureCapacity(final int index, final int length) {
        if (index < 0 || length < 0) {
            throw FastIndexOutOfBoundsException.getInstance("negative value: index=%s length=%s", index, length);
        }

        final long resultingPosition = index + (long) length;
        final int currentArrayLength = capacity();
        if (resultingPosition > currentArrayLength) {
            if (resultingPosition > ExpandableArrayBuffer.MAX_ARRAY_LENGTH) {
                throw FastIndexOutOfBoundsException.getInstance("index=%s length=%s maxCapacity=%s", index, length,
                        ExpandableArrayBuffer.MAX_ARRAY_LENGTH);
            }

            ensureCapacity((int) resultingPosition);
        }
    }

    @Override
    public IByteBuffer ensureCapacity(final int capacity) {
        if (segments.length == 1) {
            segments[0].ensureCapacity(capacity);
        } else {
            final int lastSegmentIndex = segments.length - 1;
            final int precedingCapacity = lastSegmentIndex * getSegmentSize();
            final int lastSegmentCapacity = capacity - precedingCapacity;
            final IByteBuffer lastBuffer = segments[lastSegmentIndex];
            lastBuffer.ensureCapacity(lastSegmentCapacity);
        }
        return this;
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public boolean isReadOnly() {
        return segments[0].isReadOnly();
    }

    @Override
    public long addressOffset() {
        if (segments.length == 1) {
            return segments[0].addressOffset();
        } else {
            return 0;
        }
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        if (segments.length == 1) {
            return segments[0].directBuffer();
        } else {
            return null;
        }
    }

    @Override
    public byte[] byteArray() {
        if (segments.length == 1) {
            return segments[0].byteArray();
        } else {
            return null;
        }
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        if (segments.length == 1) {
            return segments[0].nioByteBuffer();
        } else {
            return null;
        }
    }

    @Override
    public int capacity() {
        if (segments.length == 1) {
            return segments[0].capacity();
        } else {
            final int lastSegmentIndex = segments.length - 1;
            final int precedingCapacity = lastSegmentIndex * getSegmentSize();
            final IByteBuffer lastBuffer = segments[lastSegmentIndex];
            return lastBuffer.capacity() + precedingCapacity;
        }
    }

    @Override
    public long getLong(final int index) {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + Long.BYTES) {
            return buffer.getLong(bufferPosition);
        } else {
            final byte[] readBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
            final int limit = index + Long.BYTES;
            for (int i = index, ri = 0; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                readBuffer[ri] = buffer.getByte(bufferPosition);
                i++;
                ri++;
                bufferPosition++;
            }
            //CHECKSTYLE:OFF
            return (((long) readBuffer[0] << 56) + ((readBuffer[1] & 0xFFL) << 48) + ((readBuffer[2] & 0xFFL) << 40)
                    + ((readBuffer[3] & 0xFFL) << 32) + ((readBuffer[4] & 0xFFL) << 24)
                    + ((readBuffer[5] & 0xFFL) << 16) + ((readBuffer[6] & 0xFFL) << 8)
                    + ((readBuffer[7] & 0xFFL) << 0));
            //CHECKSTYLE:ON
        }
    }

    @Override
    public long getLongReverse(final int index) {
        final long bits = getLong(index);
        return Long.reverseBytes(bits);
    }

    @Override
    public int getInt(final int index) {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + Integer.BYTES) {
            return buffer.getInt(bufferPosition);
        } else {
            final byte[] readBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
            final int limit = index + Integer.BYTES;
            for (int i = index, ri = 0; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                readBuffer[ri] = buffer.getByte(bufferPosition);
                i++;
                ri++;
                bufferPosition++;
            }
            return ((readBuffer[0] << 24) + ((readBuffer[1] & 0xFF) << 16) + ((readBuffer[2] & 0xFF) << 8)
                    + ((readBuffer[3] & 0xFF)));
        }
    }

    @Override
    public int getIntReverse(final int index) {
        final int bits = getInt(index);
        return Integer.reverseBytes(bits);
    }

    @Override
    public double getDouble(final int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    @Override
    public double getDoubleReverse(final int index) {
        final long bits = getLong(index);
        return Double.longBitsToDouble(Long.reverseBytes(bits));
    }

    @Override
    public float getFloat(final int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    @Override
    public float getFloatReverse(final int index) {
        final int bits = getInt(index);
        return Float.intBitsToFloat(Integer.reverseBytes(bits));
    }

    @Override
    public short getShort(final int index) {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + Short.BYTES) {
            return buffer.getShort(bufferPosition);
        } else {
            final byte[] readBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
            final int limit = index + Short.BYTES;
            for (int i = index, ri = 0; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                readBuffer[ri] = buffer.getByte(bufferPosition);
                i++;
                ri++;
                bufferPosition++;
            }
            return (short) ((readBuffer[0] << 8) + (readBuffer[1] & 0xFF));
        }
    }

    @Override
    public short getShortReverse(final int index) {
        final short bits = getShort(index);
        return Short.reverseBytes(bits);
    }

    @Override
    public char getChar(final int index) {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + Character.BYTES) {
            return buffer.getChar(bufferPosition);
        } else {
            final byte[] readBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
            final int limit = index + Character.BYTES;
            for (int i = index, ri = 0; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                readBuffer[ri] = buffer.getByte(bufferPosition);
                i++;
                ri++;
                bufferPosition++;
            }
            return (char) ((readBuffer[0] << 8) + (readBuffer[1] & 0xFF));
        }
    }

    @Override
    public char getCharReverse(final int index) {
        final short bits = (short) getChar(index);
        return (char) Short.reverseBytes(bits);
    }

    @Override
    public byte getByte(final int index) {
        final int buf = getSegmentIndex(index);

        final IByteBuffer buffer = segments[buf];

        final int bufferPosition = getSegmentOffset(index);
        return buffer.getByte(bufferPosition);
    }

    @Override
    public int wrapAdjustment() {
        if (segments.length == 1) {
            return segments[0].wrapAdjustment();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isExpandable() {
        final IByteBuffer lastBuffer = segments[segments.length - 1];
        return lastBuffer.isExpandable();
    }

    @Override
    public void putLong(final int index, final long value) {
        final byte[] writeBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
        writeBuffer[0] = (byte) (value >>> 56);
        writeBuffer[1] = (byte) (value >>> 48);
        writeBuffer[2] = (byte) (value >>> 40);
        writeBuffer[3] = (byte) (value >>> 32);
        writeBuffer[4] = (byte) (value >>> 24);
        writeBuffer[5] = (byte) (value >>> 16);
        writeBuffer[6] = (byte) (value >>> 8);
        writeBuffer[7] = (byte) (value);
        putBytes(index, writeBuffer);
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        final long bits = Long.reverseBytes(value);
        putLong(index, bits);
    }

    @Override
    public void putInt(final int index, final int value) {
        final byte[] writeBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
        writeBuffer[0] = (byte) (value >>> 24);
        writeBuffer[1] = (byte) (value >>> 16);
        writeBuffer[2] = (byte) (value >>> 8);
        writeBuffer[3] = (byte) (value);
        putBytes(index, writeBuffer, 0, Integer.BYTES);
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        final int bits = Integer.reverseBytes(value);
        putInt(index, bits);
    }

    @Override
    public void putDouble(final int index, final double value) {
        putLong(index, Double.doubleToLongBits(value));
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
        putLong(index, bits);
    }

    @Override
    public void putFloat(final int index, final float value) {
        putInt(index, Float.floatToIntBits(value));
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
        putInt(index, bits);
    }

    @Override
    public void putShort(final int index, final short value) {
        final byte[] writeBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
        writeBuffer[0] = (byte) (value >>> 8);
        writeBuffer[1] = (byte) (value);
        putBytes(index, writeBuffer, 0, Short.BYTES);
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        final short bits = Short.reverseBytes(value);
        putShort(index, bits);
    }

    @Override
    public void putChar(final int index, final char value) {
        final byte[] writeBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
        writeBuffer[0] = (byte) (value >>> 8);
        writeBuffer[1] = (byte) (value);
        putBytes(index, writeBuffer, 0, Short.BYTES);
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        final short bits = Short.reverseBytes((short) value);
        putShort(index, bits);
    }

    @Override
    public void putByte(final int index, final byte value) {
        final int buf = getSegmentIndex(index);

        final IByteBuffer buffer = segments[buf];

        final int bufferPosition = getSegmentOffset(index);
        buffer.putByte(bufferPosition, value);
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return new ByteBufferInputStream(this, index, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        if (isExpandable() && index + length >= capacity()) {
            //allow output stream to actually grow the buffer
            return new ExpandableByteBufferOutputStream(this, index);
        } else {
            return new ByteBufferOutputStream(this, index, length);
        }
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        if (segments.length == 1) {
            return segments[0].asByteArray(index, length);
        } else {
            final byte[] byteArray = ByteBuffers.allocateByteArray(length);
            getBytes(index, byteArray, 0, length);
            return byteArray;
        }
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        if (segments.length == 1) {
            return segments[0].asNioByteBuffer(index, length);
        } else {
            final int startSegmentIndex = getSegmentIndex(index);
            final int endSegmentIndex = getSegmentIndex(index + length - 1);
            if (startSegmentIndex == endSegmentIndex) {
                return segments[startSegmentIndex].asNioByteBuffer(getSegmentOffset(index), length);
            } else {
                //WARNING: this is not a mutable version of the underlying memory
                final java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocate(length);
                getBytes(index, byteBuffer, 0, length);
                return byteBuffer;
            }
        }
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        if (segments.length == 1) {
            return segments[0].asByteArrayCopy(index, length);
        } else {
            final byte[] byteArray = ByteBuffers.allocateByteArray(length);
            getBytes(index, byteArray, 0, length);
            return byteArray;
        }
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        return asDirectBuffer(0, capacity());
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        if (segments.length == 1) {
            return segments[0].asDirectBuffer(index, length);
        } else {
            final UnsafeBuffer directBuffer = new UnsafeBuffer(ByteBuffers.allocateByteArray(length));
            getBytes(index, directBuffer, 0, length);
            return directBuffer;
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
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
        getBytes(index, bytes, 0, length);
        return ByteBuffers.newStringAscii(bytes);
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.getStringAsciii(bufferPosition, length, dst);
        } else {
            try {
                final int limit = index + length;
                for (int i = index; i < limit;) {
                    if (bufferPosition >= capacity) {
                        buf++;
                        buffer = segments[buf];
                        capacity = buffer.capacity();
                        bufferPosition = 0;
                    }
                    final char c = (char) buffer.getByte(bufferPosition);
                    dst.append(c > 127 ? '?' : c);
                    i++;
                    bufferPosition++;
                }
            } catch (final IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        ensureCapacity(index, length);
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.putStringAsciii(bufferPosition, value, valueIndex, length);
        } else {
            final int limit = index + length;
            for (int i = index, vi = valueIndex; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                char c = value.charAt(vi);
                if (c > 127) {
                    c = '?';
                }
                buffer.putByte(bufferPosition, (byte) c);
                i++;
                vi++;
                bufferPosition++;
            }
        }
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        final byte[] bytes = ByteBuffers.newStringUtf8Bytes(value);
        ensureCapacity(index, bytes.length);
        putBytes(index, bytes);
        return bytes.length;
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
        getBytes(index, bytes, 0, length);
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
            int buf = getSegmentIndex(index);
            IByteBuffer buffer = segments[buf];
            int capacity = buffer.capacity();
            int bufferPosition = getSegmentOffset(index);
            if (capacity >= bufferPosition + length) {
                buffer.getBytesTo(bufferPosition, dst, length);
            } else {
                try {
                    final int limit = index + length;
                    int remaining = length;
                    for (int i = index; i < limit;) {
                        if (bufferPosition >= capacity) {
                            buf++;
                            buffer = segments[buf];
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                        buffer.getBytesTo(bufferPosition, dst, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                    }
                } catch (final IOException e) {
                    throw Throwables.propagate(e);
                }
            }
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else {
            ensureCapacity(index, length);
            int buf = getSegmentIndex(index);
            IByteBuffer buffer = segments[buf];
            int capacity = buffer.capacity();
            int bufferPosition = getSegmentOffset(index);
            if (capacity >= bufferPosition + length) {
                buffer.putBytesTo(bufferPosition, src, length);
            } else {
                try {
                    final int limit = index + length;
                    int remaining = length;
                    for (int i = index; i < limit;) {
                        if (bufferPosition >= capacity) {
                            buf++;
                            buffer = segments[buf];
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                        buffer.putBytesTo(bufferPosition, src, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                    }
                } catch (final IOException e) {
                    throw Throwables.propagate(e);
                }
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
            ensureCapacity(index, length);
            int buf = getSegmentIndex(index);
            IByteBuffer buffer = segments[buf];
            int capacity = buffer.capacity();
            int bufferPosition = getSegmentOffset(index);
            if (capacity >= bufferPosition + length) {
                buffer.putBytesTo(bufferPosition, src, length);
            } else {
                try {
                    final int limit = index + length;
                    int remaining = length;
                    for (int i = index; i < limit;) {
                        if (bufferPosition >= capacity) {
                            buf++;
                            buffer = segments[buf];
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                        buffer.putBytesTo(bufferPosition, src, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                    }
                } catch (final IOException e) {
                    throw Throwables.propagate(e);
                }
            }
        }
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        ensureCapacity(index, length);
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.putBytesTo(bufferPosition, src, length);
        } else {
            try {
                final int limit = index + length;
                int remaining = length;
                for (int i = index; i < limit;) {
                    if (bufferPosition >= capacity) {
                        buf++;
                        buffer = segments[buf];
                        capacity = buffer.capacity();
                        bufferPosition = 0;
                    }
                    final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                    buffer.putBytesTo(bufferPosition, src, toCopy);
                    remaining -= toCopy;
                    i += toCopy;
                    bufferPosition += toCopy;
                }
            } catch (final IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else {
            int buf = getSegmentIndex(index);
            IByteBuffer buffer = segments[buf];
            int capacity = buffer.capacity();
            int bufferPosition = getSegmentOffset(index);
            if (capacity >= bufferPosition + length) {
                buffer.getBytesTo(bufferPosition, dst, length);
            } else {
                try {
                    final int limit = index + length;
                    int remaining = length;
                    for (int i = index; i < limit;) {
                        if (bufferPosition >= capacity) {
                            buf++;
                            buffer = segments[buf];
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                        buffer.getBytesTo(bufferPosition, dst, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                    }
                } catch (final IOException e) {
                    throw Throwables.propagate(e);
                }
            }
        }
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.getBytesTo(bufferPosition, dst, length);
        } else {
            try {
                final int limit = index + length;
                int remaining = length;
                for (int i = index; i < limit;) {
                    if (bufferPosition >= capacity) {
                        buf++;
                        buffer = segments[buf];
                        capacity = buffer.capacity();
                        bufferPosition = 0;
                    }
                    final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                    buffer.getBytesTo(bufferPosition, dst, toCopy);
                    remaining -= toCopy;
                    i += toCopy;
                    bufferPosition += toCopy;
                }
            } catch (final IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.putBytes(bufferPosition, src, srcIndex, length);
        } else {
            final int limit = index + length;
            int remaining = length;
            int srcPosition = srcIndex;
            for (int i = index; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                buffer.putBytes(bufferPosition, src, srcPosition, toCopy);
                remaining -= toCopy;
                i += toCopy;
                bufferPosition += toCopy;
                srcPosition += toCopy;
            }
        }
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.putBytes(bufferPosition, srcBuffer, srcIndex, length);
        } else {
            final int limit = index + length;
            int remaining = length;
            int srcPosition = srcIndex;
            for (int i = index; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                buffer.putBytes(bufferPosition, srcBuffer, srcPosition, toCopy);
                remaining -= toCopy;
                i += toCopy;
                bufferPosition += toCopy;
                srcPosition += toCopy;
            }
        }
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.putBytes(bufferPosition, srcBuffer, srcIndex, length);
        } else {
            final int limit = index + length;
            int remaining = length;
            int srcPosition = srcIndex;
            for (int i = index; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                buffer.putBytes(bufferPosition, srcBuffer, srcPosition, toCopy);
                remaining -= toCopy;
                i += toCopy;
                bufferPosition += toCopy;
                srcPosition += toCopy;
            }
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.putBytes(bufferPosition, srcBuffer, srcIndex, length);
        } else {
            final int limit = index + length;
            int remaining = length;
            int srcPosition = srcIndex;
            for (int i = index; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                buffer.putBytes(bufferPosition, srcBuffer, srcPosition, toCopy);
                remaining -= toCopy;
                i += toCopy;
                bufferPosition += toCopy;
                srcPosition += toCopy;
            }
        }
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        ensureCapacity(index, length);
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.putBytes(bufferPosition, srcBuffer, srcIndex, length);
        } else {
            final int limit = index + length;
            int remaining = length;
            long srcPosition = srcIndex;
            for (int i = index; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                buffer.putBytes(bufferPosition, srcBuffer, srcPosition, toCopy);
                remaining -= toCopy;
                i += toCopy;
                bufferPosition += toCopy;
                srcPosition += toCopy;
            }
        }
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.getBytes(bufferPosition, dst, dstIndex, length);
        } else {
            final int limit = index + length;
            int remaining = length;
            int dstPosition = dstIndex;
            for (int i = index; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                buffer.getBytes(bufferPosition, dst, dstPosition, toCopy);
                remaining -= toCopy;
                i += toCopy;
                bufferPosition += toCopy;
                dstPosition += toCopy;
            }
        }
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.getBytes(bufferPosition, dstBuffer, dstIndex, length);
        } else {
            final int limit = index + length;
            int remaining = length;
            int dstPosition = dstIndex;
            for (int i = index; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                buffer.getBytes(bufferPosition, dstBuffer, dstPosition, toCopy);
                remaining -= toCopy;
                i += toCopy;
                bufferPosition += toCopy;
                dstPosition += toCopy;
            }
        }
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.getBytes(bufferPosition, dstBuffer, dstIndex, length);
        } else {
            final int limit = index + length;
            int remaining = length;
            int dstPosition = dstIndex;
            for (int i = index; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                buffer.getBytes(bufferPosition, dstBuffer, dstPosition, toCopy);
                remaining -= toCopy;
                i += toCopy;
                bufferPosition += toCopy;
                dstPosition += toCopy;
            }
        }
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.getBytes(bufferPosition, dstBuffer, dstIndex, length);
        } else {
            final int limit = index + length;
            int remaining = length;
            int dstPosition = dstIndex;
            for (int i = index; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                buffer.getBytes(bufferPosition, dstBuffer, dstPosition, toCopy);
                remaining -= toCopy;
                i += toCopy;
                bufferPosition += toCopy;
                dstPosition += toCopy;
            }
        }
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.getBytes(bufferPosition, dstBuffer, dstIndex, length);
        } else {
            final int limit = index + length;
            int remaining = length;
            long dstPosition = dstIndex;
            for (int i = index; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                buffer.getBytes(bufferPosition, dstBuffer, dstPosition, toCopy);
                remaining -= toCopy;
                i += toCopy;
                bufferPosition += toCopy;
                dstPosition += toCopy;
            }
        }
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        int buf = getSegmentIndex(index);
        IByteBuffer buffer = segments[buf];
        int capacity = buffer.capacity();
        int bufferPosition = getSegmentOffset(index);
        if (capacity >= bufferPosition + length) {
            buffer.clear(value, bufferPosition, length);
        } else {
            final int limit = index + length;
            int remaining = length;
            for (int i = index; i < limit;) {
                if (bufferPosition >= capacity) {
                    buf++;
                    buffer = segments[buf];
                    capacity = buffer.capacity();
                    bufferPosition = 0;
                }
                final int toCopy = Integers.min(remaining, buffer.remaining(bufferPosition));
                buffer.clear(value, bufferPosition, toCopy);
                remaining -= toCopy;
                i += toCopy;
                bufferPosition += toCopy;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        if (segments.length == 1) {
            final IByteBuffer delegate = segments[0];
            if (delegate.getClass().isAssignableFrom(type)) {
                return (T) delegate;
            }
        }
        return null;
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
    public IByteBuffer asImmutableSlice() {
        return this;
    }

}
