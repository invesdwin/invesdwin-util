package de.invesdwin.util.streams.pool;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.io.MeasurableInputStream;
import it.unimi.dsi.fastutil.io.RepositionableStream;

/**
 * Adapted from: it.unimi.dsi.fastutil.io.FastByteArrayInputStream
 *
 */
@NotThreadSafe
public class PooledFastByteArrayInputStream extends MeasurableInputStream implements RepositionableStream {

    private final PooledFastByteArrayOutputStream delegate;

    /** The current position as a distance from {@link #offset}. */
    private int position;

    /** The current mark as a position, or -1 if no mark exists. */
    private int mark;

    public PooledFastByteArrayInputStream(final PooledFastByteArrayOutputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void reset() {
        position = mark;
    }

    public void init() {
        position = 0;
        mark = 0;
    }

    /** Closing a fast byte array input stream has no effect. */
    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void mark(final int dummy) {
        mark = position;
    }

    @Override
    public int available() {
        return delegate.length - position;
    }

    @Override
    public long skip(final long pN) {
        long n = pN;
        if (n <= delegate.length - position) {
            position += (int) n;
            return n;
        }
        n = delegate.length - position;
        position = delegate.length;
        return n;
    }

    @Override
    public int read() {
        if (delegate.length == position) {
            return -1;
        }
        return delegate.array[position++] & 0xFF;
    }

    /**
     * Reads bytes from this byte-array input stream as specified in {@link java.io.InputStream#read(byte[], int, int)}.
     * Note that the implementation given in {@link java.io.ByteArrayInputStream#read(byte[], int, int)} will return -1
     * on a zero-length read at EOF, contrarily to the specification. We won't.
     */

    @Override
    public int read(final byte[] b, final int offset, final int length) {
        if (delegate.length == this.position) {
            return length == 0 ? 0 : -1;
        }
        final int n = Math.min(length, delegate.length - this.position);
        System.arraycopy(delegate.array, this.position, b, offset, n);
        this.position += n;
        return n;
    }

    @Override
    public long position() {
        return position;
    }

    @Override
    public void position(final long newPosition) {
        position = (int) Math.min(newPosition, delegate.length);
    }

    @Override
    public long length() {
        return delegate.length;
    }
}
