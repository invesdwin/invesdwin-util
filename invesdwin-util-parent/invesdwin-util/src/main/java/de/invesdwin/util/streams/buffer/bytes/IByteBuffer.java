package de.invesdwin.util.streams.buffer.bytes;

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
import de.invesdwin.util.collections.array.primitive.IPrimitiveArray;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

/**
 * Default ByteOrder is always BigEndian. Use Reverse-Suffixed methods to write/read in LittleEndian. Alternatively use
 * OrderedDelegateByteBuffer to switch the default byte order (though not recommeded).
 */
public interface IByteBuffer extends IByteBufferProvider, Cloneable, IPrimitiveArray, IUnwrap {

    /**
     * Ensures the buffer has at least the given capacity and returns the buffer to use for further access; on implementations that grow by reallocating their storage the returned buffer replaces this instance.
     */
    IByteBuffer ensureCapacity(int capacity);

    /**
     * Returns the byte order used by the non-reverse accessors - big endian by default.
     */
    ByteOrder getOrder();

    /**
     * Returns true when write accessors are rejected on this buffer.
     */
    boolean isReadOnly();

    /**
     * Returns the memory address offset of the buffer start for address-backed implementations, 0 for pure heap storage.
     */
    long addressOffset();

    /**
     * Returns the underlying agrona MutableDirectBuffer view of the whole buffer.
     */
    MutableDirectBuffer directBuffer();

    /**
     * Returns the underlying backing byte array of the whole buffer - changes to it are reflected in the buffer.
     */
    byte[] byteArray();

    /**
     * Returns a java.nio.ByteBuffer view over the whole buffer's capacity.
     */
    java.nio.ByteBuffer nioByteBuffer();

    /**
     * Returns the current capacity of the buffer in bytes.
     */
    int capacity();

    /**
     * Returns the buffer's byte length - equals capacity() and fulfils the provider interface.
     */
    @Override
    default int getBufferLength() {
        return capacity();
    }

    /**
     * Returns the number of bytes from the given offset to the buffer's capacity.
     */
    default int remaining(final int index) {
        return capacity() - index;
    }






























    /**
     * Reads the byte at the given offset and returns true when it is greater than zero - the plain counterpart of getByteVolatile(int) without the barrier guarantee.
     */
    default boolean getBoolean(final int index) {
        return getByte(index) > 0;
    }




















    /**
     * Returns the wrap adjustment of the underlying implementation - 0 when the buffer has no circular wrap.
     */
    int wrapAdjustment();

    /**
     * Returns true when the implementation can grow on demand via ensureCapacity, false for fixed-size buffers.
     */
    boolean isExpandable();


































    /**
     * Writes the boolean at the given offset as a single byte via putByte (the value is cast by Bytes.checkedCast).
     */
    default void putBoolean(final int index, final boolean value) {
        putByte(index, Bytes.checkedCast(value));
    }




















    /**
     * Returns an InputStream reading the whole buffer from offset 0 for capacity() bytes.
     */
    default InputStream asInputStream() {
        return asInputStreamTo(capacity());
    }

    /**
     * Returns an InputStream reading the remaining bytes from the given offset.
     */
    default InputStream asInputStreamFrom(final int index) {
        return asInputStream(index, remaining(index));
    }

    /**
     * Returns an InputStream reading the first length bytes of the buffer.
     */
    default InputStream asInputStreamTo(final int length) {
        return asInputStream(0, length);
    }

    /**
     * Returns an InputStream reading length bytes starting at the given offset, reflecting the live buffer content.
     */
    InputStream asInputStream(int index, int length);

    /**
     * Returns an OutputStream writing into the whole buffer from offset 0 for capacity() bytes.
     */
    default OutputStream asOutputStream() {
        return asOutputStreamTo(capacity());
    }

    /**
     * Returns an OutputStream writing the remaining bytes starting at the given offset.
     */
    default OutputStream asOutputStreamFrom(final int index) {
        return asOutputStream(index, remaining(index));
    }

    /**
     * Returns an OutputStream writing the first length bytes of the buffer.
     */
    default OutputStream asOutputStreamTo(final int length) {
        return asOutputStream(0, length);
    }

    /**
     * Returns an OutputStream writing length bytes starting at the given offset, writing live into the buffer.
     */
    OutputStream asOutputStream(int index, int length);

    /**
     * Might return the actual underlying array. Thus make sure to clone() it if the buffer is to be reused. Or just use
     * asByteArrayCopy instead to make sure a copy is returned always and clone() is not used redundantly.
     * 
     * WARNING: be aware that expandable buffers might have a larger capacity than was was added to the buffer, thus
     * always prefer to use asByteArrayTo(length) instead of this capacity bounded version. Or make sure to only call
     * this method on buffers that have been slice(from, to)'d since that sets the capacity as a contraint to the
     * underlying actual backing array capacity.
     */
    default byte[] asByteArray() {
        return asByteArrayTo(capacity());
    }

    /**
     * Returns a view of the bytes from the given offset to the end - see asByteArray() for the aliasing caveats.
     */
    default byte[] asByteArrayFrom(final int index) {
        return asByteArray(index, remaining(index));
    }

    /**
     * Returns a view of the first length bytes - see asByteArray() for the aliasing caveats.
     */
    default byte[] asByteArrayTo(final int length) {
        return asByteArray(0, length);
    }

