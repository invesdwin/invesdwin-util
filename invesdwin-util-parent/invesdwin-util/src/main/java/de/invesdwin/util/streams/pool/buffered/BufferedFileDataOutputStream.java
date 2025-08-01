package de.invesdwin.util.streams.pool.buffered;

import java.io.DataOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.delegate.NonClosingDelegateOutputStream;

@NotThreadSafe
public class BufferedFileDataOutputStream extends OutputStream implements DataOutput {
    public static final int DEFAULT_BUFFER_SIZE = Integers
            .checkedCast(ByteSizeScale.BYTES.convert(64D, ByteSizeScale.KILOBYTES));

    private ICloseableByteBuffer buffer;
    private final java.nio.ByteBuffer nioBuffer;

    private final RandomAccessFile randomAccessFile;
    private final FileChannel channel;

    private NonClosingDelegateOutputStream nonClosing;

    public BufferedFileDataOutputStream(final File file) throws FileNotFoundException {
        this(file, DEFAULT_BUFFER_SIZE);
    }

    public BufferedFileDataOutputStream(final File file, final int bufferSize) throws FileNotFoundException {
        // for backwards compatiblity with file interface, we still use RandomAccessFile
        this.randomAccessFile = new RandomAccessFile(file, "rw");
        this.channel = randomAccessFile.getChannel();

        this.buffer = getBufferPool().borrowObject();
        this.nioBuffer = buffer.asNioByteBuffer(0, bufferSize);
    }

    public BufferedFileDataOutputStream(final Path path) throws IOException {
        this(path, DEFAULT_BUFFER_SIZE);
    }

    public BufferedFileDataOutputStream(final Path path, final int bufferSize) throws IOException {
        this.randomAccessFile = null;
        this.channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ,
                StandardOpenOption.WRITE);

        this.buffer = getBufferPool().borrowObject();
        this.nioBuffer = buffer.asNioByteBuffer(0, bufferSize);
    }

    public OutputStream asNonClosing() {
        if (nonClosing == null) {
            nonClosing = new NonClosingDelegateOutputStream(this);
        }
        return nonClosing;
    }

    public java.nio.ByteBuffer getNioBuffer() {
        return nioBuffer;
    }

    public IByteBuffer getBuffer() {
        return buffer;
    }

    protected IObjectPool<ICloseableByteBuffer> getBufferPool() {
        return ByteBuffers.EXPANDABLE_POOL;
    }

    @Override
    public void write(final int b) throws IOException {
        if (nioBuffer.remaining() == 0) {
            flush();
        }
        nioBuffer.put((byte) b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        int current = off;
        final int end = off + len;
        while (current < end) {
            final int size = Math.min(nioBuffer.remaining(), end - current);
            if (size == 0) {
                flush();
                continue;
            }
            nioBuffer.put(b, current, size);
            current += size;
        }
    }

    @Override
    public void writeBoolean(final boolean v) throws IOException {
        OutputStreams.writeBoolean(this, v);
    }

    @Override
    public void writeByte(final int v) throws IOException {
        OutputStreams.writeByte(this, v);
    }

    @Override
    public void writeShort(final int v) throws IOException {
        OutputStreams.writeShort(this, v);
    }

    @Override
    public void writeChar(final int v) throws IOException {
        OutputStreams.writeChar(this, v);
    }

    @Override
    public void writeInt(final int v) throws IOException {
        OutputStreams.writeInt(this, v);
    }

    @Override
    public void writeLong(final long v) throws IOException {
        OutputStreams.writeLong(this, v);
    }

    @Override
    public void writeFloat(final float v) throws IOException {
        OutputStreams.writeFloat(this, v);
    }

    @Override
    public void writeDouble(final double v) throws IOException {
        OutputStreams.writeDouble(this, v);
    }

    @Override
    public void writeBytes(final String s) throws IOException {
        OutputStreams.writeBytes(this, s);
    }

    @Override
    public void writeChars(final String s) throws IOException {
        OutputStreams.writeChars(this, s);
    }

    @Override
    public void writeUTF(final String str) throws IOException {
        OutputStreams.writeUTF(this, str);
    }

    @Override
    public void flush() throws IOException {
        ByteBuffers.flip(nioBuffer);
        channel.write(nioBuffer);
        nioBuffer.clear();
    }

    @Override
    public final void close() throws IOException {
        if (buffer != null) {
            flush();
            syncWithFilesystem();
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            channel.close();
            getBufferPool().returnObject(buffer);
            buffer = null;
            onClose();
        }
    }

    /**
     * Override with no-op to disable.
     */
    protected void syncWithFilesystem() throws IOException {
        //sync with filesystem
        //        channel.force(true);
    }

    protected void onClose() {}

    public long position() throws IOException {
        return channel.position() + nioBuffer.position();
    }

    public void seek(final long addr) throws IOException {
        channel.position(addr);
        ByteBuffers.position(nioBuffer, 0);
        ByteBuffers.limit(nioBuffer, 0);
    }

    public void sync() throws IOException {
        flush();
        syncWithFilesystem();
    }

    public FileChannel getChannel() {
        return channel;
    }

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }
}
