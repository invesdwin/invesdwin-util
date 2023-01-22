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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.EmptyByteBuffer;
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
public class ArrayListByteBuffer implements IByteBuffer {

    private final List<IByteBuffer> list = new ArrayList<>();
    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    private IMutableSlicedDelegateByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateByteBufferFactory.newInstance(this);
        }
        return mutableSliceFactory;
    }

    private void ensureCapacity(final int index, final int length) {
        if (index < 0 || length < 0) {
            throw new IndexOutOfBoundsException("negative value: index=" + index + " length=" + length);
        }

        final long resultingPosition = index + (long) length;
        final int currentArrayLength = capacity();
        if (resultingPosition > currentArrayLength) {
            if (resultingPosition > ExpandableArrayBuffer.MAX_ARRAY_LENGTH) {
                throw new IndexOutOfBoundsException("index=" + index + " length=" + length + " maxCapacity="
                        + ExpandableArrayBuffer.MAX_ARRAY_LENGTH);
            }

            ensureCapacity((int) resultingPosition);
        }
    }

    @Override
    public IByteBuffer ensureCapacity(final int capacity) {
        if (list.isEmpty()) {
            throw EmptyByteBuffer.newEmptyException();
        } else if (list.size() == 1) {
            list.get(0).ensureCapacity(capacity);
        } else {
            int remaining = capacity;
            for (int buf = 0; buf < list.size(); buf++) {
                final IByteBuffer buffer = list.get(buf);
                remaining -= buffer.capacity();
            }
            if (remaining > 0) {
                final IByteBuffer lastBuffer = list.get(list.size() - 1);
                lastBuffer.ensureCapacity(lastBuffer.capacity() + remaining);
            }
        }
        return this;
    }

    @Override
    public ByteOrder getOrder() {
        if (list.isEmpty()) {
            return ByteBuffers.DEFAULT_ORDER;
        } else {
            return list.get(0).getOrder();
        }
    }

    @Override
    public boolean isReadOnly() {
        if (list.isEmpty()) {
            return true;
        } else {
            return list.get(0).isReadOnly();
        }
    }

    @Override
    public long addressOffset() {
        if (list.isEmpty()) {
            return EmptyByteBuffer.INSTANCE.addressOffset();
        } else if (list.size() == 1) {
            return list.get(0).addressOffset();
        } else {
            return 0;
        }
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        if (list.isEmpty()) {
            return EmptyByteBuffer.EMPTY_AGRONA_BUFFER;
        } else if (list.size() == 1) {
            return list.get(0).directBuffer();
        } else {
            return null;
        }
    }

    @Override
    public byte[] byteArray() {
        if (list.isEmpty()) {
            return Bytes.EMPTY_ARRAY;
        } else if (list.size() == 1) {
            return list.get(0).byteArray();
        } else {
            return null;
        }
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        if (list.isEmpty()) {
            return EmptyByteBuffer.EMPTY_BYTE_BUFFER;
        } else if (list.size() == 1) {
            return list.get(0).nioByteBuffer();
        } else {
            return null;
        }
    }

    @Override
    public int capacity() {
        int capacity = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IByteBuffer buffer = list.get(buf);
            capacity += buffer.capacity();
        }
        return capacity;
    }

    @Override
    public long getLong(final int index) {
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IByteBuffer buffer = list.get(buf);
            int capacity = buffer.capacity();
            if (index > position + capacity) {
                position += capacity;
                continue;
            } else {
                int indexMinusPosition = index - position;
                if (capacity > indexMinusPosition + Long.BYTES) {
                    return buffer.getLong(indexMinusPosition);
                } else {
                    final byte[] readBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
                    for (int i = 0; i < Long.BYTES; i++) {
                        while (indexMinusPosition + i > capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            indexMinusPosition = index - position;
                        }
                        readBuffer[i] = buffer.getByte(indexMinusPosition + i);
                    }
                    //CHECKSTYLE:OFF
                    return (((long) readBuffer[0] << 56) + ((long) (readBuffer[1] & 255) << 48)
                            + ((long) (readBuffer[2] & 255) << 40) + ((long) (readBuffer[3] & 255) << 32)
                            + ((long) (readBuffer[4] & 255) << 24) + ((readBuffer[5] & 255) << 16)
                            + ((readBuffer[6] & 255) << 8) + ((readBuffer[7] & 255) << 0));
                    //CHECKSTYLE:ON
                }
            }
        }
        throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
    }

    @Override
    public long getLongReverse(final int index) {
        final long bits = getLong(index);
        return Long.reverseBytes(bits);
    }

    @Override
    public int getInt(final int index) {
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IByteBuffer buffer = list.get(buf);
            int capacity = buffer.capacity();
            if (index > position + capacity) {
                position += capacity;
                continue;
            } else {
                int indexMinusPosition = index - position;
                if (capacity > indexMinusPosition + Integer.BYTES) {
                    return buffer.getInt(indexMinusPosition);
                } else {
                    final byte[] readBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
                    for (int i = 0; i < Integer.BYTES; i++) {
                        while (indexMinusPosition + i > capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            indexMinusPosition = index - position;
                        }
                        readBuffer[i] = buffer.getByte(indexMinusPosition + i);
                    }
                    return ((readBuffer[0] << 24) + (readBuffer[1] << 16) + (readBuffer[2] << 8)
                            + (readBuffer[3] << 0));
                }
            }
        }
        throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
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
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IByteBuffer buffer = list.get(buf);
            int capacity = buffer.capacity();
            if (index > position + capacity) {
                position += capacity;
                continue;
            } else {
                int indexMinusPosition = index - position;
                if (capacity > indexMinusPosition + Short.BYTES) {
                    return buffer.getShort(indexMinusPosition);
                } else {
                    final byte[] readBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
                    for (int i = 0; i < Short.BYTES; i++) {
                        while (indexMinusPosition + i > capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            indexMinusPosition = index - position;
                        }
                        readBuffer[i] = buffer.getByte(indexMinusPosition + i);
                    }
                    return (short) ((readBuffer[0] << 8) + (readBuffer[1] << 0));
                }
            }
        }
        throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
    }

    @Override
    public short getShortReverse(final int index) {
        final short bits = getShort(index);
        return Short.reverseBytes(bits);
    }

    @Override
    public char getChar(final int index) {
        return (char) getShort(index);
    }

    @Override
    public char getCharReverse(final int index) {
        final short bits = getShort(index);
        return (char) Short.reverseBytes(bits);
    }

    @Override
    public byte getByte(final int index) {
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IByteBuffer buffer = list.get(buf);
            final int capacity = buffer.capacity();
            if (index > position + capacity) {
                position += capacity;
                continue;
            } else {
                return buffer.getByte(index - position);
            }
        }
        throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
    }

    @Override
    public int wrapAdjustment() {
        if (list.isEmpty()) {
            return EmptyByteBuffer.INSTANCE.wrapAdjustment();
        } else if (list.size() == 1) {
            return list.get(0).wrapAdjustment();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isExpandable() {
        if (list.isEmpty()) {
            return false;
        } else {
            final IByteBuffer lastBuffer = list.get(list.size() - 1);
            return lastBuffer.isExpandable();
        }
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
        writeBuffer[7] = (byte) (value >>> 0);
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
        writeBuffer[0] = (byte) ((value >>> 24) & 0xFF);
        writeBuffer[1] = (byte) ((value >>> 16) & 0xFF);
        writeBuffer[2] = (byte) ((value >>> 8) & 0xFF);
        writeBuffer[3] = (byte) ((value >>> 0) & 0xFF);
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
        writeBuffer[0] = (byte) ((value >>> 8) & 0xFF);
        writeBuffer[1] = (byte) ((value >>> 0) & 0xFF);
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
        writeBuffer[0] = (byte) ((value >>> 8) & 0xFF);
        writeBuffer[1] = (byte) ((value >>> 0) & 0xFF);
        putBytes(index, writeBuffer, 0, Short.BYTES);
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        final short bits = Short.reverseBytes((short) value);
        putShort(index, bits);
    }

    @Override
    public void putByte(final int index, final byte value) {
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IByteBuffer buffer = list.get(buf);
            final int capacity = buffer.capacity();
            if (index > position + capacity) {
                position += capacity;
                continue;
            } else {
                buffer.putByte(index - position, value);
                return;
            }
        }
        throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
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
        if (list.isEmpty()) {
            if (index == 0 && length == 0) {
                return Bytes.EMPTY_ARRAY;
            } else {
                throw EmptyByteBuffer.newEmptyException();
            }
        } else if (list.size() == 1) {
            return list.get(0).asByteArray(index, length);
        } else {
            final byte[] byteArray = ByteBuffers.allocateByteArray(length);
            getBytes(index, byteArray, 0, length);
            return byteArray;
        }
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        if (list.isEmpty()) {
            if (index == 0 && length == 0) {
                return EmptyByteBuffer.EMPTY_BYTE_BUFFER;
            } else {
                throw EmptyByteBuffer.newEmptyException();
            }
        } else if (list.size() == 1) {
            return list.get(0).asNioByteBuffer(index, length);
        } else {
            final java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocate(length);
            getBytes(index, byteBuffer, 0, length);
            return byteBuffer;
        }
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        if (list.isEmpty()) {
            if (index == 0 && length == 0) {
                return Bytes.EMPTY_ARRAY;
            } else {
                throw EmptyByteBuffer.newEmptyException();
            }
        } else if (list.size() == 1) {
            return list.get(0).asByteArrayCopy(index, length);
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
        if (list.isEmpty()) {
            if (index == 0 && length == 0) {
                return EmptyByteBuffer.EMPTY_AGRONA_BUFFER;
            } else {
                throw EmptyByteBuffer.newEmptyException();
            }
        } else if (list.size() == 1) {
            return list.get(0).asDirectBuffer(index, length);
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
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IByteBuffer buffer = list.get(buf);
            int capacity = buffer.capacity();
            if (index > position + capacity) {
                position += capacity;
                continue;
            } else {
                int indexMinusPosition = index - position;
                if (capacity > indexMinusPosition + Short.BYTES) {
                    buffer.getStringAsciii(indexMinusPosition, length, dst);
                    return;
                } else {
                    try {
                        final int limit = index + length;
                        for (int i = index; i < limit; i++) {
                            while (indexMinusPosition + i > capacity) {
                                buf++;
                                buffer = list.get(buf);
                                capacity = buffer.capacity();
                                indexMinusPosition = index - position;
                            }
                            final char c = (char) buffer.getByte(indexMinusPosition + i);
                            dst.append(c > 127 ? '?' : c);
                        }
                    } catch (final IOException e) {
                        throw Throwables.propagate(e);
                    }
                    return;
                }
            }
        }
        throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());

    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        ensureCapacity(index, length);
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IByteBuffer buffer = list.get(buf);
            int capacity = buffer.capacity();
            if (index > position + capacity) {
                position += capacity;
                continue;
            } else {
                int indexMinusPosition = index - position;
                if (capacity > indexMinusPosition + Short.BYTES) {
                    buffer.putStringAsciii(indexMinusPosition, value, valueIndex, length);
                    return;
                } else {
                    for (int b = 0; b < length; b++) {
                        while (indexMinusPosition + b > capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            indexMinusPosition = index - position;
                        }
                        char c = value.charAt(valueIndex + b);
                        if (c > 127) {
                            c = '?';
                        }
                        buffer.putByte(indexMinusPosition + b, (byte) c);
                    }
                    return;
                }
            }
        }
        throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
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
            int position = 0;
            for (int buf = 0; buf < list.size(); buf++) {
                IByteBuffer buffer = list.get(buf);
                int capacity = buffer.capacity();
                if (index > position + capacity) {
                    position += capacity;
                    continue;
                } else {
                    int indexMinusPosition = index - position;
                    if (capacity > indexMinusPosition + length) {
                        buffer.getBytesTo(indexMinusPosition, dst, length);
                        return;
                    } else {
                        try {
                            final int limit = index + length;
                            int remaining = length;
                            for (int i = index; i < limit; i++) {
                                while (indexMinusPosition + i > capacity) {
                                    buf++;
                                    buffer = list.get(buf);
                                    capacity = buffer.capacity();
                                    indexMinusPosition = index - position;
                                }
                                final int toCopy = Integers.min(remaining, buffer.remaining(i));
                                buffer.getBytesTo(i, dst, toCopy);
                                remaining -= toCopy;
                                i += toCopy;
                            }
                        } catch (final IOException e) {
                            throw Throwables.propagate(e);
                        }
                        return;
                    }
                }
            }
            throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else {
            int position = 0;
            for (int buf = 0; buf < list.size(); buf++) {
                IByteBuffer buffer = list.get(buf);
                int capacity = buffer.capacity();
                if (index > position + capacity) {
                    position += capacity;
                    continue;
                } else {
                    int indexMinusPosition = index - position;
                    if (capacity > indexMinusPosition + length) {
                        buffer.putBytesTo(indexMinusPosition, src, length);
                        return;
                    } else {
                        try {
                            final int limit = index + length;
                            int remaining = length;
                            for (int i = index; i < limit; i++) {
                                while (indexMinusPosition + i > capacity) {
                                    buf++;
                                    buffer = list.get(buf);
                                    capacity = buffer.capacity();
                                    indexMinusPosition = index - position;
                                }
                                final int toCopy = Integers.min(remaining, buffer.remaining(i));
                                buffer.putBytesTo(i, src, toCopy);
                                remaining -= toCopy;
                                i += toCopy;
                            }
                        } catch (final IOException e) {
                            throw Throwables.propagate(e);
                        }
                        return;
                    }
                }
            }
            throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
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
            int position = 0;
            for (int buf = 0; buf < list.size(); buf++) {
                IByteBuffer buffer = list.get(buf);
                int capacity = buffer.capacity();
                if (index > position + capacity) {
                    position += capacity;
                    continue;
                } else {
                    int indexMinusPosition = index - position;
                    if (capacity > indexMinusPosition + length) {
                        buffer.putBytesTo(indexMinusPosition, src, length);
                        return;
                    } else {
                        try {
                            final int limit = index + length;
                            int remaining = length;
                            for (int i = index; i < limit; i++) {
                                while (indexMinusPosition + i > capacity) {
                                    buf++;
                                    buffer = list.get(buf);
                                    capacity = buffer.capacity();
                                    indexMinusPosition = index - position;
                                }
                                final int toCopy = Integers.min(remaining, buffer.remaining(i));
                                buffer.putBytesTo(i, src, toCopy);
                                remaining -= toCopy;
                                i += toCopy;
                            }
                        } catch (final IOException e) {
                            throw Throwables.propagate(e);
                        }
                        return;
                    }
                }
            }
            throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
        }
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else {
            int position = 0;
            for (int buf = 0; buf < list.size(); buf++) {
                IByteBuffer buffer = list.get(buf);
                int capacity = buffer.capacity();
                if (index > position + capacity) {
                    position += capacity;
                    continue;
                } else {
                    int indexMinusPosition = index - position;
                    if (capacity > indexMinusPosition + length) {
                        buffer.getBytesTo(indexMinusPosition, dst, length);
                        return;
                    } else {
                        try {
                            final int limit = index + length;
                            int remaining = length;
                            for (int i = index; i < limit; i++) {
                                while (indexMinusPosition + i > capacity) {
                                    buf++;
                                    buffer = list.get(buf);
                                    capacity = buffer.capacity();
                                    indexMinusPosition = index - position;
                                }
                                final int toCopy = Integers.min(remaining, buffer.remaining(i));
                                buffer.getBytesTo(i, dst, toCopy);
                                remaining -= toCopy;
                                i += toCopy;
                            }
                        } catch (final IOException e) {
                            throw Throwables.propagate(e);
                        }
                        return;
                    }
                }
            }
            throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
        }
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {}

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {}

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {}

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {}

    @Override
    public void clear(final byte value, final int index, final int length) {
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IByteBuffer buffer = list.get(buf);
            int capacity = buffer.capacity();
            if (index > position + capacity) {
                position += capacity;
                continue;
            } else {
                int indexMinusPosition = index - position;
                if (capacity > indexMinusPosition + length) {
                    buffer.clear(value, indexMinusPosition, length);
                    return;
                } else {
                    final int limit = index + length;
                    int remaining = length;
                    for (int i = index; i < limit; i++) {
                        while (indexMinusPosition + i > capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            indexMinusPosition = index - position;
                        }
                        final int toCopy = Integers.min(remaining, buffer.remaining(i));
                        buffer.clear(value, i, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                    }
                    return;
                }
            }
        }
        throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
    }

    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        if (list.size() == 1) {
            final IByteBuffer delegate = list.get(0);
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

}
