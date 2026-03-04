package de.invesdwin.util.streams.buffer.bytes.stream;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * {@link OutputStream} that wraps an underlying {@link IByteBuffer}.
 */
@NotThreadSafe
public class ByteBufferOutputStream extends OutputStream {
    private IByteBuffer buffer;
    private int offset;
    private int length;
    private int position;

    /**
     * Default constructor.
     */
    public ByteBufferOutputStream() {}

    /**
     * Constructs output stream wrapping the given buffer.
     *
     * @param buffer
     *            to wrap.
     */
    public ByteBufferOutputStream(final IByteBuffer buffer) {
        wrap(buffer, 0, buffer.capacity());
    }

    /**
     * Constructs output stream wrapping the given buffer at an offset.
     *
     * @param buffer
     *            to wrap.
     * @param offset
     *            in the buffer.
     * @param length
     *            size in bytes to wrap.
     */
    public ByteBufferOutputStream(final IByteBuffer buffer, final int offset, final int length) {
        wrap(buffer, offset, length);
    }

    /**
     * Wrap the buffer.
     *
     * @param buffer
     *            to wrap.
     */
    public void wrap(final IByteBuffer buffer) {
        wrap(buffer, 0, buffer.capacity());
    }

    /**
     * Wrap the buffer at an offset.
     *
     * @param buffer
     *            to wrap.
     * @param offset
     *            in the buffer.
     * @param length
     *            size in bytes to wrap.
     */
    public void wrap(final IByteBuffer buffer, final int offset, final int length) {
        if (null == buffer) {
            throw new NullPointerException("buffer cannot be null");
        }

        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
        this.position = 0;

        if (UnsafeBuffer.SHOULD_BOUNDS_CHECK) {
            boundsCheck0(offset, length);
        }
    }

    private void boundsCheck0(final long index, final long length) {
        final long resultingPosition = index + length;
        if (index < 0 || length < 0 || resultingPosition > buffer.capacity()) {
            throw FastIndexOutOfBoundsException.getInstance("index=%s length=%s capacity=%s", index, length,
                    buffer.capacity());
        }
    }

    /**
     * The position in the buffer from the offset up to which has been written.
     *
     * @return the position in the buffer from the offset up to which has been written.
     */
    public int position() {
        return position;
    }

    /**
     * The offset within the underlying buffer at which to start.
     *
     * @return offset within the underlying buffer at which to start.
     */
    public int offset() {
        return offset;
    }

    /**
     * The underlying buffer being wrapped.
     *
     * @return the underlying buffer being wrapped.
     */
    public IByteBuffer buffer() {
        return buffer;
    }

    /**
     * The length of the underlying buffer to use
     *
     * @return length of the underlying buffer to use
     */
    public int length() {
        return length;
    }

    /**
     * Write a byte to buffer.
     *
     * @param b
     *            to be written.
     * @throws IllegalStateException
     *             if insufficient capacity remains in the buffer.
     */
    @Override
    public void write(final int b) {
        if (position == length) {
            throw new IllegalStateException("position has reached the end of underlying buffer");
        }

        buffer.putByte(offset + position, (byte) b);
        ++position;
    }

    /**
     * Write a byte[] to the buffer.
     *
     * @param srcBytes
     *            to write
     * @param srcOffset
     *            at which to begin reading bytes from the srcBytes.
     * @param length
     *            of the srcBytes to read.
     * @throws IllegalStateException
     *             if insufficient capacity remains in the buffer.
     */
    @Override
    public void write(final byte[] srcBytes, final int srcOffset, final int length) {
        final long resultingOffset = position + ((long) length);
        if (resultingOffset > this.length) {
            throw new IllegalStateException("insufficient capacity in the buffer");
        }

        buffer.putBytes(offset + position, srcBytes, srcOffset, length);
        position += length;
    }

    /**
     * Write a byte[] to the buffer.
     *
     * @param srcBytes
     *            to write
     * @throws IllegalStateException
     *             if insufficient capacity remains in the buffer.
     */
    @Override
    public void write(final byte[] srcBytes) {
        write(srcBytes, 0, srcBytes.length);
    }

    /**
     * Override to remove {@link IOException}. This method does nothing.
     */
    @Override
    public void flush() {}

    /**
     * Override to remove {@link IOException}. This method does nothing.
     */
    @Override
    public void close() {}
}