    /**
     * Might return the actual underlying array. Thus make sure to clone() it if the buffer is to be reused. Or just use
     * asByteArrayCopy instead to make sure a copy is returned always and clone() is not used redundantly.
     */
    byte[] asByteArray(int index, int length);

    /**
     * Returns a java.nio.ByteBuffer view from offset 0 for capacity() bytes.
     */
    default java.nio.ByteBuffer asNioByteBuffer() {
        return asNioByteBufferTo(capacity());
    }

    /**
     * Returns a java.nio.ByteBuffer view of the remaining bytes from the given offset.
     */
    default java.nio.ByteBuffer asNioByteBufferFrom(final int index) {
        return asNioByteBuffer(index, remaining(index));
    }

    /**
     * Returns a java.nio.ByteBuffer view of the first length bytes.
     */
    default java.nio.ByteBuffer asNioByteBufferTo(final int length) {
        return asNioByteBuffer(0, length);
    }

    /**
     * Might return the actual underlying array. Thus make sure to clone() it if the buffer is to be reused. Or just use
     * asByteArrayCopy instead to make sure a copy is returned always and clone() is not used redundantly.
     * 
     * Though it is not guaranteed that the returned ByteBuffer will not be a separate copy of this storage (e.g. when
     * ListByteBuffer is used).
     */
    java.nio.ByteBuffer asNioByteBuffer(int index, int length);

    /**
     * Always returns a new copy as a byte array regardless of the underlying storage.
     * 
     * WARNING: be aware that expandable buffers might have a larger capacity than was was added to the buffer, thus
     * always prefer to use asByteArrayTo(length) instead of this capacity bounded version. Or make sure to only call
     * this method on buffers that have been slice(from, to)'d since that sets the capacity as a contraint to the
     * underlying actual backing array capacity.
     */
    default byte[] asByteArrayCopy() {
        return asByteArrayCopyTo(capacity());
    }

    /**
     * Returns a copy of the bytes from the given offset to the end, always freshly allocated.
     */
    default byte[] asByteArrayCopyFrom(final int index) {
        return asByteArrayCopy(index, remaining(index));
    }

    /**
     * Returns a copy of the first length bytes, always freshly allocated.
     */
    default byte[] asByteArrayCopyTo(final int length) {
        return asByteArrayCopy(0, length);
    }

    /**
     * Always returns a new copy as a byte array regardless of the underlying storage.
     */
    byte[] asByteArrayCopy(int index, int length);

    /**
     * Returns a MutableDirectBuffer view of the whole buffer's capacity.
     */
    MutableDirectBuffer asDirectBuffer();

    /**
     * Returns a MutableDirectBuffer view of the remaining bytes from the given offset.
     */
    default MutableDirectBuffer asDirectBufferFrom(final int index) {
        return asDirectBuffer(index, remaining(index));
    }

    /**
     * Returns a MutableDirectBuffer view of the first length bytes.
     */
    default MutableDirectBuffer asDirectBufferTo(final int length) {
        return asDirectBuffer(0, length);
    }

    /**
     * Either returns the underlying array or copies the underlying storage into an array. Note that changes to the
     * array might or might not be reflected in the underlying storage.
     */
    MutableDirectBuffer asDirectBuffer(int index, int length);

    /**
     * Returns an IMemoryBuffer view of the whole buffer's capacity.
     */
    IMemoryBuffer asMemoryBuffer();

    /**
     * Returns an IMemoryBuffer view of the remaining bytes from the given offset.
     */
    default IMemoryBuffer asMemoryBufferFrom(final int index) {
        return asMemoryBuffer(index, remaining(index));
    }

    /**
     * Returns an IMemoryBuffer view of the first length bytes.
     */
    default IMemoryBuffer asMemoryBufferTo(final int length) {
        return asMemoryBuffer(0, length);
    }

    /**
     * Either returns the underlying array or copies the underlying storage into an array. Note that changes to the
     * array might or might not be reflected in the underlying storage.
     */
    IMemoryBuffer asMemoryBuffer(int index, int length);

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    IByteBuffer sliceFrom(int index);

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    default IByteBuffer sliceTo(final int length) {
        return slice(0, length);
    }

    /**
     * WARNING: Slice instances will be reused from each buffer so previous slices will change when invoking this method
     * again. This is fine when using a slice completely, then setting up another slice to use then. If you need
     * multiple separate slices at the same it, it is better to call newSlice... instead.
     */
    IByteBuffer slice(int index, int length);

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    IByteBuffer newSliceFrom(int index);

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    default IByteBuffer newSliceTo(final int length) {
        return newSlice(0, length);
    }

    /**
     * This always creates a new object for the slice, thus slice instances are not reused.
     */
    IByteBuffer newSlice(int index, int length);

    /**
     * This is more efficient than getStringUtf8(...) because it creates less garbage. Thout only works together with
     * putStringAscii(...).
     * 
     * WARNING: we have to keep the tripe-i naming to prevent a name-clash with Agrona.
     */
    String getStringAsciii(int index, int length);

    /**
     * Ascii strings can be directly appended to a StringBuilder for even more efficiency.
     */
    void getStringAsciii(int index, int length, Appendable dst);

    /**
     * Writes the ASCII encoding of value at the given offset using the value's full ASCII length - see putStringAsciii(int, CharSequence, int, int) for the terminal contract.
     */
    default void putStringAsciii(final int index, final CharSequence value) {
        putStringAsciiiTo(index, value, ByteBuffers.newStringAsciiLength(value));
    }

