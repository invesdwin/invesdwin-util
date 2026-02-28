package de.invesdwin.util.streams.expandable;

import static java.nio.charset.StandardCharsets.UTF_16BE;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.UTFDataFormatException;
import java.util.PrimitiveIterator;
import java.util.UUID;

import javax.annotation.concurrent.NotThreadSafe;

import org.jspecify.annotations.Nullable;

import de.invesdwin.util.collections.primitive.util.DirectByteArrayAccess;
import de.invesdwin.util.streams.closeable.ISafeCloseable;
import it.unimi.dsi.fastutil.io.MeasurableStream;
import it.unimi.dsi.fastutil.io.RepositionableStream;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Origin:
 * https://github.com/magicprinc/fastutil-concurrent-wrapper/blob/master/src/main/java/com/trivago/fastutilconcurrentwrapper/io/BAIS.java
 * 
 * @see java.io.ByteArrayInputStream
 * 
 * @see it.unimi.dsi.fastutil.io.FastByteArrayInputStream
 * 
 * @see it.unimi.dsi.fastutil.io.FastMultiByteArrayInputStream
 * @see it.unimi.dsi.fastutil.io.FastBufferedInputStream
 */
@NotThreadSafe
public class AccessibleByteArrayInputStream extends java.io.ByteArrayInputStream
        implements ObjectInput, MeasurableStream, RepositionableStream, ISafeCloseable, PrimitiveIterator.OfInt {
    protected int offset;

    public AccessibleByteArrayInputStream(final byte[] array, @PositiveOrZero final int offset,
            @PositiveOrZero final int maxLength) {
        super(array, offset, maxLength);
        this.offset = offset;
        pos = 0;
        mark = 0;
        count = Math.min(maxLength, array.length - offset);// offset|0..pos..length|...array.length
    }//new

    public AccessibleByteArrayInputStream(final byte[] array) {
        super(array);
    }//new

    public AccessibleByteArrayInputStream(final java.io.ByteArrayOutputStream baos) {
        super(baos instanceof AccessibleByteArrayOutputStream ? ((AccessibleByteArrayOutputStream) baos).array()
                : baos.toByteArray(), 0, baos.size());
    }//new

    /// The array backing the input stream. capacity = array().length
    public byte[] array() {
        return buf;
    }

    public void array(final byte[] array, @PositiveOrZero final int offset, @PositiveOrZero final int maxLength) {
        buf = array;
        this.offset = offset;
        pos = 0;
        mark = 0;
        count = Math.min(maxLength, array.length - offset);
    }

    /** The first valid entry. aka {@link #position()} */
    public @PositiveOrZero int offset() {
        return offset;
    }

    public void offset(@PositiveOrZero final int newOffset) {
        offset = newOffset;
    }

    /** The current position as a distance from {@link #offset}. */
    @Override
    public long position() {
        return pos;
    }

    public int readerIndex() {
        return pos;
    }

    public int markIndex() {
        return mark;
    }

    @Override
    public void position(final long newPosition) {
        pos = (int) Math.min(newPosition, limit());
    }

    public void readerIndex(final int newReaderIndex) {
        pos = Math.min(newReaderIndex, limit());
    }

    /** The number of valid bytes in {@link #array} starting from {@link #offset}. */
    @Override
    public @PositiveOrZero long length() {
        return count;
    }

    public @PositiveOrZero int limit() {
        return count;
    }

    public void limit(@PositiveOrZero final int maxLength) {
        count = Math.min(maxLength, array().length - offset);
    }

    /** Closing a fast byte array input stream has no effect. */
    @Override
    public void close() {}

    @Override
    public int available() {
        return count - pos;
    }// length - position

    @Override
    public long skip(@PositiveOrZero final long n) {
        long nRet = n;
        final int avail = count - pos;
        if (nRet <= avail) {
            pos += (int) nRet;
            return nRet;
        }
        nRet = avail;
        pos = count;// position = length
        return nRet;
    }

    @Override
    public int read() {
        if (count <= pos) {
            return -1;// EOF
        }
        return buf[offset + pos++] & 0xFF;
    }

    /**
     * Reads bytes from this byte-array input stream as specified in {@link java.io.InputStream#read(byte[], int, int)}.
     * 
     * Note! The implementation given in {@link java.io.ByteArrayInputStream#read(byte[], int, int)} will return -1 on a
     * 0-length read at EOF, contrarily to the specification. We won't.
     */
    @Override
    public int read(final byte[] b, @PositiveOrZero final int fromOffset, @PositiveOrZero final int length) {
        if (this.count <= this.pos) {
            return length == 0 ? 0 : -1;
        }
        final int n = Math.min(length, this.count - this.pos);
        System.arraycopy(buf, this.offset + this.pos, b, fromOffset, n);
        pos += n;
        return n;
    }

    @Override
    public byte[] readAllBytes() {
        return readNBytes(available());
    }

    @Override
    public int read(final byte[] b) {
        return read(b, 0, b.length);
    }

    @Override
    public byte[] readNBytes(final int len) {
        final int n = Math.min(len, available());
        final byte[] result = new byte[n];
        read(result);
        return result;
    }

    @Override
    public void skipNBytes(final long n) {
        skip(n);
    }

    // read next byte without shifting readerIndex
    public int peek() {
        if (count <= pos) {
            return -1;
        }
        return buf[offset + pos] & 0xFF;
    }

    @Override
    public void readFully(final byte[] b) {
        read(b);
    }

    @Override
    public void readFully(final byte[] b, final int off, final int len) {
        read(b, off, len);
    }

    @Override
    public int skipBytes(@PositiveOrZero final int n) {
        return (int) skip(n);
    }//DataInput#skipBytes

    @Override
    public boolean readBoolean() {
        return read() != 0;
    }

    @Override
    public byte readByte() {
        return (byte) read();
    }

    @Override
    public int readUnsignedByte() {
        return read() & 0xFF;
    }

    /// @see DataInputStream#readShort (short)((read() << 8)|(read() & 0xFF))
    @Override
    public short readShort() {
        if (count < pos + 2) {
            throw new ArrayIndexOutOfBoundsException("readShort, but " + available());// EOF
        }
        final short v = DirectByteArrayAccess.getShort(buf, pos);
        pos += 2;
        return v;
    }

    @Override
    public int readUnsignedShort() {
        if (count < pos + 2) {
            throw new ArrayIndexOutOfBoundsException("readUnsignedShort, but " + available());// EOF
        }
        final int v = DirectByteArrayAccess.getUnsignedShort(buf, pos);
        pos += 2;
        return v;
    }

    @Override
    public char readChar() {
        if (count < pos + 2) {
            throw new ArrayIndexOutOfBoundsException("readChar, but " + available());// EOF
        }
        final char v = DirectByteArrayAccess.getChar(buf, pos);
        pos += 2;
        return v;
    }

    @Override
    public int readInt() {
        if (count < pos + 4) {
            throw new ArrayIndexOutOfBoundsException("readInt, but " + available());// EOF
        }
        //return read() << 24 | ((read() & 0xFF) << 16) | ((read() & 0xFF) << 8) | (read() & 0xFF);
        final int v = DirectByteArrayAccess.getInt(buf, pos);
        pos += 4;
        return v;
    }

    public int readMedium() {
        return ((read() & 0xFF) << 16) | ((read() & 0xFF) << 8) | (read() & 0xFF);
    }

    /// @see UUID#UUID(long, long)
    /// @see UUID#fromString(String)
    public UUID readUUID() {
        if (count < pos + 16) {
            throw new ArrayIndexOutOfBoundsException("readUUID, but " + available());// EOF
        }
        //val bb = ByteBuffer.wrap(bytes); long mostSigBits = bb.getLong(); long leastSigBits = bb.getLong();  быстрее за счёт VarHandle
        final UUID uuid = DirectByteArrayAccess.getUUID(buf, pos);
        pos += 16;
        return uuid;
    }

    @Override
    public long readLong() {
        if (count < pos + 8) {
            throw new ArrayIndexOutOfBoundsException("readLong, but " + available());// EOF
        }
        //return (long) readInt() << 32 | (readInt() & 0xFFFF_FFFFL);
        final long v = DirectByteArrayAccess.getLong(buf, pos);
        pos += 8;
        return v;
    }

    @Override
    public float readFloat() {
        if (count < pos + 4) {
            throw new ArrayIndexOutOfBoundsException("readFloat, but " + available());// EOF
        }
        final float v = DirectByteArrayAccess.getFloat(buf, pos);
        pos += 4;
        return v;
    }

    @Override
    public double readDouble() {
        if (count < pos + 8) {
            throw new ArrayIndexOutOfBoundsException("readDouble, but " + available());// EOF
        }
        final double v = DirectByteArrayAccess.getDouble(buf, pos);
        pos += 8;
        return v;
    }

    @Override
    @Deprecated
    public String readLine() {
        final StringBuilder sb = new StringBuilder(160);
        //CHECKSTYLE:OFF
        loop: for (int c;;) {
            switch (c = read()) {
            //CHECKSTYLE:ON
            case -1:
                break loop;// eof

            case '\n':
                return sb.toString();
            case '\r':
                if (peek() == '\n') { // CR LF
                    read();
                }
                return sb.toString();

            default:
                sb.append((char) c);
            }
        }
        return sb.isEmpty() ? null : sb.toString();
    }

    @Override
    public @Nullable String readUTF() throws UTFDataFormatException {
        try {
            return available() > 2 ? DataInputStream.readUTF(this) : null;
        } catch (final UTFDataFormatException badBinaryFormatting) {
            throw badBinaryFormatting;
        } catch (final IOException e) {
            final UTFDataFormatException t = new UTFDataFormatException("IOException: readUTF @ " + this);
            t.initCause(e);
            throw t;
        }
    }

    /// not efficient! Only added to support custom {@link java.io.Externalizable} todo size instead of magic prefix;
    /// see io.netty.handler.codec.serialization.CompactObjectInputStream
    @Override
    public Object readObject() throws ClassNotFoundException, IOException {
        try (ObjectInputStream ois = new ObjectInputStream(this)) {
            return ois.readObject();
        }
    }

    /// @see BAOS#writeBytes(String)
    /// @see java.nio.charset.StandardCharsets#ISO_8859_1
    public String readLatin1String(@PositiveOrZero final int strLen) {
        final int strLenMin = Math.min(strLen, available());
        @SuppressWarnings("deprecation")
        final String s = new String(buf, 0, pos, strLenMin);
        pos += strLenMin;
        return s;
    }

    /// @see java.nio.charset.StandardCharsets#UTF_16BE
    /// @see BAOS#writeChars
    public String readUTF16String(@PositiveOrZero final int strLen) {
        final int byteLen = Math.min(strLen << 1, available());//*2
        final String s = new String(buf, pos, byteLen, UTF_16BE);
        pos += byteLen;
        return s;
    }

    @Override
    public boolean hasNext() {
        return pos < count;// position < length: available() > 0 == (length - position) > 0
    }

    @Override
    public int nextInt() {
        return read();
    }
}