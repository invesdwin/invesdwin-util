package de.invesdwin.util.streams.pool;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.pool.AgronaObjectPool;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.lang.Charsets;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;

@NotThreadSafe
public final class PooledFastByteArrayOutputStream extends FastByteArrayOutputStream {

    private static final IObjectPool<PooledFastByteArrayOutputStream> POOL = new AgronaObjectPool<PooledFastByteArrayOutputStream>(
            () -> new PooledFastByteArrayOutputStream());

    private NonClosingDelegateOutputStream nonClosing;
    private boolean closed = true;
    private FastByteArrayInputStream inputStream;

    private PooledFastByteArrayOutputStream() {
    }

    private PooledFastByteArrayOutputStream init() {
        if (!closed) {
            throw new IllegalStateException("not closed");
        }
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
            reset();
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
            inputStream = new FastByteArrayInputStream(array, 0, length) {
                @Override
                public void close() {
                    super.close();
                    PooledFastByteArrayOutputStream.this.close();
                }
            };
        } else {
            inputStream.array = array;
            inputStream.offset = 0;
            inputStream.length = length;
        }
        return inputStream;
    }

}