    /**
     * Writes the ASCII encoding of value at the given offset starting at valueIndex within value.
     */
    default void putStringAsciiiFrom(final int index, final CharSequence value, final int valueIndex) {
        putStringAsciii(index, value, valueIndex, ByteBuffers.newStringAsciiLength(value) - valueIndex);
    }

    /**
     * Writes the first length ASCII characters of value at the given offset starting at valueIndex 0.
     */
    default void putStringAsciiiTo(final int index, final CharSequence value, final int length) {
        putStringAsciii(index, value, 0, length);
    }

    /**
     * This is more efficient than putStringUtf8(...) but replaces non ascii characters with '?'.
     */
    void putStringAsciii(int index, CharSequence value, int valueIndex, int length);

    /**
     * Writes value as UTF-8 at the given offset and returns the number of bytes written.
     */
    int putStringUtf8(int index, String value);

    /**
     * Reads length bytes at the given offset decoded as a UTF-8 string.
     */
    String getStringUtf8(int index, int length);

    /**
     * Reads length bytes at the given offset as UTF-8 and appends the result to dst.
     */
    void getStringUtf8(int index, int length, Appendable dst);
















    /**
     * Copies dst.length bytes from the given offset into dst starting at its beginning.
     */
    default void getBytes(final int index, final byte[] dst) {
        getBytesTo(index, dst, dst.length);
    }
    /**
     * Copies bytes from the given offset into dst starting at dstIndex, up to the destination's remaining capacity.
     */
    default void getBytesFrom(final int index, final byte[] dst, final int dstIndex) {
        getBytes(index, dst, dstIndex, dst.length - dstIndex);
    }
    /**
     * Copies length bytes from the given offset into dst starting at its beginning.
     */
    default void getBytesTo(final int index, final byte[] dst, final int length) {
        getBytes(index, dst, 0, length);
    }
    /**
     * Copies length bytes from the given offset into dst starting at dstIndex - the terminal byte transfer of this family.
     */
    void getBytes(int index, byte[] dst, int dstIndex, int length);
    /**
     * Copies dstBuffer.capacity() bytes from the given offset into dstBuffer starting at its beginning.
     */
    default void getBytes(final int index, final MutableDirectBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }
    /**
     * Copies bytes from the given offset into dstBuffer starting at dstIndex, up to the destination's remaining capacity.
     */
    default void getBytesFrom(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstIndex, dstBuffer.capacity() - dstIndex);
    }
    /**
     * Copies length bytes from the given offset into dstBuffer starting at its beginning.
     */
    default void getBytesTo(final int index, final MutableDirectBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }
    /**
     * Copies length bytes from the given offset into dstBuffer starting at dstIndex - the terminal byte transfer of this family.
     */
    void getBytes(int index, MutableDirectBuffer dstBuffer, int dstIndex, int length);
    /**
     * Copies dstBuffer.capacity() bytes from the given offset into dstBuffer starting at its beginning.
     */
    default void getBytes(final int index, final IByteBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.capacity());
    }
    /**
     * Copies bytes from the given offset into dstBuffer starting at dstIndex, up to the destination's remaining capacity.
     */
    default void getBytesFrom(final int index, final IByteBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstIndex, dstBuffer.capacity() - dstIndex);
    }
    /**
     * Copies length bytes from the given offset into dstBuffer starting at its beginning.
     */
    default void getBytesTo(final int index, final IByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }
    /**
     * Copies length bytes from the given offset into dstBuffer starting at dstIndex - the terminal byte transfer of this family.
     */
    void getBytes(int index, IByteBuffer dstBuffer, int dstIndex, int length);
    /**
     * Copies length bytes from the given offset into dstBuffer starting at its beginning.
     */
    default void getBytesTo(final int index, final IMemoryBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, 0, length);
    }
    /**
     * Copies length bytes from the given offset into dstBuffer starting at dstIndex - the terminal byte transfer of this family.
     */
    void getBytes(int index, IMemoryBuffer dstBuffer, long dstIndex, int length);
    /**
     * Copies dstBuffer.remaining() bytes from the given offset into dstBuffer at its current position.
     */
    default void getBytes(final int index, final java.nio.ByteBuffer dstBuffer) {
        getBytesTo(index, dstBuffer, dstBuffer.remaining());
    }
    /**
     * Copies the given dstIndex count of bytes from the given offset into dstBuffer at its current position.
     */
    default void getBytesFrom(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex) {
        getBytes(index, dstBuffer, dstBuffer.position(), dstIndex);
    }
    /**
     * Copies length bytes from the given offset into dstBuffer at its current position.
     */
    default void getBytesTo(final int index, final java.nio.ByteBuffer dstBuffer, final int length) {
        getBytes(index, dstBuffer, dstBuffer.position(), length);
    }
    /**
     * Copies length bytes from the given offset into dstBuffer starting at dstIndex - the terminal byte transfer of this family.
     */
    void getBytes(int index, java.nio.ByteBuffer dstBuffer, int dstIndex, int length);
    /**
     * Writes src.length bytes at the given offset from the start of src.
     */
    default void putBytes(final int index, final byte[] src) {
        putBytesTo(index, src, src.length);
    }
    /**
     * Writes bytes at the given offset from srcIndex to the end of src.
     */
    default void putBytesFrom(final int index, final byte[] src, final int srcIndex) {
        putBytes(index, src, srcIndex, src.length - srcIndex);
    }
    /**
     * Writes length bytes at the given offset from the start of src.
     */
    default void putBytesTo(final int index, final byte[] src, final int length) {
        putBytes(index, src, 0, length);
    }
    /**
     * Writes length bytes at the given offset from src starting at srcIndex - the terminal byte transfer of this family.
     */
    void putBytes(int index, byte[] src, int srcIndex, int length);
    /**
     * Writes srcBuffer.capacity() bytes at the given offset from the start of srcBuffer.
     */
    default void putBytes(final int index, final java.nio.ByteBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }
    /**
     * Writes bytes at the given offset from srcIndex to the end of srcBuffer.
     */
    default void putBytesFrom(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }
    /**
     * Writes length bytes at the given offset from the start of srcBuffer.
     */
    default void putBytesTo(final int index, final java.nio.ByteBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }
    /**
     * Writes length bytes at the given offset from srcBuffer starting at srcIndex - the terminal byte transfer of this family.
     */
    void putBytes(int index, java.nio.ByteBuffer srcBuffer, int srcIndex, int length);
    /**
     * Writes srcBuffer.capacity() bytes at the given offset from the start of srcBuffer.
     */
    default void putBytes(final int index, final DirectBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }
    /**
     * Writes bytes at the given offset from srcIndex to the end of srcBuffer.
     */
    default void putBytesFrom(final int index, final DirectBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }
    /**
     * Writes length bytes at the given offset from the start of srcBuffer.
     */
    default void putBytesTo(final int index, final DirectBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }
    /**
     * Writes length bytes at the given offset from srcBuffer starting at srcIndex - the terminal byte transfer of this family.
     */
    void putBytes(int index, DirectBuffer srcBuffer, int srcIndex, int length);
    /**
     * Writes srcBuffer.capacity() bytes at the given offset from the start of srcBuffer.
     */
    default void putBytes(final int index, final IByteBuffer srcBuffer) {
        putBytesTo(index, srcBuffer, srcBuffer.capacity());
    }
    /**
     * Writes bytes at the given offset from srcIndex to the end of srcBuffer.
     */
    default void putBytesFrom(final int index, final IByteBuffer srcBuffer, final int srcIndex) {
        putBytes(index, srcBuffer, srcIndex, srcBuffer.capacity() - srcIndex);
    }
    /**
     * Writes length bytes at the given offset from the start of srcBuffer.
     */
    default void putBytesTo(final int index, final IByteBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }
    /**
     * Writes length bytes at the given offset from srcBuffer starting at srcIndex - the terminal byte transfer of this family.
     */
    void putBytes(int index, IByteBuffer srcBuffer, int srcIndex, int length);
    /**
     * Writes length bytes at the given offset from the start of srcBuffer.
     */
    default void putBytesTo(final int index, final IMemoryBuffer srcBuffer, final int length) {
        putBytes(index, srcBuffer, 0, length);
    }
    /**
     * Writes length bytes at the given offset from srcBuffer starting at srcIndex - the terminal byte transfer of this family.
     */
    void putBytes(int index, IMemoryBuffer srcBuffer, long srcIndex, int length);
    /**
     * Writes up to capacity() bytes from the given offset to dst.
     */
    default void getBytes(final int index, final DataOutputStream dst) throws IOException {
        getBytesTo(index, dst, capacity());
    }
    /**
     * Writes length bytes from the given offset to dst - the terminal streaming copy of this family.
     */
    default void getBytesTo(final int index, final DataOutputStream dst, final int length) throws IOException {
        getBytesTo(index, (OutputStream) dst, length);
    }
    /**
     * Writes up to capacity() bytes from the given offset to dst.
     */
    default void getBytes(final int index, final DataOutput dst) throws IOException {
        getBytesTo(index, dst, capacity());
    }
    /**
     * Writes length bytes from the given offset to dst - the terminal streaming copy of this family.
     */
    void getBytesTo(int index, DataOutput dst, int length) throws IOException;
    /**
     * Writes the remaining bytes from the given offset to dst.
     */
    default void getBytes(final int index, final OutputStream dst) throws IOException {
        getBytesTo(index, dst, remaining(index));
    }
    /**
     * Writes length bytes from the given offset to dst - the terminal streaming copy of this family.
     */
    void getBytesTo(int index, OutputStream dst, int length) throws IOException;
    /**
     * Writes the remaining bytes from the given offset to dst.
     */
    default void getBytes(final int index, final WritableByteChannel dst) throws IOException {
        getBytesTo(index, dst, remaining(index));
    }
    /**
     * Writes length bytes from the given offset to dst - the terminal streaming copy of this family.
     */
    void getBytesTo(int index, WritableByteChannel dst, int length) throws IOException;
    /**
     * Reads up to remaining(index) bytes from src into the buffer at the given offset.
     */
    default void putBytes(final int index, final DataInputStream src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }
    /**
     * Reads length bytes from src into the buffer at the given offset - the terminal streaming copy of this family.
     */
    default void putBytesTo(final int index, final DataInputStream src, final int length) throws IOException {
        putBytesTo(index, (InputStream) src, length);
    }
    /**
     * Reads up to remaining(index) bytes from src into the buffer at the given offset.
     */
    default void putBytes(final int index, final DataInput src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }
    /**
     * Reads length bytes from src into the buffer at the given offset - the terminal streaming copy of this family.
     */
    void putBytesTo(int index, DataInput src, int length) throws IOException;
    /**
     * Reads up to remaining(index) bytes from src into the buffer at the given offset.
     */
    default void putBytes(final int index, final InputStream src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }
    /**
     * Reads length bytes from src into the buffer at the given offset - the terminal streaming copy of this family.
     */
    void putBytesTo(int index, InputStream src, int length) throws IOException;
    /**
     * Reads up to remaining(index) bytes from src into the buffer at the given offset.
     */
    default void putBytes(final int index, final ReadableByteChannel src) throws IOException {
        putBytesTo(index, src, remaining(index));
    }
    /**
     * Reads length bytes from src into the buffer at the given offset - the terminal streaming copy of this family.
     */
    void putBytesTo(int index, ReadableByteChannel src, int length) throws IOException;
    /**
     * Reads the byte at the given offset - the plain counterpart of getByteVolatile(int) without the memory-barrier guarantee; a single byte has no byte order.
     */
    byte getByte(int index);
    /**
     * Volatile read of the byte at the given offset - same barrier contract as getIntVolatile(int), agrona
     * UnsafeApi.getByteVolatile equivalent.
     */
    byte getByteVolatile(int index);
    /**
     * Writes the byte at the given offset - the plain counterpart of putByteVolatile(int, byte) without the memory-barrier guarantee.
     */
    void putByte(int index, byte value);
    /**
     * Volatile write of the byte at the given offset - same barrier contract as putIntVolatile(int, int), agrona
     * UnsafeApi.putByteVolatile equivalent.
     */
    void putByteVolatile(int index, byte value);
    /**
     * Reads the char at the given offset using the default byte order (big endian) - the plain counterpart of getCharVolatile(int) without the memory-barrier guarantee and of getCharReverse(int) without the byte swap.
     */
    char getChar(int index);
    /**
     * Reads the char at the given offset with the loaded value's bytes swapped (little endian against the default big endian order) - the plain counterpart of getCharVolatileReverse(int) without the memory-barrier guarantee; see getChar(int) for the unswapped read.
     */
    char getCharReverse(int index);
    /**
     * Volatile read of the char at the given offset - same barrier contract as getIntVolatile(int), agrona
     * UnsafeApi.getCharVolatile equivalent.
     */
    char getCharVolatile(int index);
    /**
     * Little-endian twin of getCharVolatile(int): the char is read under the same full-fence volatile barrier
     * contract as getIntVolatile(int) and the bytes of the loaded value are swapped afterwards - reverseBytes over
     * the barrier-protected native-order read. The barrier guards the single atomic access; the swap is a value
     * level step applied to the loaded value, never a byte-wise reassembly of the buffer. Every Reverse twin in
     * this block shares this barrier-then-swap contract.
     */
    char getCharVolatileReverse(int index);
    /**
     * Writes the char at the given offset using the default byte order (big endian) - the plain counterpart of putCharVolatile(int, char) without the memory-barrier guarantee and of putCharReverse(int, char) without the byte swap.
     */
    void putChar(int index, char value);
    /**
     * Writes the char at the given offset with the value's bytes swapped before the write (little endian against the default big endian order) - the plain counterpart of putCharVolatileReverse(int, char) without the memory-barrier guarantee; see putChar(int, char) for the unswapped write.
     */
    void putCharReverse(int index, char value);
    /**
     * Volatile write of the char at the given offset - same barrier contract as putIntVolatile(int, int), agrona
     * UnsafeApi.putCharVolatile equivalent.
     */
    void putCharVolatile(int index, char value);
    /**
     * Little-endian twin of putCharVolatile(int, char): the value bytes are swapped first and the swapped value is
     * written through the same full-fence volatile barrier contract as putIntVolatile(int, int) - the barrier
     * guards the single atomic write of the whole value, never a byte-wise assembly. Where a class's plain
     * putCharReverse inverts the swap under the ByteBuffers.BIG_ENDIAN_REVERSAL_NEEDED configuration, this twin
     * must apply the same inversion. Every Reverse twin in this block shares this swap-then-barrier contract.
     */
    void putCharVolatileReverse(int index, char value);
    /**
     * Reads the short at the given offset using the default byte order (big endian) - the plain counterpart of getShortVolatile(int) without the memory-barrier guarantee and of getShortReverse(int) without the byte swap.
     */
    short getShort(int index);
    /**
     * Reads the short at the given offset with the loaded value's bytes swapped (little endian against the default big endian order) - the plain counterpart of getShortVolatileReverse(int) without the memory-barrier guarantee; see getShort(int) for the unswapped read.
     */
    short getShortReverse(int index);
    /**
     * Volatile read of the short at the given offset - same barrier contract as getIntVolatile(int), agrona
     * UnsafeApi.getShortVolatile equivalent.
     */
    short getShortVolatile(int index);
    /**
     * Little-endian twin of getShortVolatile(int) - same barrier-then-swap contract as
     * getCharVolatileReverse(int).
     */
    short getShortVolatileReverse(int index);
    /**
     * Writes the short at the given offset using the default byte order (big endian) - the plain counterpart of putShortVolatile(int, short) without the memory-barrier guarantee and of putShortReverse(int, short) without the byte swap.
     */
    void putShort(int index, short value);
    /**
     * Writes the short at the given offset with the value's bytes swapped before the write (little endian against the default big endian order) - the plain counterpart of putShortVolatileReverse(int, short) without the memory-barrier guarantee; see putShort(int, short) for the unswapped write.
     */
    void putShortReverse(int index, short value);
    /**
     * Volatile write of the short at the given offset - same barrier contract as putIntVolatile(int, int), agrona
     * UnsafeApi.putShortVolatile equivalent.
     */
    void putShortVolatile(int index, short value);
    /**
     * Little-endian twin of putShortVolatile(int, short) - same swap-then-barrier contract as
     * putCharVolatileReverse(int, char).
     */
    void putShortVolatileReverse(int index, short value);
    /**
     * Reads the int at the given offset using the default byte order (big endian) - the plain counterpart of getIntVolatile(int) without the memory-barrier guarantee and of getIntReverse(int) without the byte swap.
     */
    int getInt(int index);
    /**
     * Reads the int at the given offset with the loaded value's bytes swapped (little endian against the default big endian order) - the plain counterpart of getIntVolatileReverse(int) without the memory-barrier guarantee; see getInt(int) for the unswapped read.
     */
    int getIntReverse(int index);
    /**
     * Reads the int at the given byte offset with volatile semantics. Implementations must provide memory-barrier
     * semantics equivalent to agrona UnsafeApi.getIntVolatile (full fence in both directions). Where an implementation
     * has no native volatile path it must delegate to its plain accessor under a lock so visibility is still
     * guaranteed. The circular ring's epoch tag torn-read self-check depends on these semantics.
     */
    int getIntVolatile(int index);
    /**
     * Little-endian twin of getIntVolatile(int) - same barrier-then-swap contract as getCharVolatileReverse(int).
     */
    int getIntVolatileReverse(int index);
    /**
     * Acquire-semantics read of the int at the given offset (agrona UnsafeApi.getIntAcquire equivalent) - pairs with
     * putIntRelease/putIntOrdered.
     */
    int getIntAcquire(int index);
    /**
     * Little-endian twin of getIntAcquire(int): acquire-semantics native-order read (agrona UnsafeApi.getIntAcquire
     * equivalent) followed by a value level swap - same barrier-then-swap contract as
     * getCharVolatileReverse(int). Pairs with putIntReleaseReverse/putIntOrderedReverse.
     */
    int getIntAcquireReverse(int index);
    /**
     * Writes the int at the given offset using the default byte order (big endian) - the plain counterpart of putIntVolatile(int, int) without the memory-barrier guarantee and of putIntReverse(int, int) without the byte swap.
     */
    void putInt(int index, int value);
    /**
     * Writes the int at the given offset with the value's bytes swapped before the write (little endian against the default big endian order) - the plain counterpart of putIntVolatileReverse(int, int) without the memory-barrier guarantee; see putInt(int, int) for the unswapped write.
     */
    void putIntReverse(int index, int value);
    /**
     * Writes the int at the given byte offset with volatile semantics. Implementations must provide memory-barrier
     * semantics equivalent to agrona UnsafeApi.putIntVolatile (full fence in both directions). Where an
     * implementation has no native volatile path it must delegate to its plain accessor under a lock so visibility is
     * still guaranteed. The circular ring's epoch tag torn-read self-check depends on these semantics.
     */
    void putIntVolatile(int index, int value);
    /**
     * Little-endian twin of putIntVolatile(int, int) - same swap-then-barrier contract as
     * putCharVolatileReverse(int, char).
     */
    void putIntVolatileReverse(int index, int value);
    /**
     * Release-semantics write of the int at the given offset (agrona UnsafeApi.putIntRelease equivalent) - pairs with
     * getIntAcquire.
     */
    void putIntRelease(int index, int value);
    /**
     * Little-endian twin of putIntRelease(int, int): release-semantics write (agrona UnsafeApi.putIntRelease
     * equivalent) of the value-swapped int - same swap-then-barrier contract as putCharVolatileReverse(int, char).
     * Pairs with getIntAcquireReverse(int).
     */
    void putIntReleaseReverse(int index, int value);
    /**
     * Ordered-semantics write of the int at the given offset (agrona putIntOrdered which maps to putIntRelease) -
     * pairs with getIntAcquire.
     */
    void putIntOrdered(int index, int value);
    /**
     * Little-endian twin of putIntOrdered(int, int) (agrona putIntOrdered which maps to putIntRelease) - same
     * swap-then-barrier contract as putIntReleaseReverse(int, int); pairs with getIntAcquireReverse(int).
     */
    void putIntOrderedReverse(int index, int value);
    /**
     * Reads the long at the given offset using the default byte order (big endian) - the plain counterpart of getLongVolatile(int) without the memory-barrier guarantee and of getLongReverse(int) without the byte swap.
     */
    long getLong(int index);
    /**
     * Reads the long at the given offset with the loaded value's bytes swapped (little endian against the default big endian order) - the plain counterpart of getLongVolatileReverse(int) without the memory-barrier guarantee; see getLong(int) for the unswapped read.
     */
    long getLongReverse(int index);
    /**
     * Volatile read of the long at the given offset - same barrier contract as getIntVolatile(int), agrona
     * UnsafeApi.getLongVolatile equivalent.
     */
    long getLongVolatile(int index);
    /**
     * Little-endian twin of getLongVolatile(int) - same barrier-then-swap contract as getCharVolatileReverse(int).
     */
    long getLongVolatileReverse(int index);
    /**
     * Acquire-semantics read of the long at the given offset (agrona UnsafeApi.getLongAcquire equivalent) - pairs
     * with putLongRelease/putLongOrdered.
     */
    long getLongAcquire(int index);
    /**
     * Little-endian twin of getLongAcquire(int): acquire-semantics native-order read (agrona
     * UnsafeApi.getLongAcquire equivalent) followed by a value level swap - same barrier-then-swap contract as
     * getCharVolatileReverse(int). Pairs with putLongReleaseReverse/putLongOrderedReverse.
     */
    long getLongAcquireReverse(int index);
    /**
     * Writes the long at the given offset using the default byte order (big endian) - the plain counterpart of putLongVolatile(int, long) without the memory-barrier guarantee and of putLongReverse(int, long) without the byte swap.
     */
    void putLong(int index, long value);
    /**
     * Writes the long at the given offset with the value's bytes swapped before the write (little endian against the default big endian order) - the plain counterpart of putLongVolatileReverse(int, long) without the memory-barrier guarantee; see putLong(int, long) for the unswapped write.
     */
    void putLongReverse(int index, long value);
    /**
     * Volatile write of the long at the given offset - same barrier contract as putIntVolatile(int, int), agrona
     * UnsafeApi.putLongVolatile equivalent.
     */
    void putLongVolatile(int index, long value);
    /**
     * Little-endian twin of putLongVolatile(int, long) - same swap-then-barrier contract as
     * putCharVolatileReverse(int, char).
     */
    void putLongVolatileReverse(int index, long value);
    /**
     * Release-semantics write of the long at the given offset (agrona UnsafeApi.putLongRelease equivalent) - pairs
     * with getLongAcquire.
     */
    void putLongRelease(int index, long value);
    /**
     * Little-endian twin of putLongRelease(int, long): release-semantics write (agrona UnsafeApi.putLongRelease
     * equivalent) of the value-swapped long - same swap-then-barrier contract as
     * putCharVolatileReverse(int, char). Pairs with getLongAcquireReverse(int).
     */
    void putLongReleaseReverse(int index, long value);
    /**
     * Ordered-semantics write of the long at the given offset (agrona putLongOrdered which maps to putLongRelease) -
     * pairs with getLongAcquire.
     */
    void putLongOrdered(int index, long value);
    /**
     * Little-endian twin of putLongOrdered(int, long) (agrona putLongOrdered which maps to putLongRelease) - same
     * swap-then-barrier contract as putLongReleaseReverse(int, long); pairs with getLongAcquireReverse(int).
     */
    void putLongOrderedReverse(int index, long value);
    /**
     * Reads the float at the given offset using the default byte order (big endian) - the plain counterpart of getFloatVolatile(int) without the memory-barrier guarantee and of getFloatReverse(int) without the byte swap.
     */
    float getFloat(int index);
    /**
     * Reads the float at the given offset with the loaded value's bytes swapped (little endian against the default big endian order) - the plain counterpart of getFloatVolatileReverse(int) without the memory-barrier guarantee; see getFloat(int) for the unswapped read.
     */
    float getFloatReverse(int index);
    /**
     * Volatile read of the float at the given offset - same barrier contract as getIntVolatile(int). agrona has no
     * volatile float, so this is implemented as Float.intBitsToFloat over the volatile int (the Raw bits variants
     * are used for writes to preserve NaN payloads).
     */
    float getFloatVolatile(int index);
    /**
     * Little-endian twin of getFloatVolatile(int): Float.intBitsToFloat over the swapped raw bits of
     * getIntVolatileReverse(int), so the swap rides the same volatile-int barrier chain as the non-reverse variant
     * and the Raw bits path preserves NaN payloads.
     */
    float getFloatVolatileReverse(int index);
    /**
     * Writes the float at the given offset using the default byte order (big endian) - the plain counterpart of putFloatVolatile(int, float) without the memory-barrier guarantee and of putFloatReverse(int, float) without the byte swap.
     */
    void putFloat(int index, float value);
    /**
     * Writes the float at the given offset with the value's bytes swapped before the write (little endian against the default big endian order) - the plain counterpart of putFloatVolatileReverse(int, float) without the memory-barrier guarantee; see putFloat(int, float) for the unswapped write.
     */
    void putFloatReverse(int index, float value);
    /**
     * Volatile write of the float at the given offset - same barrier contract as putIntVolatile(int, int). agrona has
     * no volatile float, so this is implemented via UnsafeApi.putIntVolatile over Float.floatToRawIntBits (the Raw
     * variant preserves NaN payloads).
     */
    void putFloatVolatile(int index, float value);
    /**
     * Little-endian twin of putFloatVolatile(int, float): Float.floatToRawIntBits(value) swapped on the int level
     * and written through the volatile-int barrier chain - the Raw bits path preserves NaN payloads.
     */
    void putFloatVolatileReverse(int index, float value);
    /**
     * Reads the double at the given offset using the default byte order (big endian) - the plain counterpart of getDoubleVolatile(int) without the memory-barrier guarantee and of getDoubleReverse(int) without the byte swap.
     */
    double getDouble(int index);
    /**
     * Reads the double at the given offset with the loaded value's bytes swapped (little endian against the default big endian order) - the plain counterpart of getDoubleVolatileReverse(int) without the memory-barrier guarantee; see getDouble(int) for the unswapped read.
     */
    double getDoubleReverse(int index);
    /**
     * Volatile read of the double at the given offset - same barrier contract as getLongVolatile(int). agrona has no
     * volatile double, so this is implemented as Double.longBitsToDouble over the volatile long (the Raw bits
     * variants are used for writes to preserve NaN payloads).
     */
    double getDoubleVolatile(int index);
    /**
     * Little-endian twin of getDoubleVolatile(int): Double.longBitsToDouble over the swapped raw bits of
     * getLongVolatileReverse(int), so the swap rides the same volatile-long barrier chain as the non-reverse
     * variant and the Raw bits path preserves NaN payloads.
     */
    double getDoubleVolatileReverse(int index);
    /**
     * Writes the double at the given offset using the default byte order (big endian) - the plain counterpart of putDoubleVolatile(int, double) without the memory-barrier guarantee and of putDoubleReverse(int, double) without the byte swap.
     */
    void putDouble(int index, double value);
    /**
     * Writes the double at the given offset with the value's bytes swapped before the write (little endian against the default big endian order) - the plain counterpart of putDoubleVolatileReverse(int, double) without the memory-barrier guarantee; see putDouble(int, double) for the unswapped write.
     */
    void putDoubleReverse(int index, double value);
    /**
     * Volatile write of the double at the given offset - same barrier contract as putLongVolatile(int, long). agrona
     * has no volatile double, so this is implemented via UnsafeApi.putLongVolatile over Double.doubleToRawLongBits
     * (the Raw variant preserves NaN payloads).
     */
    void putDoubleVolatile(int index, double value);
    /**
     * Little-endian twin of putDoubleVolatile(int, double): Double.doubleToRawLongBits(value) swapped on the long
     * level and written through the volatile-long barrier chain - the Raw bits path preserves NaN payloads.
     */
    void putDoubleVolatileReverse(int index, double value);

    /**
     * Copies the whole buffer into dst starting at offset 0 of dst and returns the copied length.
     */
    @Override
    default int getBuffer(final IByteBuffer dst) {
        final int length = capacity();
        getBytesTo(0, dst, length);
        return length;
    }

    /**
     * Returns this instance as its provider interface view.
     */
    @Override
    default IByteBuffer asBuffer() {
        return this;
    }

    /**
     * Returns a detached copy of the whole buffer as a new independent buffer.
     */
    IByteBuffer clone();

    /**
     * Returns a detached copy of the bytes from the given offset to the end.
     */
    default IByteBuffer cloneFrom(final int index) {
        return clone(index, remaining(index));
    }

    /**
     * Returns a detached copy of the first length bytes.
     */
    default IByteBuffer cloneTo(final int length) {
        return clone(0, length);
    }

    /**
     * Returns a detached copy of the given region as a new independent buffer.
     */
    IByteBuffer clone(int index, int length);

    /**
     * Fills the whole buffer with zeros.
     */
    @Override
    default void clear() {
        clear(Bytes.ZERO);
    }

    /**
     * Fills the buffer with zeros from the given offset to the end.
     */
    default void clearFrom(final int index) {
        clear(Bytes.ZERO, index, remaining(index));
    }

    /**
     * Fills the buffer with zeros from offset 0 for length bytes.
     */
    default void clearTo(final int length) {
        clear(Bytes.ZERO, 0, length);
    }

    /**
     * Fills the buffer with zeros from the given offset for length bytes.
     */
    default void clear(final int index, final int length) {
        clear(Bytes.ZERO, index, length);
    }

    /**
     * Fills the whole buffer with the given byte value.
     */
    default void clear(final byte value) {
        clear(value, 0, capacity());
    }

    /**
     * Fills the buffer with the given value from the given offset to the end.
     */
    default void clearFrom(final byte value, final int index) {
        clear(value, index, remaining(index));
    }

    /**
     * Fills the buffer with the given value from offset 0 for length bytes.
     */
    default void clearTo(final byte value, final int length) {
        clear(value, 0, length);
    }

    /**
     * Fills the buffer with the given byte value from the given offset for length bytes - the terminal fill of this family.
     */
    void clear(byte value, int index, int length);

    /**
     * Fills the whole buffer with pseudo-random bytes from the given generator.
     */
    default void clear(final IRandomGenerator random) {
        clear(random, 0, capacity());
    }

    /**
     * Fills the buffer with pseudo-random bytes from the given generator starting at the given offset.
     */
    default void clearFrom(final IRandomGenerator random, final int index) {
        clear(random, index, remaining(index));
    }

    /**
     * Fills the buffer with pseudo-random bytes from the given generator from offset 0 for length bytes.
     */
    default void clearTo(final IRandomGenerator random, final int length) {
        clear(random, 0, length);
    }

    /**
     * Adapted from it.unimi.dsi.util.XoShiRo256PlusRandomGenerator.nextBytes(byte[])
     */
    default void clear(final IRandomGenerator random, final int index, final int length) {
        int i = length, n = 0;
        while (i != 0) {
            n = Integers.min(i, Long.BYTES);
            for (long bits = random.nextLong(); n-- != 0; bits >>= Long.BYTES) {
                putByte(--i + index, (byte) bits);
            }
        }
    }

    /**
     * Returns the buffer's byte capacity as its element count for the array interface.
     */
    @Override
    default int size() {
        return capacity();
    }

    /**
     * Returns true when the buffer's capacity is zero.
     */
    @Override
    default boolean isEmpty() {
        return capacity() == 0;
    }

    /**
     * Returns a read-only slice view of the whole buffer.
     */
    IByteBuffer asImmutableSlice();

}
