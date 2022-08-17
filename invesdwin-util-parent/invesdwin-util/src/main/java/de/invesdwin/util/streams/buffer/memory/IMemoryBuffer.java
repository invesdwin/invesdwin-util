package de.invesdwin.util.streams.buffer.memory;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.ArrayExpandableByteBuffer;

/**
 * Default ByteOrder is always BigEndian. Use Reverse-Suffixed methods to write/read in LittleEndian. Alternatively use
 * OrderedDelegateByteBuffer to switch the default byte order (though not recommeded).
 */
public interface IMemoryBuffer extends IMemoryBufferWriter {

    IMemoryBuffer ensureCapacity(long capacity);

    ByteOrder getOrder();

    boolean isReadOnly();

    long addressOffset();

    long capacity();

    default long remaining(final long index) {
        return capacity() - index;
    }

    long getLong(long index);

    long getLongReverse(long index);

    int getInt(long index);

    int getIntReverse(long index);

    double getDouble(long index);

    double getDoubleReverse(long index);

    float getFloat(long index);

    float getFloatReverse(long index);

    short getShort(long index);

    short getShortReverse(long index);

    char getChar(long index);

    char getCharReverse(long index);

    default boolean getBoolean(final long index) {
        return getByte(index) > 0;
    }

    byte getByte(long index);

    default void getBytes(final long index, final byte[] dst) {
        getBytesTo(index, dst, dst.length);
    }

    default void getBytesFrom(final long index, final byte[] dst, final int dstIndex) {
        getBytes(index, dst, dstIndex, dst.length - dstIndex);
    }

    default void getBytesTo(final long index, final byte[] dst, final int length) {
        getBytes(index, dst, 0, length);
    }

    void getBytes(long index, byte[] dst, int dstIndex, int length);

    default void getBytes(final long index, final MutableDirectBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }

