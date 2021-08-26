package de.invesdwin.util.lang.buffer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.annotation.concurrent.Immutable;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Charsets;
import de.invesdwin.util.lang.buffer.delegate.AgronaDelegateByteBuffer;
import de.invesdwin.util.lang.buffer.delegate.JavaDelegateByteBuffer;
import de.invesdwin.util.lang.buffer.extend.ExpandableArrayByteBuffer;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Bytes;

@Immutable
public final class ByteBuffers {

    public static final int EXAPANDABLE_LENGTH = -1;

    public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);
    public static final AtomicBuffer EMPTY_DIRECT_BUFFER = new UnsafeBuffer(Bytes.EMPTY_ARRAY);

    /**
     * ByteBuffer uses BigEndian per default.
     */
    public static final ByteOrder DEFAULT_ORDER = ByteOrder.BIG_ENDIAN;
    /**
     * What does the system actually use?
     */
    public static final ByteOrder NATIVE_ORDER = ByteOrder.nativeOrder();

    private static final ISliceInvoker SLICE_INVOKER;
    private static final FastEOFException PUTBYTESTOEOF = new FastEOFException("putBytesTo: src.read() returned -1");

    static {
        SLICE_INVOKER = newSliceInvoker();
    }

    private ByteBuffers() {
    }

    private static ISliceInvoker newSliceInvoker() {
        try {
            //java >= 13
            final Method sliceMethod = Reflections.findMethod(ByteBuffer.class, "slice", int.class, int.class);
            final MethodHandle sliceInvoker = MethodHandles.lookup().unreflect(sliceMethod);
            return (buffer, position, length) -> {
                try {
                    return (ByteBuffer) sliceInvoker.invoke(buffer, position, length);
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (final Throwable e) {
            //java < 13
            return (buffer, position, length) -> {
                final ByteBuffer duplicate = buffer.duplicate();
                position(duplicate, position);
                duplicate.limit(position + length);
                return duplicate.slice();
            };
        }
    }

    @FunctionalInterface
    private interface ISliceInvoker {
        ByteBuffer slice(ByteBuffer buffer, int position, int length);
    }

    /**
     * Workaround for java 8 compiled on java 9 or higher
     */
    public static void position(final Buffer buffer, final int position) {
        buffer.position(position);
    }

    public static ByteBuffer slice(final ByteBuffer buffer, final int position, final int length) {
        return SLICE_INVOKER.slice(buffer, position, length);
    }

    public static void get(final ByteBuffer buffer, final int position, final byte[] dst) {
        final int positionBefore = buffer.position();
        buffer.get(dst);
        position(buffer, positionBefore);
    }

    public static byte[] getRemaining(final ByteBuffer buffer, final int position) {
        final int positionBefore = buffer.position();
        position(buffer, position);
        final byte[] dst = new byte[buffer.remaining()];
        buffer.get(dst);
        position(buffer, positionBefore);
        return dst;
    }

    public static byte[] get(final ByteBuffer buffer, final int position, final int size) {
        final int positionBefore = buffer.position();
        position(buffer, position);
        final byte[] dst = new byte[size];
        buffer.get(dst);
        position(buffer, positionBefore);
        return dst;
    }

    public static void get(final ByteBuffer buffer, final int index, final byte[] dst, final int dstIndex,
            final int length) {
        final int positionBefore = buffer.position();
        position(buffer, index);
        buffer.get(dst, dstIndex, length);
        position(buffer, positionBefore);
    }

    public static void put(final ByteBuffer buffer, final int index, final byte[] src, final int srcIndex,
            final int length) {
        final int positionBefore = buffer.position();
        position(buffer, index);
        buffer.put(src, srcIndex, length);
        position(buffer, positionBefore);
    }

    public static void put(final ByteBuffer buffer, final int index, final byte[] bytes) {
        put(buffer, index, bytes, 0, bytes.length);
    }

    public static IByteBuffer allocate(final Integer fixedLength) {
        if (fixedLength == null) {
            return allocateExpandable();
        } else {
            return allocate(fixedLength.intValue());
        }
    }

    public static IByteBuffer allocate(final int fixedLength) {
        if (fixedLength == 0) {
            return EmptyByteBuffer.INSTANCE;
        } else if (fixedLength < 0) {
            return allocateExpandable();
        } else {
            return allocateFixed(fixedLength);
        }
    }

    public static IByteBuffer allocateFixed(final int fixedLength) {
        return wrap(new byte[fixedLength]);
    }

    public static IByteBuffer allocateExpandable() {
        return new ExpandableArrayByteBuffer();
    }

    public static IByteBuffer allocateNonZero(final int fixedLength) {
        if (fixedLength == 0) {
            throw new IllegalArgumentException("fixedLength should not be 0");
        } else if (fixedLength < 0) {
            return allocateExpandable();
        } else {
            return allocateFixed(fixedLength);
        }
    }

    public static IByteBuffer wrap(final byte[] bytes) {
        return new JavaDelegateByteBuffer(bytes);
    }

    public static IByteBuffer wrap(final ByteBuffer buffer) {
        return new JavaDelegateByteBuffer(buffer);
    }

    public static IByteBuffer wrap(final MutableDirectBuffer buffer) {
        return new AgronaDelegateByteBuffer(buffer);
    }

    /**
     * Allocate a buffer for this encoded size and use putStringAscii(string) afterwards.
     */
    public static int newStringAsciiLength(final CharSequence value) {
        if (value == null) {
            return 0;
        }
        return value.length();
    }

    /**
     * Allocate a buffer for the encoded size and use putBytes(stringBytes) afterwards.
     */
    public static byte[] newStringAsciiBytes(final CharSequence value) {
        if (value == null || value.length() == 0) {
            return Bytes.EMPTY_ARRAY;
        }
        final byte[] bytes = new byte[newStringAsciiLength(value)];
        final int length = value.length();
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            bytes[i] = (byte) c;
        }
        return bytes;
    }

    /**
     * Allocate a buffer for the encoded size and use putBytes(stringBytes) afterwards.
     */
    public static byte[] newStringUtf8Bytes(final CharSequence value) {
        if (value == null || value.length() == 0) {
            return Bytes.EMPTY_ARRAY;
        }
        //this variation causes additional zero bytes to be added at the end -.-
        //dunno how to get the real size and don't want to iterator until a non-zero byte is found at the end.
        //also trimming would require a second array copy, which should be equivalent to a value.toString()
        //return Charsets.UTF_8.encode(CharBuffer.wrap(value)).array();
        return value.toString().getBytes(Charsets.UTF_8);
    }

    /**
     * Allocate a buffer for the encoded size and use putBytes(stringBytes) afterwards.
     */
    public static byte[] newStringUtf8Bytes(final String value) {
        if (value == null || value.length() == 0) {
            return Bytes.EMPTY_ARRAY;
        }
        return value.getBytes(Charsets.UTF_8);
    }

    public static String newStringUtf8(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return new String(bytes, Charsets.UTF_8);
    }

    public static String newStringAscii(final byte[] bytes) {
        //actually the same
        return newStringUtf8(bytes);
    }

    public static FastEOFException newPutBytesToEOF() {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new FastEOFException(PUTBYTESTOEOF.getMessage());
        } else {
            return PUTBYTESTOEOF;
        }
    }

}
