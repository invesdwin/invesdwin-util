package de.invesdwin.util.streams.buffer.bytes.stream;

import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * {@link OutputStream} that wraps an underlying expandable version of a {@link IByteBuffer}.
 */
@NotThreadSafe
public class ExpandableByteBufferOutputStream extends OutputStream {
    private IByteBuffer buffer;
    private int offset;
    private int position;

    /**
     * Default constructor.
     */
    public ExpandableByteBufferOutputStream() {
    }

    /**
     * Wrap given {@link IByteBuffer}.
     *
     * @param buffer
     *            to wrap.
     */
    public ExpandableByteBufferOutputStream(final IByteBuffer buffer) {
        wrap(buffer, 0);
    }

    /**
     * Wrap given {@link IByteBuffer} at a given offset.
     *
     * @param buffer
     *            to wrap.
     * @param offset
     *            at which the puts will occur.
     */
    public ExpandableByteBufferOutputStream(final IByteBuffer buffer, final int offset) {
        wrap(buffer, offset);
    }

    /**
     * Wrap a given buffer beginning with an offset of 0.
     *
     * @param buffer
     *            to wrap
     */
    public void wrap(final IByteBuffer buffer) {
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
    public void wrap(final IByteBuffer buffer, final int offset) {
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
