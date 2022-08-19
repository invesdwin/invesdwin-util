package de.invesdwin.util.streams.buffer.bytes;

import java.io.DataInput;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;

import javax.annotation.concurrent.Immutable;

import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.concurrent.pool.AgronaObjectPool;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.lang.Charsets;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.buffer.bytes.delegate.AgronaDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.AgronaDelegateMutableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.ArrayExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeArrayByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.internal.DirectExpandableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.internal.UninitializedDirectByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.internal.UninitializedDirectExpandableByteBuffer;

@Immutable
public final class ByteBuffers {

    public static final int MAX_TO_STRING_COUNT = 1024;

    public static final int EXPANDABLE_LENGTH = -1;

    /**
     * ByteBuffer uses BigEndian per default. BigEndian also seems faster somehow.
     */
    public static final ByteOrder DEFAULT_ORDER = ByteOrder.BIG_ENDIAN;

    public static final boolean BIG_ENDIAN_NATIVE_ORDER = io.netty.util.internal.PlatformDependent.BIG_ENDIAN_NATIVE_ORDER;
    public static final boolean LITTLE_ENDIAN_NATIVE_ORDER = !BIG_ENDIAN_NATIVE_ORDER;

    public static final boolean BIG_ENDIAN_REVERSAL_NEEDED = !BIG_ENDIAN_NATIVE_ORDER;
    public static final boolean LITTLE_ENDIAN_REVERSAL_NEEDED = !LITTLE_ENDIAN_NATIVE_ORDER;

    /**
     * What does the system actually use?
     */
    public static final ByteOrder NATIVE_ORDER = ByteOrder.nativeOrder();

    public static final IObjectPool<IByteBuffer> EXPANDABLE_POOL = new AgronaObjectPool<IByteBuffer>(
            () -> allocateExpandable());
    public static final IObjectPool<IByteBuffer> DIRECT_EXPANDABLE_POOL = new AgronaObjectPool<IByteBuffer>(
            () -> allocateDirectExpandable());

    private static final ISliceInvoker SLICE_INVOKER;

    static {
        SLICE_INVOKER = newSliceInvoker();
    }

    private ByteBuffers() {}

