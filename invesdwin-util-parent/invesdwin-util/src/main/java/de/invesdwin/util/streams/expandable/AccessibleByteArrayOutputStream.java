package de.invesdwin.util.streams.expandable;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.primitive.util.DirectByteArrayAccess;
import de.invesdwin.util.streams.closeable.ISafeCloseable;
import it.unimi.dsi.fastutil.io.MeasurableStream;
import it.unimi.dsi.fastutil.io.RepositionableStream;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Origin:
 * https://github.com/magicprinc/fastutil-concurrent-wrapper/blob/master/src/main/java/com/trivago/fastutilconcurrentwrapper/io/BAOS.java
 * 
 * Simple, fast byte-array output stream that exposes the backing array.
 *
 * <p>
 * {@link java.io.ByteArrayOutputStream} is nice, but to get its content you must generate each time a new object. This
 * doesn't happen here.
 *
 * <p>
 * This class will automatically enlarge the backing array, doubling its size whenever new space is needed. The
 * {@link #reset()} method will mark the content as empty, but will not decrease the capacity: use {@link #trim()} for
 * that purpose.
 * 
 * @see AccessibleByteArrayInputStream
 * 
 * @see org.springframework.util.ResizableByteArrayOutputStream
 * @see org.springframework.util.FastByteArrayOutputStream
 * @see it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
 * 
 * @see it.unimi.dsi.fastutil.io.FastByteArrayInputStream
 * @see it.unimi.dsi.fastutil.io.FastMultiByteArrayInputStream
 * 
 * @see it.unimi.dsi.fastutil.io.FastBufferedOutputStream
 * @see it.unimi.dsi.fastutil.io.FastBufferedInputStream
 * 
 * @see java.io.DataOutputStream
 * @see com.google.common.io.ByteArrayDataOutput
 * 
 * @see java.nio.ByteBuffer
 * @see io.netty.buffer.ByteBuf
 * @see io.vertx.core.buffer.Buffer
 * @see okio.Buffer
 */
@NotThreadSafe
public class AccessibleByteArrayOutputStream extends java.io.ByteArrayOutputStream
        implements RepositionableStream, ObjectOutput, MeasurableStream, ISafeCloseable, Appendable {
    /** The current writing position. */
    protected int position;

    /// Creates a new array output stream with an initial capacity of 160 bytes.
    public AccessibleByteArrayOutputStream() {
        super(160);
    }//new

    /// Creates a new array output stream with a given initial capacity.
    ///
    /// @param initialCapacity
    ///            the initial length of the backing array.
    public AccessibleByteArrayOutputStream(@PositiveOrZero final int initialCapacity) {
        super(initialCapacity);
    }//new

    /// Creates a new array output stream wrapping a given byte array.
    ///
    /// @param a
    ///            the byte array to wrap.
    public AccessibleByteArrayOutputStream(final byte[] a) {
        super(0);
        buf = a;
    }//new

    public AccessibleByteArrayOutputStream(final byte[] a, @PositiveOrZero final int length) {
        super(0);
        buf = a;
        count = Math.min(length, a.length);
    }//new

    public AccessibleByteArrayOutputStream(final java.io.ByteArrayOutputStream baos) {
        super(0);
        count = baos.size();
        if (baos instanceof AccessibleByteArrayOutputStream) {
            final AccessibleByteArrayOutputStream us = (AccessibleByteArrayOutputStream) baos;
            buf = us.array();
            position = us.writerIndex();
        } else {
            buf = baos.toByteArray();
            position = count;
        }
    }//new ~ clone

    /// The array backing the output stream.
    ///
    /// @see #buf
    public byte[] array() {
        return buf;
    }

    public void array(final byte[] array) {
        buf = array;
        count = 0;
        position = 0;
    }

    /// Marks this array output stream as empty.
    @Override
    public void reset() {
        count = 0;
        position = 0;
    }

    /// Ensures that the length of the backing array is equal to [#length].
    ///
    /// @see #resize(int)
    public byte[] trim() {
        buf = it.unimi.dsi.fastutil.bytes.ByteArrays.trim(buf, count);
        return buf;
    }

    @Override
    public void write(final int b) {
        grow(1);
        buf[position++] = (byte) b;
        if (count < position) {
            count = position;
        }
    }

    @Override
    public void write(final byte[] b, @PositiveOrZero final int off, @PositiveOrZero final int len) {
        //ByteArrays.ensureOffsetLength(b, off, len);
        java.util.Objects.checkFromIndexSize(off, len, b.length);
        grow(len);
        System.arraycopy(b, off, buf, position, len);
        position += len;
        if (count < position) {
            count = position;
        }
    }

    @Override
    public void position(@PositiveOrZero final long newPosition) {
        position = (int) Math.min(newPosition, buf.length);
    }

    public void writerIndex(@PositiveOrZero final int newPosition) {
        position = Math.min(newPosition, buf.length);
    }

    @Override
    public @PositiveOrZero long position() {
        return position;
    }

    public @PositiveOrZero int writerIndex() {
        return position;
    }

    @Override
    public @PositiveOrZero long length() {
        return count;
    }

    @Override
    public @PositiveOrZero int size() {
        return count;
    }

    /// Return the current size of this stream's internal buffer.
    public @PositiveOrZero int capacity() {
        return buf.length;
    }

    /// Resize the internal buffer size to a specified capacity.
    ///
    /// @param targetCapacity
    ///            the desired size of the buffer ×throws IllegalArgumentException if the given capacity is smaller than
    ///            the actual size of the content stored in the buffer already
    /// @see #size()
    /// @see #trim()
    /// @see org.springframework.util.ResizableByteArrayOutputStream#resize(int)
    public void resize(@PositiveOrZero final int targetCapacity) {
        //Assert.isTrue(targetCapacity >= count, "New capacity must not be smaller than current size");
        final byte[] resizedBuffer = new byte[targetCapacity];
        count = Math.min(this.count, targetCapacity);
        System.arraycopy(buf, 0, resizedBuffer, 0, count);
        buf = resizedBuffer;
    }

    /// Grow the internal buffer size.
    ///
    /// @param additionalCapacity
    ///            the number of bytes to add to the current buffer size
    /// @see #size()
    /// @see org.springframework.util.ResizableByteArrayOutputStream#grow(int)
    public void grow(@Positive final int len) {
        if (position + len > buf.length) {
            final int newLength = (int) Math.max(
                    Math.min((long) buf.length + (buf.length >> 1), it.unimi.dsi.fastutil.Arrays.MAX_ARRAY_SIZE),
                    position + len);
            final byte[] resizedBuffer = new byte[newLength];
            System.arraycopy(buf, 0, resizedBuffer, 0, count);
            buf = resizedBuffer;
        }
    }

    @Override
    public byte[] toByteArray() {
        return it.unimi.dsi.fastutil.bytes.ByteArrays.copy(buf, 0, count);
    }

    @Override
    public void close() {}// NOP: only to force no exception

    @Override
    public void write(final byte[] b) {
        write(b, 0, b.length);// Only to force no exception
    }

    /// Fast {@link java.nio.charset.StandardCharsets#ISO_8859_1} ×not {@link ByteArrayOutputStream#toString()}
    @Override
    public String toString() {
        return new String(buf, 0, 0, count);
    }

    @Override
    public String toString(final Charset charset) {
        return new String(buf, 0, count, charset);
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException {
        out.write(buf, 0, count);
    }

    @Override
    public void writeBoolean(final boolean v) {
        write(v ? 1 : 0);
    }

    @Override
    public void writeByte(final int v) {
        write(v);
    }

    @Override
    public void writeShort(final int v) {
        grow(2);
        DirectByteArrayAccess.setShort(buf, position, (short) v);
        position += 2;
        if (count < position) {
            count = position;
        }
    }

    @Override
    public void writeChar(final int v) {
        grow(2);
        DirectByteArrayAccess.setChar(buf, position, (char) v);
        position += 2;
        if (count < position) {
            count = position;
        }
    }

    @Override
    public void writeInt(final int v) {
        grow(4);
        DirectByteArrayAccess.setInt(buf, position, v);
        position += 4;
        if (count < position) {
            count = position;
        }
    }

    public void writeMedium(final int v) {
        write(v >> 16);
        writeShort(v);
    }

    public void writeUUID(final UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public void writeLong(final long v) {
        grow(8);
        DirectByteArrayAccess.setLong(buf, position, v);
        position += 8;
        if (count < position) {
            count = position;
        }
    }

    @Override
    public void writeFloat(final float v) {
        grow(4);
        DirectByteArrayAccess.setFloat(buf, position, v);
        position += 4;
        if (count < position) {
            count = position;
        }
    }

    @Override
    public void writeDouble(final double v) {
        grow(8);
        DirectByteArrayAccess.setDouble(buf, position, v);
        position += 8;
        if (count < position) {
            count = position;
        }
    }

    /**
     * @deprecated This method is dangerous as it discards the high byte of every character. For UTF-8, use
     *             {@link #writeUTF(String)} or {@link #write(byte[]) @code write(s.getBytes(UTF_8))}.
     * @see java.io.DataOutputStream#writeBytes(String)
     */
    @Deprecated
    @Override
    public void writeBytes(final String s) {
        //for (int i = 0, len = s.length(); i < len; i++)write((byte)s.charAt(i));
        final int len = s.length();
        grow(len);
        s.getBytes(0, len, buf, position);
        position += len;
        if (count < position) {
            count = position;
        }
    }

    /// @see com.trivago.fastutilconcurrentwrapper.io.BAIS#readUTF16String
    /// @see #append
    @Override
    public void writeChars(final String s) {
        append(s, 0, s.length());
    }

    @Override
    public void writeUTF(final String s) {
        final int savePos = position;
        writeShort(0);// len placeholder
        for (int i = 0, len = s.length(); i < len; i++) {
            writeUtf8Char(s.charAt(i));
            if (position - savePos > 0xFF_FF + 2) {
                count = savePos;// rollback
                position = savePos;
                throw new IllegalArgumentException(
                        "UTF encoded string too long: %d: %s".formatted(s.length(), s.substring(0, 99)));
            }
        }
        final int len = position - savePos - 2;
        buf[savePos] = (byte) (len >> 8);
        buf[savePos + 1] = (byte) len;
    }

    /// @see java.io.DataOutputStream#writeUTF(String,DataOutput)
    /// @see jdk.internal.util.ModifiedUtf#putChar(byte[], int, char)
    public int writeUtf8Char(final char c) {
        if (c != 0 && c < 0x80) {
            write(c);
            return 1;
        } else if (c >= 0x800) {
            write(0xE0 | c >> 12 & 0x0F);
            write(0x80 | c >> 6 & 0x3F);
            write(0x80 | c & 0x3F);
            return 3;
        } else {
            write(0xC0 | c >> 6 & 0x1F);
            write(0x80 | c & 0x3F);
            return 2;
        }
    }

    /// not efficient! Only added to support custom {@link java.io.Externalizable}
    @Override
    public void writeObject(final Object obj) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(this)) {
            oos.writeObject(obj);
            oos.flush();
        }
    }

    @Override
    public AccessibleByteArrayOutputStream append(final CharSequence csq) {
        append(csq, 0, csq.length());
        return this;
    }

    /// @see #write(byte[], int, int)
    /// @see #writeBytes(byte[])
    @Override
    public AccessibleByteArrayOutputStream append(final CharSequence csq, final int start, final int end) {
        final int len = end - start;
        java.util.Objects.checkFromIndexSize(start, len, csq.length());
        final int len2 = len << 1;
        grow(len2);

        DirectByteArrayAccess.copyCharsToByteArray(csq, start, buf, position, len);

        position += len2;
        if (count < position) {
            count = position;
        }
        return this;
    }

    /// @see #writeChar(int)
    @Override
    public AccessibleByteArrayOutputStream append(final char c) {
        grow(2);
        DirectByteArrayAccess.setChar(buf, position, c);
        position += 2;
        if (count < position) {
            count = position;
        }
        return this;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof AccessibleByteArrayOutputStream) {
            final AccessibleByteArrayOutputStream baos = (AccessibleByteArrayOutputStream) obj;
            return java.util.Arrays.equals(buf, 0, count, baos.array(), 0, baos.size());
        } else if (obj instanceof java.io.ByteArrayOutputStream) {
            final java.io.ByteArrayOutputStream baos = (java.io.ByteArrayOutputStream) obj;
            return java.util.Arrays.equals(buf, 0, count, baos.toByteArray(), 0, baos.size());
        } else if (obj instanceof byte[]) {
            final byte[] a = (byte[]) obj;
            return java.util.Arrays.equals(buf, 0, count, a, 0, a.length);
        } else if (obj instanceof CharSequence) {
            final CharSequence cs = (CharSequence) obj;
            final byte[] a = cs.toString().getBytes(StandardCharsets.UTF_16BE);
            return java.util.Arrays.equals(buf, 0, count, a, 0, a.length);
        } else {
            return false;
        }
    }

    /// @see java.util.Arrays#hashCode(byte[])
    /// @see jdk.internal.util.ArraysSupport#hashCode(int, byte[], int, int)
    /// @see java.lang.String#hashCode
    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < count; i++) {
            result = 31 * result + buf[i];
        }
        return result;
    }
}