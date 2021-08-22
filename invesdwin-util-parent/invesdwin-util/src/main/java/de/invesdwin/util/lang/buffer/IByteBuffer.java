package de.invesdwin.util.lang.buffer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public interface IByteBuffer {

    /**
     * Reads the underlying offset to the memory address.
     *
     * @return the underlying offset to the memory address.
     */
    long addressOffset();

    MutableDirectBuffer directBuffer();

    /**
     * Get the underlying byte[] if one exists.
     * <p>
     * NB: there may not be a one-to-one mapping between indices on this buffer and the underlying byte[], see
     * {@link DirectBuffer#wrapAdjustment()}.
     *
     * @return the underlying byte[] if one exists.
     */
    byte[] byteArray();

    /**
     * Get the underlying {@link ByteBuffer} if one exists.
     * <p>
     * NB: there may not be a one-to-one mapping between indices on this buffer and the underlying byte[], see
     * {@link DirectBuffer#wrapAdjustment()}.
     *
     * @return the underlying {@link ByteBuffer} if one exists.
     */
    ByteBuffer byteBuffer();

    /**
     * Get the capacity of the underlying buffer.
     *
     * @return the capacity of the underlying buffer in bytes.
     */
    int capacity();

    /**
     * Get the value at a given index.
     *
     * @param index
     *            in bytes from which to get.
     * @return the value for at a given index.
     */
    long getLong(int index);

    /**
     * Get the value at a given index.
     *
     * @param index
     *            in bytes from which to get.
     * @return the value for a given index.
     */
    int getInt(int index);

    /**
     * Get the value at a given index.
     *
     * @param index
     *            in bytes from which to get.
     * @return the value at a given index.
     */
    double getDouble(int index);

    /**
     * Get the value at a given index.
     *
     * @param index
     *            in bytes from which to get.
     * @return the value at a given index.
     */
    float getFloat(int index);

    /**
     * Get the value at a given index.
     *
     * @param index
     *            in bytes from which to get.
     * @return the value at a given index.
     */
    short getShort(int index);

    /**
     * Get the value at a given index.
     *
     * @param index
     *            in bytes from which to get.
     * @return the value at a given index.
     */
    char getChar(int index);

    /**
     * Get the value at a given index.
     *
     * @param index
     *            in bytes from which to get.
     * @return the value at a given index.
     */
    byte getByte(int index);

    /**
     * Get from the underlying buffer into a supplied byte array. This method will try to fill the supplied byte array.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param dst
     *            into which the dst will be copied.
     */
    default void getBytes(final int index, final byte[] dst) {
        getBytes(index, dst, dst.length);
    }

    /**
     * Get bytes from the underlying buffer into a supplied byte array.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param dst
     *            into which the bytes will be copied.
     * @param length
     *            of the supplied buffer to use.
     */
    default void getBytes(final int index, final byte[] dst, final int length) {
        getBytes(index, dst, 0, length);
    }

    /**
     * Get bytes from the underlying buffer into a supplied byte array.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param dst
     *            into which the bytes will be copied.
     * @param offset
     *            in the supplied buffer to start the copy.
     * @param length
     *            of the supplied buffer to use.
     */
    void getBytes(int index, byte[] dst, int offset, int length);

    /**
     * Get bytes from this {@link DirectBuffer} into the provided {@link MutableDirectBuffer} at given indices.
     *
     * @param index
     *            in this buffer to begin getting the bytes.
     * @param dstBuffer
     *            to which the bytes will be copied.
     */
    default void getBytes(final int index, final MutableDirectBuffer dstBuffer) {
        getBytes(index, dstBuffer, dstBuffer.capacity());
    }

    /**
     * Get bytes from this {@link DirectBuffer} into the provided {@link MutableDirectBuffer} at given indices.
     *
     * @param index
     *            in this buffer to begin getting the bytes.
     * @param dstBuffer
     *            to which the bytes will be copied.
     * @param length
     *            of the bytes to be copied.
     */
    default void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }

    /**
     * Get bytes from this {@link DirectBuffer} into the provided {@link MutableDirectBuffer} at given indices.
     *
     * @param index
     *            in this buffer to begin getting the bytes.
     * @param dstBuffer
     *            to which the bytes will be copied.
     * @param dstIndex
     *            in the channel buffer to which the byte copy will begin.
     * @param length
     *            of the bytes to be copied.
     */
    void getBytes(int index, MutableDirectBuffer dstBuffer, int dstIndex, int length);

    /**
     * Get bytes from this {@link DirectBuffer} into the provided {@link MutableDirectBuffer} at given indices.
     *
     * @param index
     *            in this buffer to begin getting the bytes.
     * @param dstBuffer
     *            to which the bytes will be copied.
     */
    default void getBytes(final int index, final IByteBuffer dstBuffer) {
        getBytes(index, dstBuffer, dstBuffer.capacity());
    }

    /**
     * Get bytes from this {@link DirectBuffer} into the provided {@link MutableDirectBuffer} at given indices.
     *
     * @param index
     *            in this buffer to begin getting the bytes.
     * @param dstBuffer
     *            to which the bytes will be copied.
     * @param length
     *            of the bytes to be copied.
     */
    default void getBytes(final int index, final IByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }

    /**
     * Get bytes from this {@link DirectBuffer} into the provided {@link MutableDirectBuffer} at given indices.
     *
     * @param index
     *            in this buffer to begin getting the bytes.
     * @param dstBuffer
     *            to which the bytes will be copied.
     * @param dstIndex
     *            in the channel buffer to which the byte copy will begin.
     * @param length
     *            of the bytes to be copied.
     */
    void getBytes(int index, IByteBuffer dstBuffer, int dstIndex, int length);

    /**
     * Get from the underlying buffer into a supplied {@link ByteBuffer} current {@link ByteBuffer#position()}.
     * <p>
     * The destination buffer will have its {@link ByteBuffer#position()} advanced as a result.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param dstBuffer
     *            into which the bytes will be copied.
     */
    default void getBytes(final int index, final ByteBuffer dstBuffer) {
        getBytes(index, dstBuffer, dstBuffer.remaining());
    }

    /**
     * Get from the underlying buffer into a supplied {@link ByteBuffer} current {@link ByteBuffer#position()}.
     * <p>
     * The destination buffer will have its {@link ByteBuffer#position()} advanced as a result.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param dstBuffer
     *            into which the bytes will be copied.
     * @param length
     *            of the supplied buffer to use.
     */
    default void getBytes(final int index, final ByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, dstBuffer.position(), length);
    }

    /**
     * Get from the underlying buffer into a supplied {@link ByteBuffer} at an offset.
     * <p>
     * The destination buffer will not have its {@link ByteBuffer#position()} advanced as a result.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param dstBuffer
     *            into which the bytes will be copied.
     * @param dstOffset
     *            in the target buffer.
     * @param length
     *            of the supplied buffer to use.
     */
    void getBytes(int index, ByteBuffer dstBuffer, int dstOffset, int length);

    /**
     * Get the adjustment in indices between an index in this buffer and the wrapped object. The wrapped object might be
     * a {@link ByteBuffer} or a byte[].
     * <p>
     * You only need to use this adjustment if you plan to perform operations on the underlying byte array or byte
     * buffer that rely on their indices.
     *
     * @return the adjustment in indices between an index in this buffer and the wrapped object.
     * @see DirectBuffer#byteArray()
     * @see DirectBuffer#byteBuffer()
     */
    int wrapAdjustment();

    /**
     * Is this buffer expandable to accommodate putting data into it beyond the current capacity?
     *
     * @return true is the underlying storage can expand otherwise false.
     */
    boolean isExpandable();

    /**
     * Put a value to a given index.
     *
     * @param index
     *            in bytes for where to put.
     * @param value
     *            for at a given index.
     */
    int putLong(int index, long value);

    /**
     * Put a value to a given index.
     *
     * @param index
     *            in bytes for where to put.
     * @param value
     *            for at a given index.
     */
    int putInt(int index, int value);

    /**
     * Put a value to a given index.
     *
     * @param index
     *            in bytes for where to put.
     * @param value
     *            to be written.
     */
    int putDouble(int index, double value);

    /**
     * Put a value to a given index.
     *
     * @param index
     *            in bytes for where to put.
     * @param value
     *            to be written.
     */
    int putFloat(int index, float value);

    /**
     * Put a value to a given index.
     *
     * @param index
     *            in bytes for where to put.
     * @param value
     *            to be written.
     */
    int putShort(int index, short value);

    /**
     * Put a value to a given index.
     *
     * @param index
     *            in bytes for where to put.
     * @param value
     *            to be written.
     */
    int putChar(int index, char value);

    /**
     * Put a value to a given index.
     *
     * @param index
     *            in bytes for where to put.
     * @param value
     *            to be written.
     */
    int putByte(int index, byte value);

    /**
     * Put an array of src into the underlying buffer.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param src
     *            to be copied into the underlying buffer.
     */
    default int putBytes(final int index, final byte[] src) {
        return putBytes(index, src, src.length);
    }

    /**
     * Put an array into the underlying buffer.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param src
     *            to be copied to the underlying buffer.
     * @param length
     *            of the supplied buffer to copy.
     */
    default int putBytes(final int index, final byte[] src, final int length) {
        return putBytes(index, src, 0, length);
    }

    /**
     * Put an array into the underlying buffer.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param src
     *            to be copied to the underlying buffer.
     * @param offset
     *            in the supplied buffer to begin the copy.
     * @param length
     *            of the supplied buffer to copy.
     */
    int putBytes(int index, byte[] src, int offset, int length);

    /**
     * Put bytes into the underlying buffer for the view. Bytes will be copied from current
     * {@link ByteBuffer#position()} for a given length.
     * <p>
     * The source buffer will have its {@link ByteBuffer#position()} advanced as a result.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param srcBuffer
     *            to copy the bytes from.
     * @param length
     *            of the supplied buffer to copy.
     */
    default int putBytes(final int index, final ByteBuffer srcBuffer) {
        return putBytes(index, srcBuffer, srcBuffer.remaining());
    }

    /**
     * Put bytes into the underlying buffer for the view. Bytes will be copied from current
     * {@link ByteBuffer#position()} for a given length.
     * <p>
     * The source buffer will have its {@link ByteBuffer#position()} advanced as a result.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param srcBuffer
     *            to copy the bytes from.
     * @param length
     *            of the supplied buffer to copy.
     */
    default int putBytes(final int index, final ByteBuffer srcBuffer, final int length) {
        return putBytes(index, srcBuffer, srcBuffer.position(), length);
    }

    /**
     * Put bytes into the underlying buffer for the view. Bytes will be copied from the buffer index to the buffer index
     * + length.
     * <p>
     * The source buffer will not have its {@link ByteBuffer#position()} advanced as a result.
     *
     * @param index
     *            in the underlying buffer to start from.
     * @param srcBuffer
     *            to copy the bytes from (does not change position).
     * @param srcIndex
     *            in the source buffer from which the copy will begin.
     * @param length
     *            of the bytes to be copied.
     */
    int putBytes(int index, ByteBuffer srcBuffer, int srcIndex, int length);

    /**
     * Put bytes from a source {@link DirectBuffer} into this {@link MutableDirectBuffer} at given indices.
     *
     * @param index
     *            in this buffer to begin putting the bytes.
     * @param srcBuffer
     *            from which the bytes will be copied.
     */
    default int putBytes(final int index, final DirectBuffer srcBuffer) {
        return putBytes(index, srcBuffer, 0, srcBuffer.capacity());
    }

    /**
     * Put bytes from a source {@link DirectBuffer} into this {@link MutableDirectBuffer} at given indices.
     *
     * @param index
     *            in this buffer to begin putting the bytes.
     * @param srcBuffer
     *            from which the bytes will be copied.
     * @param length
     *            of the bytes to be copied.
     */
    default int putBytes(final int index, final DirectBuffer srcBuffer, final int length) {
        return putBytes(index, srcBuffer, 0, length);
    }

    /**
     * Put bytes from a source {@link DirectBuffer} into this {@link MutableDirectBuffer} at given indices.
     *
     * @param index
     *            in this buffer to begin putting the bytes.
     * @param srcBuffer
     *            from which the bytes will be copied.
     * @param srcIndex
     *            in the source buffer from which the byte copy will begin.
     * @param length
     *            of the bytes to be copied.
     */
    int putBytes(int index, DirectBuffer srcBuffer, int srcIndex, int length);

    default int putBytes(final int index, final IByteBuffer srcBuffer) {
        return putBytes(index, srcBuffer, srcBuffer.capacity());
    }

    default int putBytes(final int index, final IByteBuffer srcBuffer, final int length) {
        return putBytes(index, srcBuffer, 0, length);
    }

    int putBytes(int index, IByteBuffer srcBuffer, int srcIndex, int length);

    default InputStream asInputStream() {
        return asInputStream(capacity());
    }

    default InputStream asInputStream(final int length) {
        return asInputStream(0, length);
    }

    InputStream asInputStream(int offset, int length);

    default OutputStream asOutputStream() {
        return asOutputStream(capacity());
    }

    default OutputStream asOutputStream(final int length) {
        return asOutputStream(0, length);
    }

    OutputStream asOutputStream(int offset, int length);

    default byte[] asByteArray() {
        return asByteArray(capacity());
    }

    default byte[] asByteArray(final int length) {
        return asByteArray(0, length);
    }

    /**
     * Either returns the underlying array or copies the underlying storage into an array. Note that changes to the
     * array might or might not be reflected in the underlying storage.
     */
    byte[] asByteArray(int offset, int length);

    default IByteBuffer slice() {
        return slice(0, capacity());
    }

    default IByteBuffer slice(final int length) {
        return slice(0, length);
    }

    IByteBuffer slice(int offset, int length);

}
