package de.invesdwin.util.streams.pool.buffered;

import java.io.DataInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.delegate.NonClosingDelegateInputStream;

@NotThreadSafe
public class BufferedFileDataInputStream extends InputStream implements DataInput {
    public static final int DEFAULT_BUFFER_SIZE = BufferedFileDataOutputStream.DEFAULT_BUFFER_SIZE;

    private final RandomAccessFile raf;
    private final FileChannel channel;

    private ICloseableByteBuffer buffer;
    private final java.nio.ByteBuffer nioBuffer;

    private long bufferPos;
    private long channelLimit;

    private NonClosingDelegateInputStream nonClosing;

    public BufferedFileDataInputStream(final File file) throws FileNotFoundException {
        this(file, DEFAULT_BUFFER_SIZE);
    }

    public BufferedFileDataInputStream(final File file, final int bufferSize) throws FileNotFoundException {
        // for backwards compatiblity with file interface, we still use RandomAccessFile
        this.raf = new RandomAccessFile(file, "r");
        this.channel = raf.getChannel();
        try {
            channelLimit = channel.size();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        this.buffer = getBufferPool().borrowObject();
        this.nioBuffer = buffer.asNioByteBuffer(0, bufferSize);
        ByteBuffers.limit(this.nioBuffer, 0);
    }

    public BufferedFileDataInputStream(final Path path) throws IOException {
        this(path, DEFAULT_BUFFER_SIZE);
    }

    public BufferedFileDataInputStream(final Path path, final int bufferSize) throws IOException {
        this.raf = null;
        this.channel = FileChannel.open(path, StandardOpenOption.READ);
        try {
            channelLimit = channel.size();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        this.buffer = getBufferPool().borrowObject();
        this.nioBuffer = buffer.asNioByteBuffer(0, bufferSize);
        ByteBuffers.limit(this.nioBuffer, 0);
    }

    public InputStream asNonClosing() {
        if (nonClosing == null) {
            nonClosing = new NonClosingDelegateInputStream(this);
        }
        return nonClosing;
    }

    public IByteBuffer getBuffer() {
        return buffer;
    }

    public java.nio.ByteBuffer getNioBuffer() {
        return nioBuffer;
    }

    protected IObjectPool<ICloseableByteBuffer> getBufferPool() {
        return ByteBuffers.DIRECT_EXPANDABLE_POOL;
    }

    private boolean fillBuffer() throws IOException {
        ByteBuffers.position(nioBuffer, 0);
        final int limit = (int) Math.min((channelLimit - channel.position()), nioBuffer.capacity());
        ByteBuffers.limit(nioBuffer, limit);
        if (limit == 0) {
            return false;
        }
        bufferPos = channel.position();
        channel.read(nioBuffer);
        ByteBuffers.flip(nioBuffer);
        return true;
    }

    @Override
    public int read() throws IOException {
        if (nioBuffer.remaining() == 0) {
            if (!fillBuffer()) {
                return -1;
            }
        }
        return nioBuffer.get() & 0xFF;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (nioBuffer.remaining() == 0) {
            if (!fillBuffer()) {
                return -1;
            }
        }
        final int read = Math.min(len, nioBuffer.remaining());
        nioBuffer.get(b, off, read);
        return read;
    }

    public long position() throws IOException {
        return bufferPos + nioBuffer.position();
    }

    public long limit() {
        return channelLimit;
    }

    public void limit(final long limit) {
        this.channelLimit = limit;
    }

    public void position(final long addr) throws IOException {
        if (addr >= bufferPos && addr <= bufferPos + nioBuffer.limit()) {
            ByteBuffers.position(nioBuffer, (int) (addr - bufferPos));
        } else {
            channel.position(addr);
            bufferPos = addr;
            nioBuffer.position(0);
            ByteBuffers.limit(nioBuffer, 0);
        }
    }

    @Override
    public final void close() throws IOException {
        if (buffer != null) {
            if (raf != null) {
                raf.close();
            }
            channel.close();
            getBufferPool().returnObject(buffer);
            buffer = null;
            onClose();
        }
    }

    protected void onClose() {}

    @Override
    public String readUTF() throws IOException {
        return InputStreams.readUTF(this);
    }

    @Override
    public String readLine() throws IOException {
        return InputStreams.readLine(this);
    }

    @Override
    public double readDouble() throws IOException {
        return InputStreams.readDouble(this);
    }

    @Override
    public float readFloat() throws IOException {
        return InputStreams.readFloat(this);
    }

    @Override
    public long readLong() throws IOException {
        return InputStreams.readLong(this);
    }

    @Override
    public int readInt() throws IOException {
        return InputStreams.readInt(this);
    }

    @Override
    public char readChar() throws IOException {
        return InputStreams.readChar(this);
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return InputStreams.readUnsignedShort(this);
    }

    @Override
    public short readShort() throws IOException {
        return InputStreams.readShort(this);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return InputStreams.readBoolean(this);
    }

    @Override
    public int skipBytes(final int n) throws IOException {
        return InputStreams.skipBytes(this, n);
    }

    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        InputStreams.readFullyNoTimeout(this, b, off, len);
    }

    @Override
    public void readFully(final byte[] b) throws IOException {
        InputStreams.readFullyNoTimeout(this, b);
    }

    @Override
    public byte readByte() throws IOException {
        return InputStreams.readByte(this);
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return InputStreams.readUnsignedByte(this);
    }
}
