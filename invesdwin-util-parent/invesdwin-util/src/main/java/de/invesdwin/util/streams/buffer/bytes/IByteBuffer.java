package de.invesdwin.util.streams.buffer.bytes;

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

import de.invesdwin.norva.beanpath.spi.IUnwrap;
import de.invesdwin.util.collections.array.IPrimitiveArray;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

/**
 * Default ByteOrder is always BigEndian. Use Reverse-Suffixed methods to write/read in LittleEndian. Alternatively use
 * OrderedDelegateByteBuffer to switch the default byte order (though not recommeded).
 */
public interface IByteBuffer extends IByteBufferProvider, Cloneable, IPrimitiveArray, IUnwrap {

    IByteBuffer ensureCapacity(int capacity);

    ByteOrder getOrder();

    boolean isReadOnly();

    long addressOffset();

    MutableDirectBuffer directBuffer();

    byte[] byteArray();

    java.nio.ByteBuffer nioByteBuffer();

    int capacity();

    default int remaining(final int index) {
        return capacity() - index;
    }

    long getLong(int index);

    long getLongReverse(int index);

    int getInt(int index);

    int getIntReverse(int index);

    double getDouble(int index);

    double getDoubleReverse(int index);

    float getFloat(int index);

    float getFloatReverse(int index);

    short getShort(int index);

    short getShortReverse(int index);

    char getChar(int index);

    char getCharReverse(int index);

    default boolean getBoolean(final int index) {
        return getByte(index) > 0;
    }

    byte getByte(int index);

    default void getBytes(final int index, final byte[] dst) {
        getBytesTo(index, dst, dst.length);
    }

    default void getBytesFrom(final int index, final byte[] dst, final int dstIndex) {
        getBytes(index, dst, dstIndex, dst.length - dstIndex);
    }

    default void getBytesTo(final int index, final byte[] dst, final int length) {
        getBytes(index, dst, 0, length);
    }

    void getBytes(int index, byte[] dst, int dstIndex, int length);

    default void getBytes(final int index, final MutableDirectBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }

