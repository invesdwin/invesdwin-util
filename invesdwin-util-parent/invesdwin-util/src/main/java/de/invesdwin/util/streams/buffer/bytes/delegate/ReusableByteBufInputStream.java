package de.invesdwin.util.streams.buffer.bytes.delegate;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.lang.string.Strings;
import io.netty.buffer.ByteBuf;

@NotThreadSafe
public class ReusableByteBufInputStream extends InputStream implements DataInput {
    private ByteBuf buffer;
    private StringBuilder lineBuf;
    private boolean lineBufComplete = false;

    public void wrap(final ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public int available() throws IOException {
        return buffer.readableBytes();
    }

    public int lineBufLength() {
        if (lineBuf == null) {
            return 0;
        }
        return lineBuf.length();
    }

    // Suppress a warning since the class is not thread-safe
    @Override
    public void mark(final int readlimit) {
        buffer.markReaderIndex();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        final int available = available();
        if (available == 0) {
            return -1;
        }
        return buffer.readByte() & 0xff;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int available = available();
        if (available == 0) {
            return -1;
        }

        final int readLen = Math.min(available, len);
        buffer.readBytes(b, off, readLen);
        return readLen;
    }

    // Suppress a warning since the class is not thread-safe
    @Override
    public void reset() throws IOException {
        buffer.resetReaderIndex();
    }

    @Override
    public long skip(final long n) throws IOException {
        if (n > Integer.MAX_VALUE) {
            return skipBytes(Integer.MAX_VALUE);
        } else {
            return skipBytes((int) n);
        }
    }

    @Override
    public boolean readBoolean() throws IOException {
        checkAvailable(1);
        return read() != 0;
    }

    @Override
    public byte readByte() throws IOException {
        final int available = available();
        if (available == 0) {
            throw FastEOFException.getInstance("available = 0");
        }
        return buffer.readByte();
    }

    @Override
    public char readChar() throws IOException {
        return (char) readShort();
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public void readFully(final byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        checkAvailable(len);
        buffer.readBytes(b, off, len);
    }

    @Override
    public int readInt() throws IOException {
        checkAvailable(4);
        return buffer.readInt();
    }

    @Override
    public String readLine() throws IOException {
        int available = available();
        if (available == 0) {
            return null;
        }

        if (lineBuf != null && lineBufComplete) {
            lineBuf.setLength(0);
            lineBufComplete = false;
        }

        loop: do {
            final int c = buffer.readUnsignedByte();
            --available;
            switch (c) {
            case '\n':
                lineBufComplete = true;
                break loop;

            case '\r':
                if (available > 0 && (char) buffer.getUnsignedByte(buffer.readerIndex()) == '\n') {
                    buffer.skipBytes(1);
                    --available;
                }
                lineBufComplete = true;
                break loop;

            default:
                if (lineBuf == null) {
                    lineBuf = new StringBuilder();
                }
                lineBuf.append((char) c);
            }
        } while (available > 0);

        if (lineBufComplete) {
            return lineBuf != null && lineBuf.length() > 0 ? lineBuf.toString() : Strings.EMPTY;
        } else {
            return Strings.EMPTY;
        }
    }

    @Override
    public long readLong() throws IOException {
        checkAvailable(8);
        return buffer.readLong();
    }

    @Override
    public short readShort() throws IOException {
        checkAvailable(2);
        return buffer.readShort();
    }

    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return readByte() & 0xff;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return readShort() & 0xffff;
    }

    @Override
    public int skipBytes(final int n) throws IOException {
        final int nBytes = Math.min(available(), n);
        buffer.skipBytes(nBytes);
        return nBytes;
    }

    private void checkAvailable(final int fieldSize) throws IOException {
        if (fieldSize < 0) {
            throw FastIndexOutOfBoundsException.getInstance("fieldSize cannot be a negative number: %s", fieldSize);
        }
        if (fieldSize > available()) {
            throw FastEOFException
                    .getInstance("fieldSize is too long! Length is " + fieldSize + ", but maximum is " + available());
        }
    }

}
