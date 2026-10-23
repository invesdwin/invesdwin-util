package de.invesdwin.util.streams.buffer.memory;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.norva.beanpath.spi.IUnwrap;
import de.invesdwin.util.collections.array.large.ILargeArray;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * Default ByteOrder is always BigEndian. Use Reverse-Suffixed methods to write/read in LittleEndian. Alternatively use
 * OrderedDelegateByteBuffer to switch the default byte order (though not recommeded).
 */
public interface IMemoryBuffer extends IMemoryBufferProvider, ILargeArray, IUnwrap {

    IMemoryBuffer ensureCapacity(long capacity);

    ByteOrder getOrder();

    boolean isReadOnly();

    long addressOffset();

    long capacity();

    @Override
    default long getBufferLength() {
        return capacity();
    }

    default long remaining(final long index) {
        return capacity() - index;
    }

    long getLong(long index);

    long getLongReverse(long index);

    int getInt(long index);

    /**
     * Reads the int at the given byte offset with volatile semantics. Implementations must provide memory-barrier
     * semantics equivalent to agrona UnsafeApi.getIntVolatile (full fence in both directions). Where an implementation
     * has no native volatile path it must delegate to its plain accessor under a lock so visibility is still
     * guaranteed. The circular ring's epoch tag torn-read self-check depends on these semantics.
     */
    int getIntVolatile(long index);

    /**
     * Volatile read of the byte at the given offset - same barrier contract as getIntVolatile(long), agrona
     * UnsafeApi.getByteVolatile equivalent.
     */
    byte getByteVolatile(long index);

    /**
     * Volatile read of the char at the given offset - same barrier contract as getIntVolatile(long), agrona
     * UnsafeApi.getCharVolatile equivalent.
     */
    char getCharVolatile(long index);

    /**
     * Volatile read of the short at the given offset - same barrier contract as getIntVolatile(long), agrona
     * UnsafeApi.getShortVolatile equivalent.
     */
    short getShortVolatile(long index);

    /**
     * Volatile read of the long at the given offset - same barrier contract as getIntVolatile(long), agrona
     * UnsafeApi.getLongVolatile equivalent.
     */
    long getLongVolatile(long index);

    /**
     * Volatile read of the float at the given offset - same barrier contract as getIntVolatile(long). agrona has no
     * volatile float, so this is implemented as Float.intBitsToFloat over the volatile int (the Raw bits variants
     * are used for writes to preserve NaN payloads).
     */
    float getFloatVolatile(long index);

    /**
     * Volatile read of the double at the given offset - same barrier contract as getLongVolatile(long). agrona has no
     * volatile double, so this is implemented as Double.longBitsToDouble over the volatile long (the Raw bits
     * variants are used for writes to preserve NaN payloads).
     */
    double getDoubleVolatile(long index);

    /**
     * Acquire-semantics read of the int at the given offset (agrona UnsafeApi.getIntAcquire equivalent) - pairs with
     * putIntRelease/putIntOrdered.
     */
    int getIntAcquire(long index);

    /**
     * Acquire-semantics read of the long at the given offset (agrona UnsafeApi.getLongAcquire equivalent) - pairs
     * with putLongRelease/putLongOrdered.
     */
    long getLongAcquire(long index);

    /**
     * Little-endian twin of getCharVolatile(long): the char is read under the same full-fence volatile barrier
     * contract as getIntVolatile(long) and the bytes of the loaded value are swapped afterwards - reverseBytes over
     * the barrier-protected native-order read. The barrier guards the single atomic access; the swap is a value
     * level step applied to the loaded value, never a byte-wise reassembly of the buffer. Every Reverse twin in
     * this block shares this barrier-then-swap contract.
     */
    char getCharVolatileReverse(long index);

    /**
     * Little-endian twin of getShortVolatile(long) - same barrier-then-swap contract as
     * getCharVolatileReverse(long).
     */
    short getShortVolatileReverse(long index);

    /**
     * Little-endian twin of getIntVolatile(long) - same barrier-then-swap contract as getCharVolatileReverse(long).
     */
    int getIntVolatileReverse(long index);

    /**
     * Little-endian twin of getLongVolatile(long) - same barrier-then-swap contract as getCharVolatileReverse(long).
     */
    long getLongVolatileReverse(long index);

