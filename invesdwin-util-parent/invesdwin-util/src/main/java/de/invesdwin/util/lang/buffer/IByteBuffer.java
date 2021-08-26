package de.invesdwin.util.lang.buffer;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Bytes;

public interface IByteBuffer {

    ByteOrder getOrder();

    long addressOffset();

    MutableDirectBuffer directBuffer();

    byte[] byteArray();

    ByteBuffer byteBuffer();

    int capacity();

    default int remaining(final int index) {
        return capacity() - index;
    }

    long getLong(int index);

    int getInt(int index);

    double getDouble(int index);

    float getFloat(int index);

    short getShort(int index);

    char getChar(int index);

    default boolean getBoolean(final int index) {
        return Booleans.checkedCast(getByte(index));
    }

    byte getByte(int index);

    default void getBytes(final int index, final byte[] dst) {
        getBytesTo(index, dst, dst.length);
    }

    default void getBytesFrom(final int index, final byte[] dst, final int dstIndex) {
        getBytes(index, dst, dstIndex, remaining(dstIndex));
    }

    default void getBytesTo(final int index, final byte[] dst, final int length) {
        getBytes(index, dst, 0, length);
    }

    void getBytes(int index, byte[] dst, int dstIndex, int length);

    default void getBytes(final int index, final MutableDirectBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }

    default void getBytesFrom(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstIndex, remaining(dstIndex));
    }

    default void getBytesTo(final int index, final MutableDirectBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }

    void getBytes(int index, MutableDirectBuffer dstBuffer, int dstIndex, int length);

    default void getBytes(final int index, final IByteBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }

    default void getBytesFrom(final int index, final IByteBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstIndex, remaining(dstIndex));
    }

    default void getBytesTo(final int index, final IByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }

    void getBytes(int index, IByteBuffer dstBuffer, int dstIndex, int length);

    default void getBytes(final int index, final ByteBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.remaining());
    }

    default void getBytesFrom(final int index, final ByteBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstBuffer.position(), dstIndex);
    }

    default void getBytesTo(final int index, final ByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, dstBuffer.position(), length);
    }

    void getBytes(int index, ByteBuffer dstBuffer, int dstIndex, int length);

    int wrapAdjustment();

    boolean isExpandable();

    void putLong(int index, long value);

    void putInt(int index, int value);

    void putDouble(int index, double value);

    void putFloat(int index, float value);

    void putShort(int index, short value);

    void putChar(int index, char value);

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

    default void putBytes(final int index, final ByteBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final int index, final ByteBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final int index, final ByteBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(int index, ByteBuffer srcBuffer, int srcIndex, int length);

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

    default MutableDirectBuffer asDirectBuffer() {
        return asDirectBufferTo(capacity());
    }

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

    default IByteBuffer sliceFrom(final int index) {
        return slice(index, remaining(index));
    }

    default IByteBuffer sliceTo(final int length) {
        return slice(0, length);
    }

    IByteBuffer slice(int index, int length);

    /**
     * This is more efficient than getStringUtf8(...) because it creates less garbage. Thout only works together with
     * putStringAscii(...).
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
        getBytesTo(index, dst, capacity());
    }

    void getBytesTo(int index, OutputStream dst, int length) throws IOException;

    default void putBytes(final int index, final DataInputStream src) throws IOException {
        putBytesTo(index, src, capacity());
    }

    default void putBytesTo(final int index, final DataInputStream src, final int length) throws IOException {
        putBytesTo(index, (InputStream) src, length);
    }

    default void putBytes(final int index, final DataInput src) throws IOException {
        putBytesTo(index, src, capacity());
    }

    void putBytesTo(int index, DataInput src, int length) throws IOException;

    default void putBytes(final int index, final InputStream src) throws IOException {
        putBytesTo(index, src, capacity());
    }

    void putBytesTo(int index, InputStream src, int length) throws IOException;

}