    private static ISliceInvoker newSliceInvoker() {
        try {
            //java >= 13
            final Method sliceMethod = Reflections.findMethod(java.nio.ByteBuffer.class, "slice", int.class, int.class);
            final MethodHandle sliceInvoker = MethodHandles.lookup().unreflect(sliceMethod);
            return (buffer, position, length) -> {
                try {
                    return (java.nio.ByteBuffer) sliceInvoker.invoke(buffer, position, length);
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (final Throwable e) {
            //java < 13
            return (buffer, position, length) -> {
                final java.nio.ByteBuffer duplicate = buffer.duplicate();
                position(duplicate, position);
                duplicate.limit(position + length);
                return duplicate.slice();
            };
        }
    }

    @FunctionalInterface
    private interface ISliceInvoker {
        java.nio.ByteBuffer slice(java.nio.ByteBuffer buffer, int position, int length);
    }

    /**
     * Workaround for java 8 compiled on java 9 or higher
     */
    public static void position(final Buffer buffer, final int position) {
        buffer.position(position);
    }

    public static java.nio.ByteBuffer slice(final java.nio.ByteBuffer buffer, final int position, final int length) {
        return SLICE_INVOKER.slice(buffer, position, length);
    }

    public static void get(final java.nio.ByteBuffer buffer, final int position, final byte[] dst) {
        final int positionBefore = buffer.position();
        buffer.get(dst);
        position(buffer, positionBefore);
    }

    public static byte[] getRemaining(final java.nio.ByteBuffer buffer, final int position) {
        final int positionBefore = buffer.position();
        position(buffer, position);
        final byte[] dst = allocateByteArray(buffer.remaining());
        buffer.get(dst);
        position(buffer, positionBefore);
        return dst;
    }

    public static byte[] get(final java.nio.ByteBuffer buffer, final int position, final int size) {
        final int positionBefore = buffer.position();
        position(buffer, position);
        final byte[] dst = allocateByteArray(size);
        buffer.get(dst);
        position(buffer, positionBefore);
        return dst;
    }

    public static void get(final java.nio.ByteBuffer buffer, final int index, final byte[] dst, final int dstIndex,
            final int length) {
        final int positionBefore = buffer.position();
        position(buffer, index);
        buffer.get(dst, dstIndex, length);
        position(buffer, positionBefore);
    }

    public static void put(final java.nio.ByteBuffer buffer, final int index, final byte[] src, final int srcIndex,
            final int length) {
        final int positionBefore = buffer.position();
        position(buffer, index);
        buffer.put(src, srcIndex, length);
        position(buffer, positionBefore);
    }

    public static void put(final java.nio.ByteBuffer buffer, final int index, final byte[] bytes) {
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
        return wrap(allocateByteArray(fixedLength));
    }

    /**
     * We skip zeroing with this implementation. Requires JVM args: --add-opens java.base/jdk.internal.misc=ALL-UNNAMED
     * -Dio.netty.tryReflectionSetAccessible=true
     * 
     * https://shipilev.net/jvm/anatomy-quarks/7-initialization-costs/
     * 
     * https://netty.io/wiki/using-as-a-generic-library.html#performance
     */
    public static byte[] allocateByteArray(final int fixedLength) {
        if (fixedLength == 0) {
            return Bytes.EMPTY_ARRAY;
        }
        return io.netty.util.internal.PlatformDependent.allocateUninitializedArray(fixedLength);
    }

    public static IByteBuffer allocateExpandable() {
        return new ArrayExpandableByteBuffer();
    }

    public static IByteBuffer allocateExpandable(final int initialLength) {
        return new ArrayExpandableByteBuffer(initialLength);
    }

    public static IByteBuffer allocateDirect(final Integer fixedLength) {
        if (fixedLength == null) {
            return allocateDirectExpandable();
        } else {
            return allocateDirect(fixedLength.intValue());
        }
    }

    public static IByteBuffer allocateDirect(final int fixedLength) {
        if (fixedLength == 0) {
            return EmptyByteBuffer.INSTANCE;
        } else if (fixedLength < 0) {
            return allocateDirectExpandable();
        } else {
            return allocateDirectFixed(fixedLength);
        }
    }

    public static IByteBuffer allocateDirectFixed(final int fixedLength) {
        if (UninitializedDirectByteBuffers.isDirectByteBufferNoCleanerSupported()) {
            return new UninitializedDirectByteBuffer(fixedLength);
        } else {
            //no need to register a second cleaner
            return wrap(java.nio.ByteBuffer.allocateDirect(fixedLength));
        }
    }

    public static IByteBuffer allocateDirectExpandable() {
        if (UninitializedDirectByteBuffers.isDirectByteBufferNoCleanerSupported()) {
            return new UninitializedDirectExpandableByteBuffer();
        } else {
            return new DirectExpandableByteBuffer();
        }
    }

    public static IByteBuffer allocateDirectExpandable(final int initialLength) {
        if (UninitializedDirectByteBuffers.isDirectByteBufferNoCleanerSupported()) {
            return new UninitializedDirectExpandableByteBuffer(initialLength);
        } else {
            return new DirectExpandableByteBuffer(initialLength);
        }
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
        final byte[] bytes = allocateByteArray(newStringAsciiLength(value));
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
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        //tell it to treat each byte separately
        return new String(bytes, Charsets.US_ASCII);
    }

    public static FastEOFException newPutBytesToEOF() {
        return FastEOFException.getInstance("putBytesTo: src.read() returned -1");
    }

    public static byte[] asByteArrayCopyGet(final IByteBuffer buffer, final int index, final int length) {
        final byte[] bytes = allocateByteArray(length);
        buffer.getBytes(index, bytes, 0, length);
        return bytes;
    }

    public static byte[] asByteArrayCopyGet(final java.nio.ByteBuffer buffer, final int index, final int length) {
        final byte[] bytes = allocateByteArray(length);
        get(buffer, index, bytes, 0, length);
        return bytes;
    }

    public static long addressOffset(final java.nio.ByteBuffer buffer) {
        if (buffer.isDirect()) {
            return BufferUtil.address(buffer);
        } else {
            return BufferUtil.ARRAY_BASE_OFFSET + BufferUtil.arrayOffset(buffer);
        }
    }

    public static int wrapAdjustment(final java.nio.ByteBuffer buffer) {
        final long offset = buffer.hasArray() ? BufferUtil.ARRAY_BASE_OFFSET : BufferUtil.address(buffer);
        return (int) (addressOffset(buffer) - offset);
    }

    public static IByteBuffer wrap(final long address, final int length) {
        return new UnsafeByteBuffer(address, length);
    }

    public static IByteBuffer wrap(final DirectBuffer buffer) {
        /*
         * We do not check if the instance might be a IByteBuffer already since we can not risk to share the mutable
         * slices between threads. So we don't unwrap that here.
         */
        return new AgronaDelegateByteBuffer(buffer);
    }

    public static IByteBuffer wrap(final MutableDirectBuffer buffer) {
        /*
         * We do not check if the instance might be a IByteBuffer already since we can not risk to share the mutable
         * slices between threads. So we don't unwrap that here.
         */
        return new AgronaDelegateMutableByteBuffer(buffer);
    }

    public static IByteBuffer wrap(final byte[] bytes) {
        return new UnsafeArrayByteBuffer(bytes);
    }

    public static IByteBuffer wrapFrom(final byte[] bytes, final int index) {
        return wrap(bytes, index, bytes.length - index);
    }

    public static IByteBuffer wrapTo(final byte[] bytes, final int length) {
        return wrap(bytes, 0, length);
    }

    public static IByteBuffer wrap(final byte[] bytes, final int index, final int length) {
        if (index == 0 && length == bytes.length) {
            return wrap(bytes);
        } else {
            return new UnsafeArrayByteBuffer(bytes, index, length);
        }
    }

    public static IByteBuffer wrap(final java.nio.ByteBuffer buffer) {
        if (buffer.hasArray() && wrapAdjustment(buffer) == 0) {
            return wrapTo(buffer.array(), buffer.limit());
        } else {
            return new UnsafeByteBuffer(buffer);
        }
    }

    public static IByteBuffer wrapFrom(final java.nio.ByteBuffer buffer, final int index) {
        return wrap(buffer, index, buffer.capacity() - index);
    }

    public static IByteBuffer wrapTo(final java.nio.ByteBuffer buffer, final int length) {
        return wrap(buffer, 0, length);
    }

    public static IByteBuffer wrap(final java.nio.ByteBuffer buffer, final int index, final int length) {
        if (index == 0 && length == buffer.capacity()) {
            return wrap(buffer);
        } else {
            return new UnsafeByteBuffer(buffer, index, length);
        }
    }

    public static IByteBuffer wrapRelative(final java.nio.ByteBuffer buffer) {
        final int position = buffer.position();
        final int limit = buffer.limit();
        if (buffer.hasArray() && wrapAdjustment(buffer) == 0) {
            return wrap(buffer.array(), position, limit - position);
        } else {
            return new UnsafeByteBuffer(buffer, position, limit - position);
        }
    }

    public static IByteBuffer wrapRelativeFrom(final java.nio.ByteBuffer buffer, final int index) {
        return wrapRelative(buffer, index, buffer.limit() - buffer.position() - index);
    }

    public static IByteBuffer wrapRelativeTo(final java.nio.ByteBuffer buffer, final int length) {
        return wrapRelative(buffer, 0, length);
    }

    public static IByteBuffer wrapRelative(final java.nio.ByteBuffer buffer, final int index, final int length) {
        if (index == 0 && length == (buffer.limit() - buffer.position())) {
            return wrapRelative(buffer);
        } else {
            return new UnsafeByteBuffer(buffer, index, length);
        }
    }

    public static IByteBuffer wrapFrom(final DirectBuffer buffer, final int index) {
        return wrap(buffer, index, buffer.capacity() - index);
    }

    public static IByteBuffer wrapTo(final DirectBuffer buffer, final int length) {
        return wrap(buffer, 0, length);
    }

    public static IByteBuffer wrap(final DirectBuffer buffer, final int index, final int length) {
        if (index == 0 && length == buffer.capacity()) {
            return wrap(buffer);
        } else {
            return new UnsafeByteBuffer(buffer, index, length);
        }
    }

    public static int readExpandable(final ReadableByteChannel src, final IByteBuffer buffer, final int index)
            throws IOException {
        int location = index;
        while (true) {
            final int count = src.read(buffer.asNioByteBufferFrom(location));
            if (count == -1) { // EOF
                break;
            } else {
                location += count;
                final int capacity = buffer.capacity();
                if (location == capacity) {
                    buffer.ensureCapacity(capacity + count);
                }
            }
        }
        return location - index;
    }

    public static int readExpandable(final DataInput src, final IByteBuffer buffer, final int index)
            throws IOException {
        if (src instanceof ReadableByteChannel) {
            return readExpandable((ReadableByteChannel) src, buffer, index);
        } else {
            int location = index;
            while (true) {
                final int count = InputStreams.read(src, buffer.byteArray(), location, buffer.remaining(location));
                if (count == -1) { // EOF
                    break;
                } else {
                    location += count;
                    final int capacity = buffer.capacity();
                    if (location == capacity) {
                        buffer.ensureCapacity(capacity + count);
                    }
                }
            }
            return location - index;
        }
    }

    public static int readExpandable(final InputStream src, final IByteBuffer buffer, final int index)
            throws IOException {
        if (src instanceof ReadableByteChannel) {
            return readExpandable((ReadableByteChannel) src, buffer, index);
        } else if (src instanceof FileInputStream) {
            final FileInputStream cSrc = (FileInputStream) src;
            return readExpandable(cSrc.getChannel(), buffer, index);
        } else if (src instanceof DataInput) {
            return readExpandable((DataInput) src, buffer, index);
        } else {
            int location = index;
            while (true) {
                final int count = src.read(buffer.byteArray(), location, buffer.remaining(location));
                if (count == -1) { // EOF
                    break;
                } else {
                    location += count;
                    final int capacity = buffer.capacity();
                    if (location == capacity) {
                        buffer.ensureCapacity(capacity + count);
                    }
                }
            }
            return location - index;
        }
    }

    public static int newAllocateFixedLength(final Integer fixedLength) {
        if (fixedLength == null) {
            return EXPANDABLE_LENGTH;
        } else {
            return fixedLength.intValue();
        }
    }

    public static Integer newFixedLength(final int fixedLength) {
        if (fixedLength < 0) {
            return null;
        } else {
            return fixedLength;
        }
    }

    public static String toString(final java.nio.ByteBuffer nioBuffer) {
        return toString(wrap(nioBuffer));
    }

    public static String toStringRelative(final java.nio.ByteBuffer nioBuffer) {
        return toString(wrapRelative(nioBuffer));
    }

    public static String toString(final IByteBuffer buffer) {
        final byte[] byteArray = buffer.asByteArray(0, Integers.min(MAX_TO_STRING_COUNT, buffer.capacity()));
        return Objects.toStringHelper(buffer)
                .add("addressOffset", buffer.addressOffset())
                .add("capacity", buffer.capacity())
                .add("wrapAdjustment", buffer.wrapAdjustment())
                .with(Arrays.toString(byteArray))
                .toString();
    }

    public static void ensureCapacity(final IByteBuffer buffer, final int desiredCapacity) {
        final int capacity = buffer.capacity();
        if (desiredCapacity > capacity) {
            throw new IndexOutOfBoundsException(
                    "desiredCapacity=" + desiredCapacity + " is beyond capacity=" + capacity);
        }
    }

    public static long assertBuffer(final long address) {
        if (address <= 0) {
            throw new NullPointerException("address [" + address + "] should be positive (this can cause a jvm crash)");
        }
        return address;
    }

    public static <T> T assertBuffer(final T buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer should not be null (this can cause a jvm crash)");
        }
        return buffer;
    }

    public static byte[] asByteArray(final IByteBuffer buffer, final int index, final int length) {
        final byte[] bytes = buffer.byteArray();
        if (bytes != null) {
            final int wrapAdjustment = buffer.wrapAdjustment();
            if (wrapAdjustment != 0 || index != 0 || length != bytes.length) {
                return ByteBuffers.copy(bytes, wrapAdjustment + index, length);
            } else {
                return bytes;
            }
        }
        final java.nio.ByteBuffer byteBuffer = buffer.nioByteBuffer();
        if (byteBuffer != null && byteBuffer.hasArray()) {
            final byte[] array = byteBuffer.array();
            if (array != null) {
                final int wrapAdjustment = buffer.wrapAdjustment();
                if (wrapAdjustment != 0 || index != 0 || length != array.length) {
                    return ByteBuffers.copy(array, wrapAdjustment + index, length);
                } else {
                    return array;
                }
            }
        }
        return ByteBuffers.asByteArrayCopyGet(buffer, index, length);
    }

    public static byte[] asByteArrayCopy(final IByteBuffer buffer, final int index, final int length) {
        final byte[] bytes = buffer.byteArray();
        if (bytes != null) {
            final int wrapAdjustment = buffer.wrapAdjustment();
            if (wrapAdjustment != 0 || index != 0 || length != bytes.length) {
                return ByteBuffers.copy(bytes, wrapAdjustment + index, length);
            } else {
                return bytes.clone();
            }
        }
        final java.nio.ByteBuffer byteBuffer = buffer.nioByteBuffer();
        if (byteBuffer != null && byteBuffer.hasArray()) {
            final byte[] array = byteBuffer.array();
            if (array != null) {
                final int wrapAdjustment = buffer.wrapAdjustment();
                if (wrapAdjustment != 0 || index != 0 || length != array.length) {
                    return ByteBuffers.copy(array, wrapAdjustment + index, length);
                } else {
                    return array.clone();
                }
            }
        }
        return ByteBuffers.asByteArrayCopyGet(buffer, index, length);
    }

    public static int calculateExpansion(final int requestedSize) {
        return 1 << (32 - Integer.numberOfLeadingZeros(requestedSize - 1));
    }

    public static byte[] copyOf(final byte[] original, final int newLength) {
        final byte[] copy = allocateByteArray(newLength);
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static byte[] copy(final byte[] original, final int from, final int newLength) {
        final byte[] copy = allocateByteArray(newLength);
        System.arraycopy(original, from, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    /**
     * https://security.stackexchange.com/a/77433
     * 
     * https://codahale.com/a-lesson-in-timing-attacks/
     * 
     * MessageDigest.isEqual is fixed since Java 6u17. It also does not leak information about the correct length. So an
     * invalid equals check takes the same time as a valid equals check.
     */
    public static boolean constantTimeEquals(final byte[] a, final byte[] b) {
        return MessageDigest.isEqual(a, b);
    }

    public static boolean constantTimeEquals(final IByteBuffer digesta, final byte[] digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final int lenA = digesta.capacity();
        final int lenB = digestb.length;

        if (lenB == 0) {
            return lenA == 0;
        }

        int result = 0;
        result |= lenA - lenB;

        // time-constant comparison
        for (int i = 0; i < lenA; i++) {
            // If i >= lenB, indexB is 0; otherwise, i.
            final int indexB = ((i - lenB) >>> 31) * i;
            result |= digesta.getByte(i) ^ digestb[indexB];
        }
        return result == 0;
    }

    public static boolean constantTimeEquals(final IByteBuffer digesta, final byte[] digestb, final int digestbOffset,
            final int digestbLength) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final int lenA = digesta.capacity();
        final int lenB = digestbLength;

        if (lenB == 0) {
            return lenA == 0;
        }

        int result = 0;
        result |= lenA - lenB;

        // time-constant comparison
        for (int i = 0; i < lenA; i++) {
            // If i >= lenB, indexB is 0; otherwise, i.
            final int indexB = digestbOffset + ((i - lenB) >>> 31) * i;
            result |= digesta.getByte(i) ^ digestb[indexB];
        }
        return result == 0;
    }

    public static boolean constantTimeEquals(final byte[] digesta, final IByteBuffer digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final int lenA = digesta.length;
        final int lenB = digestb.capacity();

        if (lenB == 0) {
            return lenA == 0;
        }

        int result = 0;
        result |= lenA - lenB;

        // time-constant comparison
        for (int i = 0; i < lenA; i++) {
            // If i >= lenB, indexB is 0; otherwise, i.
            final int indexB = ((i - lenB) >>> 31) * i;
            result |= digesta[i] ^ digestb.getByte(indexB);
        }
        return result == 0;
    }

    public static boolean constantTimeEquals(final IByteBuffer digesta, final IByteBuffer digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final int lenA = digesta.capacity();
        final int lenB = digestb.capacity();

        if (lenB == 0) {
            return lenA == 0;
        }

        int result = 0;
        result |= lenA - lenB;

        // time-constant comparison
        for (int i = 0; i < lenA; i++) {
            // If i >= lenB, indexB is 0; otherwise, i.
            final int indexB = ((i - lenB) >>> 31) * i;
            result |= digesta.getByte(i) ^ digestb.getByte(indexB);
        }
        return result == 0;
    }

    public static boolean equals(final byte[] digesta, final byte[] digestb) {
        if (digesta == digestb) {
            return true;
        }
        if (digesta == null || digestb == null) {
            return false;
        }

        final int lenA = digesta.length;
        final int lenB = digestb.length;

        if (lenB == 0) {
            return lenA == 0;
        }

        if (lenA != lenB) {
            return false;
        }

        for (int i = 0; i < lenA; i++) {
            if (digesta[i] != digestb[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(final IByteBuffer digesta, final byte[] digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final int lenA = digesta.capacity();
        final int lenB = digestb.length;

        if (lenB == 0) {
            return lenA == 0;
        }

        if (lenA != lenB) {
            return false;
        }

        for (int i = 0; i < lenA; i++) {
            if (digesta.getByte(i) != digestb[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(final byte[] digesta, final IByteBuffer digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final int lenA = digesta.length;
        final int lenB = digestb.capacity();

        if (lenB == 0) {
            return lenA == 0;
        }

        if (lenA != lenB) {
            return false;
        }

        for (int i = 0; i < lenA; i++) {
            if (digesta[i] != digestb.getByte(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(final IByteBuffer digesta, final IByteBuffer digestb) {
        if (digesta == null || digestb == null) {
            return false;
        }

        final int lenA = digesta.capacity();
        final int lenB = digestb.capacity();

        if (lenB == 0) {
            return lenA == 0;
        }

        if (lenA != lenB) {
            return false;
        }

        for (int i = 0; i < lenA; i++) {
            if (digesta.getByte(i) != digestb.getByte(i)) {
                return false;
            }
        }
        return true;
    }

    public static java.nio.ByteBuffer asNioByteBuffer(final IByteBuffer buffer) {
        if (buffer != null) {
            return buffer.asNioByteBuffer();
        } else {
            return null;
        }
    }

}