    /**
     * Little-endian twin of getFloatVolatile(long): Float.intBitsToFloat over the swapped raw bits of
     * getIntVolatileReverse(long), so the swap rides the same volatile-int barrier chain as the non-reverse variant
     * and the Raw bits path preserves NaN payloads.
     */
    float getFloatVolatileReverse(long index);

    /**
     * Little-endian twin of getDoubleVolatile(long): Double.longBitsToDouble over the swapped raw bits of
     * getLongVolatileReverse(long), so the swap rides the same volatile-long barrier chain as the non-reverse
     * variant and the Raw bits path preserves NaN payloads.
     */
    double getDoubleVolatileReverse(long index);

    /**
     * Little-endian twin of getIntAcquire(long): acquire-semantics native-order read (agrona UnsafeApi.getIntAcquire
     * equivalent) followed by a value level swap - same barrier-then-swap contract as
     * getCharVolatileReverse(long). Pairs with putIntReleaseReverse/putIntOrderedReverse.
     */
    int getIntAcquireReverse(long index);

    /**
     * Little-endian twin of getLongAcquire(long): acquire-semantics native-order read (agrona
     * UnsafeApi.getLongAcquire equivalent) followed by a value level swap - same barrier-then-swap contract as
     * getCharVolatileReverse(long). Pairs with putLongReleaseReverse/putLongOrderedReverse.
     */
    long getLongAcquireReverse(long index);

    int getIntReverse(long index);

    double getDouble(long index);

    double getDoubleReverse(long index);

    float getFloat(long index);

    float getFloatReverse(long index);

    short getShort(long index);

    short getShortReverse(long index);

    char getChar(long index);

    char getCharReverse(long index);

    default boolean getBoolean(final long index) {
        return getByte(index) > 0;
    }

    byte getByte(long index);

    default void getBytes(final long index, final byte[] dst) {
        getBytesTo(index, dst, dst.length);
    }

    default void getBytesFrom(final long index, final byte[] dst, final int dstIndex) {
        getBytes(index, dst, dstIndex, dst.length - dstIndex);
    }

    default void getBytesTo(final long index, final byte[] dst, final int length) {
        getBytes(index, dst, 0, length);
    }

    void getBytes(long index, byte[] dst, int dstIndex, int length);

    default void getBytes(final long index, final MutableDirectBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }

