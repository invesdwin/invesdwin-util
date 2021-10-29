package de.invesdwin.util.streams.buffer.memory;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * {@link OutputStream} that wraps an underlying {@link IMemoryBuffer}.
 */
@NotThreadSafe
public class MemoryBufferOutputStream extends OutputStream {
    private IMemoryBuffer buffer;
    private long offset;
    private long length;
    private long position;

    /**
     * Default constructor.
     */
    public MemoryBufferOutputStream() {
    }

    /**
     * Constructs output stream wrapping the given buffer.
     *
     * @param buffer
     *            to wrap.
     */
    public MemoryBufferOutputStream(final IMemoryBuffer buffer) {
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
    public MemoryBufferOutputStream(final IMemoryBuffer buffer, final long offset, final long length) {
        wrap(buffer, offset, length);
    }

    /**
     * Wrap the buffer.
     *
     * @param buffer
     *            to wrap.
     */
    public void wrap(final IMemoryBuffer buffer) {
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
    public void wrap(final IMemoryBuffer buffer, final long offset, final long length) {
        if (null == buffer) {
            throw new NullPointerException("buffer cannot be null");
        }

        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
        this.position = 0;
    }

    /**
     * The position in the buffer from the offset up to which has been written.
     *
     * @return the position in the buffer from the offset up to which has been written.
     */
    public long position() {
        return position;
    }

    /**
     * The offset within the underlying buffer at which to start.
     *
     * @return offset within the underlying buffer at which to start.
     */
    public long offset() {
        return offset;
    }

    /**
     * The underlying buffer being wrapped.
     *
     * @return the underlying buffer being wrapped.
     */
    public IMemoryBuffer buffer() {
        return buffer;
    }

    /**
     * The length of the underlying buffer to use
     *
     * @return length of the underlying buffer to use
     */
    public long length() {
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
        final long resultingOffset = position + (length);
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
    public void flush() {
    }

    /**
     * Override to remove {@link IOException}. This method does nothing.
     */
    @Override
    public void close() {
    }
}
