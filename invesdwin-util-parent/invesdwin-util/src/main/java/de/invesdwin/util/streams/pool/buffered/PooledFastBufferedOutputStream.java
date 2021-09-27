package de.invesdwin.util.streams.pool.buffered;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.pool.AgronaObjectPool;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.streams.NonClosingDelegateOutputStream;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@NotThreadSafe
public final class PooledFastBufferedOutputStream extends ReusableFastBufferedOutputStream {

    private static final IObjectPool<PooledFastBufferedOutputStream> POOL = new AgronaObjectPool<PooledFastBufferedOutputStream>(
            () -> new PooledFastBufferedOutputStream());

    private NonClosingDelegateOutputStream nonClosing;

    private PooledFastBufferedOutputStream() {
        super(ByteBuffers.allocateByteArray(DEFAULT_BUFFER_SIZE));
    }

    @Override
    public PooledFastBufferedOutputStream init(final OutputStream os) {
        super.init(os);
        return this;
    }

    public OutputStream asNonClosing() {
        if (nonClosing == null) {
            nonClosing = new NonClosingDelegateOutputStream(this);
        }
        return nonClosing;
    }

    @Override
    public void close() throws IOException {
        if (os != null) {
            super.close();
            POOL.returnObject(this);
        }
    }

    public static PooledFastBufferedOutputStream newInstance(final OutputStream out) {
        return POOL.borrowObject().init(out);
    }

}