    default void getBytesFrom(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstIndex, dstBuffer.capacity() - dstIndex);
    }

    default void getBytesTo(final long index, final MutableDirectBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }

    void getBytes(long index, MutableDirectBuffer dstBuffer, int dstIndex, int length);

    default void getBytes(final long index, final IByteBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }

    default void getBytesFrom(final long index, final IByteBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstIndex, dstBuffer.capacity() - dstIndex);
    }

    default void getBytesTo(final long index, final IByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }

    void getBytes(long index, IByteBuffer dstBuffer, int dstIndex, int length);

    default void getBytes(final long index, final IMemoryBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }

    default void getBytesFrom(final long index, final IMemoryBuffer dstBuffer, final long dstIndex) {
        getBytes(index, dstBuffer, dstIndex, dstBuffer.capacity() - dstIndex);
    }

    default void getBytesTo(final long index, final IMemoryBuffer dstBuffer, final long length) {
        getBytes(index, dstBuffer, 0, length);
    }

    void getBytes(long index, IMemoryBuffer dstBuffer, long dstIndex, long length);

    default void getBytes(final long index, final java.nio.ByteBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.remaining());
    }

    default void getBytesFrom(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstBuffer.position(), dstIndex);
    }

    default void getBytesTo(final long index, final java.nio.ByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, dstBuffer.position(), length);
    }

    void getBytes(long index, java.nio.ByteBuffer dstBuffer, int dstIndex, int length);

    long wrapAdjustment();

    boolean isExpandable();

    void putLong(long index, long value);

    void putLongReverse(long index, long value);

    void putInt(long index, int value);

    void putIntReverse(long index, int value);

    void putDouble(long index, double value);

    void putDoubleReverse(long index, double value);

    void putFloat(long index, float value);

    void putFloatReverse(long index, float value);

    void putShort(long index, short value);

    void putShortReverse(long index, short value);

    void putChar(long index, char value);

    void putCharReverse(long index, char value);

    default void putBoolean(final long index, final boolean value) {
        putByte(index, Bytes.checkedCast(value));
    }

    void putByte(long index, byte value);

    default void putBytes(final long index, final byte[] src) {
        putBytesTo(index, src, src.length);
    }

    default void putBytesFrom(final long index, final byte[] src, final int srcIndex) {
        putBytes(index, src, srcIndex, src.length - srcIndex);
    }

    default void putBytesTo(final long index, final byte[] src, final int length) {
        putBytes(index, src, 0, length);
    }

    void putBytes(long index, byte[] src, int srcIndex, int length);

    default void putBytes(final long index, final java.nio.ByteBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final long index, final java.nio.ByteBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(long index, java.nio.ByteBuffer srcBuffer, int srcIndex, int length);

    default void putBytes(final long index, final DirectBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final long index, final DirectBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final long index, final DirectBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(long index, DirectBuffer srcBuffer, int srcIndex, int length);

    default void putBytes(final long index, final IByteBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final long index, final IByteBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final long index, final IByteBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(long index, IByteBuffer srcBuffer, int srcIndex, int length);

    default void putBytes(final long index, final IMemoryBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final long index, final IMemoryBuffer srcBuffer, final long srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final long index, final IMemoryBuffer srcBuffer, final long length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(long index, IMemoryBuffer srcBuffer, long srcIndex, long length);

    default InputStream asInputStream() {
        return asInputStreamTo(capacity());
    }

    default InputStream asInputStreamFrom(final long index) {
        return asInputStream(index, remaining(index));
    }

    default InputStream asInputStreamTo(final long length) {
        return asInputStream(0, length);
    }

    InputStream asInputStream(long index, long length);

    default OutputStream asOutputStream() {
        return asOutputStreamTo(capacity());
    }

    default OutputStream asOutputStreamFrom(final long index) {
        return asOutputStream(index, remaining(index));
    }

    default OutputStream asOutputStreamTo(final long length) {
        return asOutputStream(0, length);
    }

    OutputStream asOutputStream(long index, long length);

    /**
     * Might return the actual underlying array. Thus make sure to clone() it if the buffer is to be reused. Or just use
     * asByteArrayCopy instead to make sure a copy is returned always and clone() is not used redundantly.
     */
    java.nio.ByteBuffer asNioByteBuffer(long index, int length);

    /**
     * Always returns a new copy as a byte array regardless of the underlying storage.
     */
    byte[] asByteArrayCopy(long index, int length);

    /**
     * Either returns the underlying array or copies the underlying storage into an array. Note that changes to the
     * array might or might not be reflected in the underlying storage.
     */
    MutableDirectBuffer asDirectBuffer(long index, int length);

    /**
     * Either returns the underlying array or copies the underlying storage into an array. Note that changes to the
     * array might or might not be reflected in the underlying storage.
     */
    IByteBuffer asByteBuffer(long index, int length);

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    IMemoryBuffer sliceFrom(long index);

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    default IMemoryBuffer sliceTo(final long length) {
        return slice(0, length);
    }

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    IMemoryBuffer slice(long index, long length);

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    IMemoryBuffer newSliceFrom(long index);

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    default IMemoryBuffer newSliceTo(final long length) {
        return newSlice(0, length);
    }

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    IMemoryBuffer newSlice(long index, long length);

    /**
     * This is more efficient than getStringUtf8(...) because it creates less garbage. Thout only works together with
     * putStringAscii(...).
     */
    String getStringAsciii(long index, int length);

    /**
     * Ascii strings can be directly appended to a StringBuilder for even more efficiency.
     */
    void getStringAsciii(long index, int length, Appendable dst);

    default void putStringAsciii(final long index, final CharSequence value) {
        putStringAsciiiTo(index, value, ByteBuffers.newStringAsciiLength(value));
    }

    default void putStringAsciiiFrom(final long index, final CharSequence value, final int valueIndex) {
        putStringAsciii(index, value, valueIndex, ByteBuffers.newStringAsciiLength(value) - valueIndex);
    }

    default void putStringAsciiiTo(final long index, final CharSequence value, final int length) {
        putStringAsciii(index, value, 0, length);
    }

    /**
     * This is more efficient than putStringUtf8(...) but replaces non ascii characters with '?'.
     */
    void putStringAsciii(long index, CharSequence value, int valueIndex, int length);

    int putStringUtf8(long index, String value);

    String getStringUtf8(long index, int length);

    void getStringUtf8(long index, int length, Appendable dst);

    default void getBytes(final long index, final DataOutputStream dst) throws IOException {
        getBytesTo(index, dst, capacity());
    }

    default void getBytesTo(final long index, final DataOutputStream dst, final long length) throws IOException {
        getBytesTo(index, (OutputStream) dst, length);
    }

    default void getBytes(final long index, final DataOutput dst) throws IOException {
        getBytesTo(index, dst, capacity());
    }

    void getBytesTo(long index, DataOutput dst, long length) throws IOException;

    default void getBytes(final long index, final OutputStream dst) throws IOException {
        getBytesTo(index, dst, remaining(index));
    }

    void getBytesTo(long index, OutputStream dst, long length) throws IOException;

    default void getBytes(final long index, final WritableByteChannel dst) throws IOException {
        getBytesTo(index, dst, remaining(index));
    }

    default void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
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

    default void putBytes(final long index, final DataInputStream src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    default void putBytesTo(final long index, final DataInputStream src, final long length) throws IOException {
        putBytesTo(index, (InputStream) src, length);
    }

    default void putBytes(final long index, final DataInput src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    void putBytesTo(long index, DataInput src, long length) throws IOException;

    default void putBytes(final long index, final InputStream src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    void putBytesTo(long index, InputStream src, long length) throws IOException;

    default void putBytes(final long index, final ReadableByteChannel src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    default void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
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

    <T> T unwrap(Class<T> type);

    @Override
    default long write(final IMemoryBuffer buffer) {
        final long length = capacity();
        getBytesTo(0, buffer, length);
        return length;
    }

    @Override
    default IMemoryBuffer asBuffer() {
        return this;
    }

    default void clear() {
        clear(Bytes.ZERO);
    }

    default void clear(final byte value) {
        clear(value, 0, capacity());
    }

    default void clearFrom(final byte value, final long index) {
        clear(value, index, remaining(index));
    }

    default void clearTo(final byte value, final long length) {
        clear(value, 0, length);
    }

    void clear(byte value, long index, long length);

}