    default void getBytesFrom(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstIndex, dstBuffer.capacity() - dstIndex);
    }

    default void getBytesTo(final int index, final MutableDirectBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }

    void getBytes(int index, MutableDirectBuffer dstBuffer, int dstIndex, int length);

    default void getBytes(final int index, final IByteBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }

    default void getBytesFrom(final int index, final IByteBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstIndex, dstBuffer.capacity() - dstIndex);
    }

    default void getBytesTo(final int index, final IByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }

    void getBytes(int index, IByteBuffer dstBuffer, int dstIndex, int length);

    default void getBytesTo(final int index, final IMemoryBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }

    void getBytes(int index, IMemoryBuffer dstBuffer, long dstIndex, int length);

    default void getBytes(final int index, final java.nio.ByteBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.remaining());
    }

    default void getBytesFrom(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstBuffer.position(), dstIndex);
    }

    default void getBytesTo(final int index, final java.nio.ByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, dstBuffer.position(), length);
    }

    void getBytes(int index, java.nio.ByteBuffer dstBuffer, int dstIndex, int length);

    int wrapAdjustment();

    boolean isExpandable();

    void putLong(int index, long value);

    void putLongReverse(int index, long value);

    void putInt(int index, int value);

    void putIntReverse(int index, int value);

    void putDouble(int index, double value);

    void putDoubleReverse(int index, double value);

    void putFloat(int index, float value);

    void putFloatReverse(int index, float value);

    void putShort(int index, short value);

    void putShortReverse(int index, short value);

    void putChar(int index, char value);

    void putCharReverse(int index, char value);

    default void putBoolean(final int index, final boolean value) {
        putByte(index, Bytes.checkedCast(value));
    }

    void putByte(int index, byte value);

    default void putBytes(final int index, final byte[] src) {
        putBytesTo(index, src, src.length);
    }

    default void putBytesFrom(final int index, final byte[] src, final int srcIndex) {
        putBytes(index, src, srcIndex, src.length - srcIndex);
    }

    default void putBytesTo(final int index, final byte[] src, final int length) {
        putBytes(index, src, 0, length);
    }

    void putBytes(int index, byte[] src, int srcIndex, int length);

    default void putBytes(final int index, final java.nio.ByteBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final int index, final java.nio.ByteBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(int index, java.nio.ByteBuffer srcBuffer, int srcIndex, int length);

    default void putBytes(final int index, final DirectBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final int index, final DirectBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final int index, final DirectBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(int index, DirectBuffer srcBuffer, int srcIndex, int length);

    default void putBytes(final int index, final IByteBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final int index, final IByteBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final int index, final IByteBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(int index, IByteBuffer srcBuffer, int srcIndex, int length);

    default void putBytesTo(final int index, final IMemoryBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(int index, IMemoryBuffer srcBuffer, long srcIndex, int length);

    default InputStream asInputStream() {
        return asInputStreamTo(capacity());
    }

    default InputStream asInputStreamFrom(final int index) {
        return asInputStream(index, remaining(index));
    }

    default InputStream asInputStreamTo(final int length) {
        return asInputStream(0, length);
    }

    InputStream asInputStream(int index, int length);

    default OutputStream asOutputStream() {
        return asOutputStreamTo(capacity());
    }

    default OutputStream asOutputStreamFrom(final int index) {
        return asOutputStream(index, remaining(index));
    }

    default OutputStream asOutputStreamTo(final int length) {
        return asOutputStream(0, length);
    }

    OutputStream asOutputStream(int index, int length);

    /**
     * Might return the actual underlying array. Thus make sure to clone() it if the buffer is to be reused. Or just use
     * asByteArrayCopy instead to make sure a copy is returned always and clone() is not used redundantly.
     * 
     * WARNING: be aware that expandable buffers might have a larger capacity than was was added to the buffer, thus
     * always prefer to use asByteArrayTo(length) instead of this capacity bounded version. Or make sure to only call
     * this method on buffers that have been slice(from, to)'d since that sets the capacity as a contraint to the
     * underlying actual backing array capacity.
     */
    default byte[] asByteArray() {
        return asByteArrayTo(capacity());
    }

    default byte[] asByteArrayFrom(final int index) {
        return asByteArray(index, remaining(index));
    }

    default byte[] asByteArrayTo(final int length) {
        return asByteArray(0, length);
    }

    /**
     * Might return the actual underlying array. Thus make sure to clone() it if the buffer is to be reused. Or just use
     * asByteArrayCopy instead to make sure a copy is returned always and clone() is not used redundantly.
     */
    byte[] asByteArray(int index, int length);

    default java.nio.ByteBuffer asNioByteBuffer() {
        return asNioByteBufferTo(capacity());
    }

    default java.nio.ByteBuffer asNioByteBufferFrom(final int index) {
        return asNioByteBuffer(index, remaining(index));
    }

    default java.nio.ByteBuffer asNioByteBufferTo(final int length) {
        return asNioByteBuffer(0, length);
    }

    /**
     * Might return the actual underlying array. Thus make sure to clone() it if the buffer is to be reused. Or just use
     * asByteArrayCopy instead to make sure a copy is returned always and clone() is not used redundantly.
     * 
     * Though it is not guaranteed that the returned ByteBuffer will not be a separate copy of this storage (e.g. when
     * ListByteBuffer is used).
     */
    java.nio.ByteBuffer asNioByteBuffer(int index, int length);

    /**
     * Always returns a new copy as a byte array regardless of the underlying storage.
     * 
     * WARNING: be aware that expandable buffers might have a larger capacity than was was added to the buffer, thus
     * always prefer to use asByteArrayTo(length) instead of this capacity bounded version. Or make sure to only call
     * this method on buffers that have been slice(from, to)'d since that sets the capacity as a contraint to the
     * underlying actual backing array capacity.
     */
    default byte[] asByteArrayCopy() {
        return asByteArrayCopyTo(capacity());
    }

    default byte[] asByteArrayCopyFrom(final int index) {
        return asByteArrayCopy(index, remaining(index));
    }

    default byte[] asByteArrayCopyTo(final int length) {
        return asByteArrayCopy(0, length);
    }

    /**
     * Always returns a new copy as a byte array regardless of the underlying storage.
     */
    byte[] asByteArrayCopy(int index, int length);

    MutableDirectBuffer asDirectBuffer();

    default MutableDirectBuffer asDirectBufferFrom(final int index) {
        return asDirectBuffer(index, remaining(index));
    }

    default MutableDirectBuffer asDirectBufferTo(final int length) {
        return asDirectBuffer(0, length);
    }

    /**
     * Either returns the underlying array or copies the underlying storage into an array. Note that changes to the
     * array might or might not be reflected in the underlying storage.
     */
    MutableDirectBuffer asDirectBuffer(int index, int length);

    IMemoryBuffer asMemoryBuffer();

    default IMemoryBuffer asMemoryBufferFrom(final int index) {
        return asMemoryBuffer(index, remaining(index));
    }

    default IMemoryBuffer asMemoryBufferTo(final int length) {
        return asMemoryBuffer(0, length);
    }

    /**
     * Either returns the underlying array or copies the underlying storage into an array. Note that changes to the
     * array might or might not be reflected in the underlying storage.
     */
    IMemoryBuffer asMemoryBuffer(int index, int length);

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    IByteBuffer sliceFrom(int index);

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    default IByteBuffer sliceTo(final int length) {
        return slice(0, length);
    }

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    IByteBuffer slice(int index, int length);

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    IByteBuffer newSliceFrom(int index);

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    default IByteBuffer newSliceTo(final int length) {
        return newSlice(0, length);
    }

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    IByteBuffer newSlice(int index, int length);

    /**
     * This is more efficient than getStringUtf8(...) because it creates less garbage. Thout only works together with
     * putStringAscii(...).
     * 
     * WARNING: we have to keep the tripe-i naming to prevent a name-clash with Agrona.
     */
    String getStringAsciii(int index, int length);

    /**
     * Ascii strings can be directly appended to a StringBuilder for even more efficiency.
     */
    void getStringAsciii(int index, int length, Appendable dst);

    default void putStringAsciii(final int index, final CharSequence value) {
        putStringAsciiiTo(index, value, ByteBuffers.newStringAsciiLength(value));
    }

    default void putStringAsciiiFrom(final int index, final CharSequence value, final int valueIndex) {
        putStringAsciii(index, value, valueIndex, ByteBuffers.newStringAsciiLength(value) - valueIndex);
    }

    default void putStringAsciiiTo(final int index, final CharSequence value, final int length) {
        putStringAsciii(index, value, 0, length);
    }

    /**
     * This is more efficient than putStringUtf8(...) but replaces non ascii characters with '?'.
     */
    void putStringAsciii(int index, CharSequence value, int valueIndex, int length);

    int putStringUtf8(int index, String value);

    String getStringUtf8(int index, int length);

    void getStringUtf8(int index, int length, Appendable dst);

    default void getBytes(final int index, final DataOutputStream dst) throws IOException {
        getBytesTo(index, dst, capacity());
    }

    default void getBytesTo(final int index, final DataOutputStream dst, final int length) throws IOException {
        getBytesTo(index, (OutputStream) dst, length);
    }

    default void getBytes(final int index, final DataOutput dst) throws IOException {
        getBytesTo(index, dst, capacity());
    }

    void getBytesTo(int index, DataOutput dst, int length) throws IOException;

    default void getBytes(final int index, final OutputStream dst) throws IOException {
        getBytesTo(index, dst, remaining(index));
    }

    void getBytesTo(int index, OutputStream dst, int length) throws IOException;

    default void getBytes(final int index, final WritableByteChannel dst) throws IOException {
        getBytesTo(index, dst, remaining(index));
    }

    void getBytesTo(int index, WritableByteChannel dst, int length) throws IOException;

    default void putBytes(final int index, final DataInputStream src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    default void putBytesTo(final int index, final DataInputStream src, final int length) throws IOException {
        putBytesTo(index, (InputStream) src, length);
    }

    default void putBytes(final int index, final DataInput src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    void putBytesTo(int index, DataInput src, int length) throws IOException;

    default void putBytes(final int index, final InputStream src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    void putBytesTo(int index, InputStream src, int length) throws IOException;

    default void putBytes(final int index, final ReadableByteChannel src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    void putBytesTo(int index, ReadableByteChannel src, int length) throws IOException;

    @Override
    default int getBuffer(final IByteBuffer dst) {
        final int length = capacity();
        getBytesTo(0, dst, length);
        return length;
    }

    @Override
    default IByteBuffer asBuffer() {
        return this;
    }

    IByteBuffer clone();

    default IByteBuffer cloneFrom(final int index) {
        return clone(index, remaining(index));
    }

    default IByteBuffer cloneTo(final int length) {
        return clone(0, length);
    }

    IByteBuffer clone(int index, int length);

    default void clear() {
        clear(Bytes.ZERO);
    }

    default void clearFrom(final int index) {
        clear(Bytes.ZERO, index, remaining(index));
    }

    default void clearTo(final int length) {
        clear(Bytes.ZERO, 0, length);
    }

    default void clear(final int index, final int length) {
        clear(Bytes.ZERO, index, length);
    }

    default void clear(final byte value) {
        clear(value, 0, capacity());
    }

    default void clearFrom(final byte value, final int index) {
        clear(value, index, remaining(index));
    }

    default void clearTo(final byte value, final int length) {
        clear(value, 0, length);
    }

    void clear(byte value, int index, int length);

    default void clear(final IRandomGenerator random) {
        clear(random, 0, capacity());
    }

    default void clearFrom(final IRandomGenerator random, final int index) {
        clear(random, index, remaining(index));
    }

    default void clearTo(final IRandomGenerator random, final int length) {
        clear(random, 0, length);
    }

    /**
     * Adapted from it.unimi.dsi.util.XoShiRo256PlusRandomGenerator.nextBytes(byte[])
     */
    default void clear(final IRandomGenerator random, final int index, final int length) {
        int i = length, n = 0;
        while (i != 0) {
            n = Math.min(i, Long.BYTES);
            for (long bits = random.nextLong(); n-- != 0; bits >>= Long.BYTES) {
                putByte(--i + index, (byte) bits);
            }
        }
    }

    @Override
    default int size() {
        return capacity();
    }

    @Override
    default boolean isEmpty() {
        return capacity() == 0;
    }

    IByteBuffer asImmutableSlice();

}
