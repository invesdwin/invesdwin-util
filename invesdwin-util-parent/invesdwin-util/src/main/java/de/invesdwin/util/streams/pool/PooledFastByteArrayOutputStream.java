package de.invesdwin.util.streams.pool;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.pool.AgronaObjectPool;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.lang.Charsets;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;

@NotThreadSafe
public final class PooledFastByteArrayOutputStream extends FastByteArrayOutputStream {

    private static final IObjectPool<PooledFastByteArrayOutputStream> POOL = new AgronaObjectPool<PooledFastByteArrayOutputStream>(
            () -> new PooledFastByteArrayOutputStream());

    private NonClosingDelegateOutputStream nonClosing;
    private boolean closed = true;
    private PooledFastByteArrayInputStream inputStream;

    private PooledFastByteArrayOutputStream() {
    }

    private PooledFastByteArrayOutputStream init() {
        if (!closed) {
            throw new IllegalStateException("not closed");
        }
        reset();
        closed = false;
        return this;
    }

    public OutputStream asNonClosing() {
        if (nonClosing == null) {
            nonClosing = new NonClosingDelegateOutputStream(this);
        }
        return nonClosing;
    }

    @Override
    public void close() {
        if (!closed) {
            POOL.returnObject(this);
            closed = true;
        }
    }

    public byte[] toByteArray() {
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
        System.arraycopy(array, 0, bytes, 0, length);
        return bytes;
    }

    @Override
    public String toString() {
        return new String(toByteArray(), Charsets.UTF_8);
    }

    public static PooledFastByteArrayOutputStream newInstance() {
        return POOL.borrowObject().init();
    }

    public InputStream asInputStream() {
        if (inputStream == null) {
            inputStream = new PooledFastByteArrayInputStream(this);
        } else {
            inputStream.init();
        }
        return inputStream;
    }

}