    default void getBytesFrom(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstIndex, dstBuffer.capacity() - dstIndex);
    }

    default void getBytesTo(final long index, final MutableDirectBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }

    void getBytes(long index, MutableDirectBuffer dstBuffer, int dstIndex, int length);

    default void getBytes(final long index, final IByteBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }

    default void getBytesFrom(final long index, final IByteBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstIndex, dstBuffer.capacity() - dstIndex);
    }

    default void getBytesTo(final long index, final IByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }

    void getBytes(long index, IByteBuffer dstBuffer, int dstIndex, int length);

    default void getBytes(final long index, final IMemoryBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }

    default void getBytesFrom(final long index, final IMemoryBuffer dstBuffer, final long dstIndex) {
        getBytes(index, dstBuffer, dstIndex, dstBuffer.capacity() - dstIndex);
    }

    default void getBytesTo(final long index, final IMemoryBuffer dstBuffer, final long length) {
        getBytes(index, dstBuffer, 0, length);
    }

    void getBytes(long index, IMemoryBuffer dstBuffer, long dstIndex, long length);

    default void getBytes(final long index, final java.nio.ByteBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.remaining());
    }

    default void getBytesFrom(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstBuffer.position(), dstIndex);
    }

    default void getBytesTo(final long index, final java.nio.ByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, dstBuffer.position(), length);
    }

    void getBytes(long index, java.nio.ByteBuffer dstBuffer, int dstIndex, int length);

    long wrapAdjustment();

    boolean isExpandable();

    void putLong(long index, long value);

    void putLongReverse(long index, long value);

    void putInt(long index, int value);

    /**
     * Writes the int at the given byte offset with volatile semantics. Implementations must provide memory-barrier
     * semantics equivalent to agrona UnsafeApi.putIntVolatile (full fence in both directions). Where an
     * implementation has no native volatile path it must delegate to its plain accessor under a lock so visibility is
     * still guaranteed. The circular ring's epoch tag torn-read self-check depends on these semantics.
     */
    void putIntVolatile(long index, int value);

    /**
     * Volatile write of the byte at the given offset - same barrier contract as putIntVolatile(long, int), agrona
     * UnsafeApi.putByteVolatile equivalent.
     */
    void putByteVolatile(long index, byte value);

    /**
     * Volatile write of the char at the given offset - same barrier contract as putIntVolatile(long, int), agrona
     * UnsafeApi.putCharVolatile equivalent.
     */
    void putCharVolatile(long index, char value);

    /**
     * Volatile write of the short at the given offset - same barrier contract as putIntVolatile(long, int), agrona
     * UnsafeApi.putShortVolatile equivalent.
     */
    void putShortVolatile(long index, short value);

    /**
     * Volatile write of the long at the given offset - same barrier contract as putIntVolatile(long, int), agrona
     * UnsafeApi.putLongVolatile equivalent.
     */
    void putLongVolatile(long index, long value);

    /**
     * Volatile write of the float at the given offset - same barrier contract as putIntVolatile(long, int). agrona has
     * no volatile float, so this is implemented via UnsafeApi.putIntVolatile over Float.floatToRawIntBits (the Raw
     * variant preserves NaN payloads).
     */
    void putFloatVolatile(long index, float value);

    /**
     * Volatile write of the double at the given offset - same barrier contract as putLongVolatile(long, long). agrona
     * has no volatile double, so this is implemented via UnsafeApi.putLongVolatile over Double.doubleToRawLongBits
     * (the Raw variant preserves NaN payloads).
     */
    void putDoubleVolatile(long index, double value);

    /**
     * Release-semantics write of the int at the given offset (agrona UnsafeApi.putIntRelease equivalent) - pairs with
     * getIntAcquire.
     */
    void putIntRelease(long index, int value);

    /**
     * Ordered-semantics write of the int at the given offset (agrona putIntOrdered which maps to putIntRelease) -
     * pairs with getIntAcquire.
     */
    void putIntOrdered(long index, int value);

    /**
     * Release-semantics write of the long at the given offset (agrona UnsafeApi.putLongRelease equivalent) - pairs
     * with getLongAcquire.
     */
    void putLongRelease(long index, long value);

    /**
     * Ordered-semantics write of the long at the given offset (agrona putLongOrdered which maps to putLongRelease) -
     * pairs with getLongAcquire.
     */
    void putLongOrdered(long index, long value);

    /**
     * Little-endian twin of putCharVolatile(long, char): the value bytes are swapped first and the swapped value is
     * written through the same full-fence volatile barrier contract as putIntVolatile(long, int) - the barrier
     * guards the single atomic write of the whole value, never a byte-wise assembly. Where a class's plain
     * putCharReverse inverts the swap under the ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED configuration, this twin
     * must apply the same inversion. Every Reverse twin in this block shares this swap-then-barrier contract.
     */
    void putCharVolatileReverse(long index, char value);

    /**
     * Little-endian twin of putShortVolatile(long, short) - same swap-then-barrier contract as
     * putCharVolatileReverse(long, char).
     */
    void putShortVolatileReverse(long index, short value);

    /**
     * Little-endian twin of putIntVolatile(long, int) - same swap-then-barrier contract as
     * putCharVolatileReverse(long, char).
     */
    void putIntVolatileReverse(long index, int value);

    /**
     * Little-endian twin of putLongVolatile(long, long) - same swap-then-barrier contract as
     * putCharVolatileReverse(long, char).
     */
    void putLongVolatileReverse(long index, long value);

    /**
     * Little-endian twin of putFloatVolatile(long, float): Float.floatToRawIntBits(value) swapped on the int level
     * and written through the volatile-int barrier chain - the Raw bits path preserves NaN payloads.
     */
    void putFloatVolatileReverse(long index, float value);

    /**
     * Little-endian twin of putDoubleVolatile(long, double): Double.doubleToRawLongBits(value) swapped on the long
     * level and written through the volatile-long barrier chain - the Raw bits path preserves NaN payloads.
     */
    void putDoubleVolatileReverse(long index, double value);

    /**
     * Little-endian twin of putIntRelease(long, int): release-semantics write (agrona UnsafeApi.putIntRelease
     * equivalent) of the value-swapped int - same swap-then-barrier contract as putCharVolatileReverse(long, char).
     * Pairs with getIntAcquireReverse(long).
     */
    void putIntReleaseReverse(long index, int value);

    /**
     * Little-endian twin of putLongRelease(long, long): release-semantics write (agrona UnsafeApi.putLongRelease
     * equivalent) of the value-swapped long - same swap-then-barrier contract as
     * putCharVolatileReverse(long, char). Pairs with getLongAcquireReverse(long).
     */
    void putLongReleaseReverse(long index, long value);

    /**
     * Little-endian twin of putIntOrdered(long, int) (agrona putIntOrdered which maps to putIntRelease) - same
     * swap-then-barrier contract as putIntReleaseReverse(long, int); pairs with getIntAcquireReverse(long).
     */
    void putIntOrderedReverse(long index, int value);

    /**
     * Little-endian twin of putLongOrdered(long, long) (agrona putLongOrdered which maps to putLongRelease) - same
     * swap-then-barrier contract as putLongReleaseReverse(long, long); pairs with getLongAcquireReverse(long).
     */
    void putLongOrderedReverse(long index, long value);

    void putIntReverse(long index, int value);

    void putDouble(long index, double value);

    void putDoubleReverse(long index, double value);

    void putFloat(long index, float value);

    void putFloatReverse(long index, float value);

    void putShort(long index, short value);

    void putShortReverse(long index, short value);

    void putChar(long index, char value);

    void putCharReverse(long index, char value);

    default void putBoolean(final long index, final boolean value) {
        putByte(index, Bytes.checkedCast(value));
    }

    void putByte(long index, byte value);

    default void putBytes(final long index, final byte[] src) {
        putBytesTo(index, src, src.length);
    }

    default void putBytesFrom(final long index, final byte[] src, final int srcIndex) {
        putBytes(index, src, srcIndex, src.length - srcIndex);
    }

    default void putBytesTo(final long index, final byte[] src, final int length) {
        putBytes(index, src, 0, length);
    }

    void putBytes(long index, byte[] src, int srcIndex, int length);

    default void putBytes(final long index, final java.nio.ByteBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final long index, final java.nio.ByteBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(long index, java.nio.ByteBuffer srcBuffer, int srcIndex, int length);

    default void putBytes(final long index, final DirectBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final long index, final DirectBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final long index, final DirectBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(long index, DirectBuffer srcBuffer, int srcIndex, int length);

    default void putBytes(final long index, final IByteBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final long index, final IByteBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final long index, final IByteBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(long index, IByteBuffer srcBuffer, int srcIndex, int length);

    default void putBytes(final long index, final IMemoryBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }

    default void putBytesFrom(final long index, final IMemoryBuffer srcBuffer, final long srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }

    default void putBytesTo(final long index, final IMemoryBuffer srcBuffer, final long length) {
        putBytes(index, srcBuffer, 0, length);
    }

    void putBytes(long index, IMemoryBuffer srcBuffer, long srcIndex, long length);

    default InputStream asInputStream() {
        return asInputStreamTo(capacity());
    }

    default InputStream asInputStreamFrom(final long index) {
        return asInputStream(index, remaining(index));
    }

    default InputStream asInputStreamTo(final long length) {
        return asInputStream(0, length);
    }

    InputStream asInputStream(long index, long length);

    default OutputStream asOutputStream() {
        return asOutputStreamTo(capacity());
    }

    default OutputStream asOutputStreamFrom(final long index) {
        return asOutputStream(index, remaining(index));
    }

    default OutputStream asOutputStreamTo(final long length) {
        return asOutputStream(0, length);
    }

    OutputStream asOutputStream(long index, long length);

    /**
     * Might return the actual underlying array. Thus make sure to clone() it if the buffer is to be reused. Or just use
     * asByteArrayCopy instead to make sure a copy is returned always and clone() is not used redundantly.
     */
    java.nio.ByteBuffer asNioByteBuffer(long index, int length);

    /**
     * Always returns a new copy as a byte array regardless of the underlying storage.
     */
    byte[] asByteArrayCopy(long index, int length);

    /**
     * Either returns the underlying array or copies the underlying storage into an array. Note that changes to the
     * array might or might not be reflected in the underlying storage.
     */
    MutableDirectBuffer asDirectBuffer(long index, int length);

    /**
     * Either returns the underlying array or copies the underlying storage into an array. Note that changes to the
     * array might or might not be reflected in the underlying storage.
     */
    IByteBuffer asByteBuffer(long index, int length);

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    IMemoryBuffer sliceFrom(long index);

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    default IMemoryBuffer sliceTo(final long length) {
        return slice(0, length);
    }

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    IMemoryBuffer slice(long index, long length);

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    IMemoryBuffer newSliceFrom(long index);

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    default IMemoryBuffer newSliceTo(final long length) {
        return newSlice(0, length);
    }

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    IMemoryBuffer newSlice(long index, long length);

    /**
     * This is more efficient than getStringUtf8(...) because it creates less garbage. Thout only works together with
     * putStringAscii(...).
     * 
     * WARNING: we have to keep the tripe-i naming to prevent a name-clash with Agrona.
     */
    String getStringAsciii(long index, int length);

    /**
     * Ascii strings can be directly appended to a StringBuilder for even more efficiency.
     */
    void getStringAsciii(long index, int length, Appendable dst);

    default void putStringAsciii(final long index, final CharSequence value) {
        putStringAsciiiTo(index, value, ByteBuffers.newStringAsciiLength(value));
    }

    default void putStringAsciiiFrom(final long index, final CharSequence value, final int valueIndex) {
        putStringAsciii(index, value, valueIndex, ByteBuffers.newStringAsciiLength(value) - valueIndex);
    }

    default void putStringAsciiiTo(final long index, final CharSequence value, final int length) {
        putStringAsciii(index, value, 0, length);
    }

    /**
     * This is more efficient than putStringUtf8(...) but replaces non ascii characters with '?'.
     */
    void putStringAsciii(long index, CharSequence value, int valueIndex, int length);

    int putStringUtf8(long index, String value);

    String getStringUtf8(long index, int length);

    void getStringUtf8(long index, int length, Appendable dst);

    default void getBytes(final long index, final DataOutputStream dst) throws IOException {
        getBytesTo(index, dst, capacity());
    }

    default void getBytesTo(final long index, final DataOutputStream dst, final long length) throws IOException {
        getBytesTo(index, (OutputStream) dst, length);
    }

    default void getBytes(final long index, final DataOutput dst) throws IOException {
        getBytesTo(index, dst, capacity());
    }

    void getBytesTo(long index, DataOutput dst, long length) throws IOException;

    default void getBytes(final long index, final OutputStream dst) throws IOException {
        getBytesTo(index, dst, remaining(index));
    }

    void getBytesTo(long index, OutputStream dst, long length) throws IOException;

    default void getBytes(final long index, final WritableByteChannel dst) throws IOException {
        getBytesTo(index, dst, remaining(index));
    }

    void getBytesTo(long index, WritableByteChannel dst, long length) throws IOException;

    default void putBytes(final long index, final DataInputStream src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    default void putBytesTo(final long index, final DataInputStream src, final long length) throws IOException {
        putBytesTo(index, (InputStream) src, length);
    }

    default void putBytes(final long index, final DataInput src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    void putBytesTo(long index, DataInput src, long length) throws IOException;

    default void putBytes(final long index, final InputStream src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    void putBytesTo(long index, InputStream src, long length) throws IOException;

    default void putBytes(final long index, final ReadableByteChannel src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }

    void putBytesTo(long index, ReadableByteChannel src, long length) throws IOException;

    @Override
    default long getBuffer(final IMemoryBuffer dst) {
        final long length = capacity();
        getBytesTo(0, dst, length);
        return length;
    }

    @Override
    default IMemoryBuffer asBuffer() {
        return this;
    }

    @Override
    default void clear() {
        clear(Bytes.ZERO);
    }

    default void clearFrom(final long index) {
        clear(Bytes.ZERO, index, remaining(index));
    }

    default void clearTo(final long length) {
        clear(Bytes.ZERO, 0, length);
    }

    default void clear(final long index, final long length) {
        clear(Bytes.ZERO, index, length);
    }

    default void clear(final byte value) {
        clear(value, 0, capacity());
    }

    default void clearFrom(final byte value, final long index) {
        clear(value, index, remaining(index));
    }

    default void clearTo(final byte value, final long length) {
        clear(value, 0, length);
    }

    void clear(byte value, long index, long length);

    IMemoryBuffer clone(long index, int length);

    @Override
    default long size() {
        return capacity();
    }

    @Override
    default boolean isEmpty() {
        return capacity() == 0;
    }

    IMemoryBuffer asImmutableSlice();
}
