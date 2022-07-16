package de.invesdwin.util.streams.buffer.memory.stream;

import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

/**
 * {@link OutputStream} that wraps an underlying expandable version of a {@link IMemoryBuffer}.
 */
@NotThreadSafe
public class ExpandableMemoryBufferOutputStream extends OutputStream {
    private IMemoryBuffer buffer;
    private long offset;
    private long position;

    /**
     * Default constructor.
     */
    public ExpandableMemoryBufferOutputStream() {
    }

    /**
     * Wrap given {@link IMemoryBuffer}.
     *
     * @param buffer
     *            to wrap.
     */
    public ExpandableMemoryBufferOutputStream(final IMemoryBuffer buffer) {
        wrap(buffer, 0);
    }

    /**
     * Wrap given {@link IMemoryBuffer} at a given offset.
     *
     * @param buffer
     *            to wrap.
     * @param offset
     *            at which the puts will occur.
     */
    public ExpandableMemoryBufferOutputStream(final IMemoryBuffer buffer, final long offset) {
        wrap(buffer, offset);
    }

    /**
     * Wrap a given buffer beginning with an offset of 0.
     *
     * @param buffer
     *            to wrap
     */
    public void wrap(final IMemoryBuffer buffer) {
        wrap(buffer, 0);
    }

    /**
     * Wrap a given buffer beginning at an offset.
     *
     * @param buffer
     *            to wrap
     * @param offset
     *            at which the puts will occur.
     */
    public void wrap(final IMemoryBuffer buffer, final long offset) {
        Assertions.checkNotNull(buffer, "Buffer must not be null");
        //        if (!buffer.isExpandable()) {
        //            throw new IllegalStateException("buffer must be expandable.");
        //        }

        this.buffer = buffer;
        this.offset = offset;
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
     * Write a byte to buffer.
     *
     * @param b
     *            to be written.
     */
    @Override
    public void write(final int b) {
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
     */
    @Override
    public void write(final byte[] srcBytes, final int srcOffset, final int length) {
        buffer.putBytes(offset + position, srcBytes, srcOffset, length);
        position += length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }
}
