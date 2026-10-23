package de.invesdwin.util.streams.buffer.memory.delegate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.collections.array.base.IBaseArrayId;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.EmptyByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.MemoryDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.memory.EmptyMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedFromDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.stream.ExpandableMemoryBufferOutputStream;
import de.invesdwin.util.streams.buffer.memory.stream.MemoryBufferInputStream;
import de.invesdwin.util.streams.buffer.memory.stream.MemoryBufferOutputStream;

//CS:OFF JavaNCSS - wide accessor composite; the endian twin family doubles the per-type block
@NotThreadSafe
public class ListMemoryBuffer implements IMemoryBuffer {

    private final List<IMemoryBuffer> list;

    private IMutableSlicedDelegateMemoryBufferFactory mutableSliceFactory;

    public ListMemoryBuffer() {
        this(new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    public ListMemoryBuffer(final List<? extends IMemoryBuffer> list) {
        this.list = (List<IMemoryBuffer>) list;
        //just document expectations but skip this check internally
        //assertList(this.list);
    }

    public static void assertList(final List<IMemoryBuffer> list) {
        final int lastSegmentIndex = list.size() - 1;
        for (int i = 0; i <= lastSegmentIndex; i++) {
            final IMemoryBuffer segment = list.get(i);
            final ByteOrder segmentOrder = segment.getOrder();
            if (segmentOrder != ByteBuffers.DEFAULT_ORDER) {
                throw new IllegalArgumentException("All segments must have the same byte order: segmentIndex=" + i
                        + " segmentByteOrder=" + segmentOrder + " expectedByteOrder=" + ByteBuffers.DEFAULT_ORDER);
            }
        }
    }

    @Override
    public int getId() {
        return IBaseArrayId.newId(list);
    }

    public List<IMemoryBuffer> getList() {
        return list;
    }

    private IMutableSlicedDelegateMemoryBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateMemoryBufferFactory.newInstance(this);
        }
        return mutableSliceFactory;
    }

    private void ensureCapacity(final long index, final long length) {
        if (index < 0 || length < 0) {
            throw FastIndexOutOfBoundsException.getInstance("negative value: index=%s length=%s", index, length);
        }

        final long resultingPosition = index + length;
        final long currentArrayLength = capacity();
        if (resultingPosition > currentArrayLength) {
            if (resultingPosition > ExpandableArrayBuffer.MAX_ARRAY_LENGTH) {
                throw FastIndexOutOfBoundsException.getInstance("index=%s length=%s maxCapacity=%s", index, length,
                        +ExpandableArrayBuffer.MAX_ARRAY_LENGTH);
            }

            ensureCapacity((int) resultingPosition);
        }
    }

    @Override
    public IMemoryBuffer ensureCapacity(final long capacity) {
        if (list.isEmpty()) {
            throw EmptyMemoryBuffer.newEmptyException();
        } else if (list.size() == 1) {
            list.get(0).ensureCapacity(capacity);
        } else {
            long remaining = capacity;
            for (int buf = 0; buf < list.size(); buf++) {
                final IMemoryBuffer buffer = list.get(buf);
                remaining -= buffer.capacity();
            }
            if (remaining > 0) {
                final IMemoryBuffer lastBuffer = list.get(list.size() - 1);
                lastBuffer.ensureCapacity(lastBuffer.capacity() + remaining);
            }
        }
        return this;
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public boolean isReadOnly() {
        if (list.isEmpty()) {
            return true;
        } else {
            return list.get(0).isReadOnly();
        }
    }

    @Override
    public long addressOffset() {
        if (list.isEmpty()) {
            return EmptyMemoryBuffer.INSTANCE.addressOffset();
        } else if (list.size() == 1) {
            return list.get(0).addressOffset();
        } else {
            return 0;
        }
    }

    @Override
    public long capacity() {
        int capacity = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            capacity += buffer.capacity();
        }
        return capacity;
    }

    @Override
    public long getLong(final long index) {
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + Long.BYTES) {
                    return buffer.getLong(bufferPosition);
                } else {
                    final byte[] readBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
                    final long limit = index + Long.BYTES;
                    int ri = 0;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        readBuffer[ri] = buffer.getByte(bufferPosition);
                        i++;
                        ri++;
                        bufferPosition++;
                    }
                    //CHECKSTYLE:OFF
                    return (((long) readBuffer[0] << 56) + ((readBuffer[1] & 0xFFL) << 48)
                            + ((readBuffer[2] & 0xFFL) << 40) + ((readBuffer[3] & 0xFFL) << 32)
                            + ((readBuffer[4] & 0xFFL) << 24) + ((readBuffer[5] & 0xFFL) << 16)
                            + ((readBuffer[6] & 0xFFL) << 8) + ((readBuffer[7] & 0xFFL) << 0));
                    //CHECKSTYLE:ON
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public long getLongReverse(final long index) {
        final long bits = getLong(index);
        return Long.reverseBytes(bits);
    }

    @Override
    public int getInt(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + Integer.BYTES) {
                    return buffer.getInt(bufferPosition);
                } else {
                    final byte[] readBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
                    final long limit = index + Integer.BYTES;
                    int ri = 0;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        readBuffer[ri] = buffer.getByte(bufferPosition);
                        i++;
                        ri++;
                        bufferPosition++;
                    }
                    return ((readBuffer[0] << 24) + ((readBuffer[1] & 0xFF) << 16) + ((readBuffer[2] & 0xFF) << 8)
                            + ((readBuffer[3] & 0xFF)));
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Reads the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public int getIntVolatile(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Integer.BYTES) {
                    return buffer.getIntVolatile(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getIntVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public byte getByteVolatile(final long index) {
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                return buffer.getByteVolatile(index - position);
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Reads the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public char getCharVolatile(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Character.BYTES) {
                    return buffer.getCharVolatile(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getCharVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Reads the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public short getShortVolatile(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Short.BYTES) {
                    return buffer.getShortVolatile(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getShortVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Reads the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public long getLongVolatile(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Long.BYTES) {
                    return buffer.getLongVolatile(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getLongVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Reads the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public float getFloatVolatile(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Float.BYTES) {
                    return buffer.getFloatVolatile(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getFloatVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Reads the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public double getDoubleVolatile(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Double.BYTES) {
                    return buffer.getDoubleVolatile(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getDoubleVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Reads the value at the given index with acquire semantics. Atomic acquire wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public int getIntAcquire(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Integer.BYTES) {
                    return buffer.getIntAcquire(bufferPosition);
                } else {
                    //value splits across buffers: cross-part acquire wide access is not atomic-supported
                    throw new UnsupportedOperationException("getIntAcquire: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Reads the value at the given index with acquire semantics. Atomic acquire wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public long getLongAcquire(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Long.BYTES) {
                    return buffer.getLongAcquire(bufferPosition);
                } else {
                    //value splits across buffers: cross-part acquire wide access is not atomic-supported
                    throw new UnsupportedOperationException("getLongAcquire: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of getCharVolatile(long) for this buffer composite: the swap is a value level
     * step on the single volatile access of the part that owns the position. Atomic volatile wide
     * access is only available where the value fits completely inside a single buffer of this
     * list; positions that split across buffers throw an {@link UnsupportedOperationException}
     * naming the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public char getCharVolatileReverse(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Character.BYTES) {
                    return buffer.getCharVolatileReverse(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getCharVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of getShortVolatile(long) for this buffer composite: the swap is a value level
     * step on the single volatile access of the part that owns the position. Atomic volatile wide
     * access is only available where the value fits completely inside a single buffer of this
     * list; positions that split across buffers throw an {@link UnsupportedOperationException}
     * naming the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public short getShortVolatileReverse(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Short.BYTES) {
                    return buffer.getShortVolatileReverse(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getShortVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of getIntVolatile(long) for this buffer composite: the swap is a value level
     * step on the single volatile access of the part that owns the position. Atomic volatile wide
     * access is only available where the value fits completely inside a single buffer of this
     * list; positions that split across buffers throw an {@link UnsupportedOperationException}
     * naming the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public int getIntVolatileReverse(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Integer.BYTES) {
                    return buffer.getIntVolatileReverse(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getIntVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of getLongVolatile(long) for this buffer composite: the swap is a value level
     * step on the single volatile access of the part that owns the position. Atomic volatile wide
     * access is only available where the value fits completely inside a single buffer of this
     * list; positions that split across buffers throw an {@link UnsupportedOperationException}
     * naming the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public long getLongVolatileReverse(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Long.BYTES) {
                    return buffer.getLongVolatileReverse(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getLongVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of getFloatVolatile(long) for this buffer composite: the swap is a value level
     * step on the single volatile access of the part that owns the position. Atomic volatile wide
     * access is only available where the value fits completely inside a single buffer of this
     * list; positions that split across buffers throw an {@link UnsupportedOperationException}
     * naming the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public float getFloatVolatileReverse(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Float.BYTES) {
                    return buffer.getFloatVolatileReverse(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getFloatVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of getDoubleVolatile(long) for this buffer composite: the swap is a value level
     * step on the single volatile access of the part that owns the position. Atomic volatile wide
     * access is only available where the value fits completely inside a single buffer of this
     * list; positions that split across buffers throw an {@link UnsupportedOperationException}
     * naming the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public double getDoubleVolatileReverse(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Double.BYTES) {
                    return buffer.getDoubleVolatileReverse(bufferPosition);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("getDoubleVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of getIntAcquire(long) for this buffer composite: the swap is a value level
     * step on the single acquire access of the part that owns the position. Atomic acquire wide
     * access is only available where the value fits completely inside a single buffer of this
     * list; positions that split across buffers throw an {@link UnsupportedOperationException}
     * naming the method and index, because cross-part acquire wide access is not atomic-supported.
     */
    @Override
    public int getIntAcquireReverse(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Integer.BYTES) {
                    return buffer.getIntAcquireReverse(bufferPosition);
                } else {
                    //value splits across buffers: cross-part acquire wide access is not atomic-supported
                    throw new UnsupportedOperationException("getIntAcquireReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of getLongAcquire(long) for this buffer composite: the swap is a value level
     * step on the single acquire access of the part that owns the position. Atomic acquire wide
     * access is only available where the value fits completely inside a single buffer of this
     * list; positions that split across buffers throw an {@link UnsupportedOperationException}
     * naming the method and index, because cross-part acquire wide access is not atomic-supported.
     */
    @Override
    public long getLongAcquireReverse(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Long.BYTES) {
                    return buffer.getLongAcquireReverse(bufferPosition);
                } else {
                    //value splits across buffers: cross-part acquire wide access is not atomic-supported
                    throw new UnsupportedOperationException("getLongAcquireReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public int getIntReverse(final long index) {
        final int bits = getInt(index);
        return Integer.reverseBytes(bits);
    }

    @Override
    public double getDouble(final long index) {
        return Double.longBitsToDouble(getLong(index));
    }

    @Override
    public double getDoubleReverse(final long index) {
        final long bits = getLong(index);
        return Double.longBitsToDouble(Long.reverseBytes(bits));
    }

    @Override
    public float getFloat(final long index) {
        return Float.intBitsToFloat(getInt(index));
    }

    @Override
    public float getFloatReverse(final long index) {
        final int bits = getInt(index);
        return Float.intBitsToFloat(Integer.reverseBytes(bits));
    }

    @Override
    public short getShort(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + Short.BYTES) {
                    return buffer.getShort(bufferPosition);
                } else {
                    final byte[] readBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
                    final long limit = index + Short.BYTES;
                    int ri = 0;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        readBuffer[ri] = buffer.getByte(bufferPosition);
                        i++;
                        ri++;
                        bufferPosition++;
                    }
                    return (short) ((readBuffer[0] << 8) + (readBuffer[1] & 0xFF));
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public short getShortReverse(final long index) {
        final short bits = getShort(index);
        return Short.reverseBytes(bits);
    }

    @Override
    public char getChar(final long index) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + Character.BYTES) {
                    return buffer.getChar(bufferPosition);
                } else {
                    final byte[] readBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
                    final long limit = index + Character.BYTES;
                    int ri = 0;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        readBuffer[ri] = buffer.getByte(bufferPosition);
                        i++;
                        ri++;
                        bufferPosition++;
                    }
                    return (char) ((readBuffer[0] << 8) + (readBuffer[1] & 0xFF));
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public char getCharReverse(final long index) {
        final short bits = (short) getChar(index);
        return (char) Short.reverseBytes(bits);
    }

    @Override
    public byte getByte(final long index) {
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                return buffer.getByte(index - position);
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public long wrapAdjustment() {
        if (list.isEmpty()) {
            return EmptyByteBuffer.INSTANCE.wrapAdjustment();
        } else if (list.size() == 1) {
            return list.get(0).wrapAdjustment();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isExpandable() {
        if (list.isEmpty()) {
            return false;
        } else {
            final IMemoryBuffer lastBuffer = list.get(list.size() - 1);
            return lastBuffer.isExpandable();
        }
    }

    @Override
    public void putLong(final long index, final long value) {
        final byte[] writeBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
        writeBuffer[0] = (byte) (value >>> 56);
        writeBuffer[1] = (byte) (value >>> 48);
        writeBuffer[2] = (byte) (value >>> 40);
        writeBuffer[3] = (byte) (value >>> 32);
        writeBuffer[4] = (byte) (value >>> 24);
        writeBuffer[5] = (byte) (value >>> 16);
        writeBuffer[6] = (byte) (value >>> 8);
        writeBuffer[7] = (byte) (value);
        putBytes(index, writeBuffer);
    }

    @Override
    public void putLongReverse(final long index, final long value) {
        final long bits = Long.reverseBytes(value);
        putLong(index, bits);
    }

    @Override
    public void putInt(final long index, final int value) {
        final byte[] writeBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
        writeBuffer[0] = (byte) (value >>> 24);
        writeBuffer[1] = (byte) (value >>> 16);
        writeBuffer[2] = (byte) (value >>> 8);
        writeBuffer[3] = (byte) (value);
        putBytes(index, writeBuffer, 0, Integer.BYTES);
    }

    /**
     * Writes the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putIntVolatile(final long index, final int value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Integer.BYTES) {
                    buffer.putIntVolatile(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putIntVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void putByteVolatile(final long index, final byte value) {
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                buffer.putByteVolatile(index - position, value);
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Writes the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putCharVolatile(final long index, final char value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Character.BYTES) {
                    buffer.putCharVolatile(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putCharVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Writes the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putShortVolatile(final long index, final short value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Short.BYTES) {
                    buffer.putShortVolatile(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putShortVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Writes the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putLongVolatile(final long index, final long value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Long.BYTES) {
                    buffer.putLongVolatile(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putLongVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Writes the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putFloatVolatile(final long index, final float value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Float.BYTES) {
                    buffer.putFloatVolatile(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putFloatVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Writes the value at the given index with volatile semantics. Atomic volatile wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putDoubleVolatile(final long index, final double value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Double.BYTES) {
                    buffer.putDoubleVolatile(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putDoubleVolatile: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Writes the value at the given index with release semantics. Atomic release wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putIntRelease(final long index, final int value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Integer.BYTES) {
                    buffer.putIntRelease(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part release wide access is not atomic-supported
                    throw new UnsupportedOperationException("putIntRelease: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Writes the value at the given index with ordered semantics. Atomic ordered wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putIntOrdered(final long index, final int value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Integer.BYTES) {
                    buffer.putIntOrdered(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part ordered wide access is not atomic-supported
                    throw new UnsupportedOperationException("putIntOrdered: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Writes the value at the given index with release semantics. Atomic release wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putLongRelease(final long index, final long value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Long.BYTES) {
                    buffer.putLongRelease(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part release wide access is not atomic-supported
                    throw new UnsupportedOperationException("putLongRelease: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Writes the value at the given index with ordered semantics. Atomic ordered wide access is
     * only available where the value fits completely inside a single buffer of this list;
     * positions that split across buffers throw an {@link UnsupportedOperationException} naming
     * the method and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putLongOrdered(final long index, final long value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Long.BYTES) {
                    buffer.putLongOrdered(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part ordered wide access is not atomic-supported
                    throw new UnsupportedOperationException("putLongOrdered: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of putCharVolatile(long, x) for this buffer composite: the value bytes are swapped
     * by the part and written in a single volatile access of it. Atomic volatile wide access is only
     * available where the value fits completely inside a single buffer of this list; positions
     * that split across buffers throw an {@link UnsupportedOperationException} naming the method
     * and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putCharVolatileReverse(final long index, final char value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Character.BYTES) {
                    buffer.putCharVolatileReverse(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putCharVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of putShortVolatile(long, x) for this buffer composite: the value bytes are swapped
     * by the part and written in a single volatile access of it. Atomic volatile wide access is only
     * available where the value fits completely inside a single buffer of this list; positions
     * that split across buffers throw an {@link UnsupportedOperationException} naming the method
     * and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putShortVolatileReverse(final long index, final short value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Short.BYTES) {
                    buffer.putShortVolatileReverse(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putShortVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of putIntVolatile(long, x) for this buffer composite: the value bytes are swapped
     * by the part and written in a single volatile access of it. Atomic volatile wide access is only
     * available where the value fits completely inside a single buffer of this list; positions
     * that split across buffers throw an {@link UnsupportedOperationException} naming the method
     * and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putIntVolatileReverse(final long index, final int value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Integer.BYTES) {
                    buffer.putIntVolatileReverse(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putIntVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of putLongVolatile(long, x) for this buffer composite: the value bytes are swapped
     * by the part and written in a single volatile access of it. Atomic volatile wide access is only
     * available where the value fits completely inside a single buffer of this list; positions
     * that split across buffers throw an {@link UnsupportedOperationException} naming the method
     * and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putLongVolatileReverse(final long index, final long value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Long.BYTES) {
                    buffer.putLongVolatileReverse(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putLongVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of putFloatVolatile(long, x) for this buffer composite: the value bytes are swapped
     * by the part and written in a single volatile access of it. Atomic volatile wide access is only
     * available where the value fits completely inside a single buffer of this list; positions
     * that split across buffers throw an {@link UnsupportedOperationException} naming the method
     * and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putFloatVolatileReverse(final long index, final float value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Float.BYTES) {
                    buffer.putFloatVolatileReverse(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putFloatVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of putDoubleVolatile(long, x) for this buffer composite: the value bytes are swapped
     * by the part and written in a single volatile access of it. Atomic volatile wide access is only
     * available where the value fits completely inside a single buffer of this list; positions
     * that split across buffers throw an {@link UnsupportedOperationException} naming the method
     * and index, because cross-part volatile wide access is not atomic-supported.
     */
    @Override
    public void putDoubleVolatileReverse(final long index, final double value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Double.BYTES) {
                    buffer.putDoubleVolatileReverse(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part volatile wide access is not atomic-supported
                    throw new UnsupportedOperationException("putDoubleVolatileReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of putIntRelease(long, x) for this buffer composite: the value bytes are swapped
     * by the part and written in a single release access of it. Atomic release wide access is only
     * available where the value fits completely inside a single buffer of this list; positions
     * that split across buffers throw an {@link UnsupportedOperationException} naming the method
     * and index, because cross-part release wide access is not atomic-supported.
     */
    @Override
    public void putIntReleaseReverse(final long index, final int value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Integer.BYTES) {
                    buffer.putIntReleaseReverse(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part release wide access is not atomic-supported
                    throw new UnsupportedOperationException("putIntReleaseReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of putLongRelease(long, x) for this buffer composite: the value bytes are swapped
     * by the part and written in a single release access of it. Atomic release wide access is only
     * available where the value fits completely inside a single buffer of this list; positions
     * that split across buffers throw an {@link UnsupportedOperationException} naming the method
     * and index, because cross-part release wide access is not atomic-supported.
     */
    @Override
    public void putLongReleaseReverse(final long index, final long value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Long.BYTES) {
                    buffer.putLongReleaseReverse(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part release wide access is not atomic-supported
                    throw new UnsupportedOperationException("putLongReleaseReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of putIntOrdered(long, x) for this buffer composite: the value bytes are swapped
     * by the part and written in a single ordered access of it. Atomic ordered wide access is only
     * available where the value fits completely inside a single buffer of this list; positions
     * that split across buffers throw an {@link UnsupportedOperationException} naming the method
     * and index, because cross-part ordered wide access is not atomic-supported.
     */
    @Override
    public void putIntOrderedReverse(final long index, final int value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Integer.BYTES) {
                    buffer.putIntOrderedReverse(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part ordered wide access is not atomic-supported
                    throw new UnsupportedOperationException("putIntOrderedReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    /**
     * Little-endian twin of putLongOrdered(long, x) for this buffer composite: the value bytes are swapped
     * by the part and written in a single ordered access of it. Atomic ordered wide access is only
     * available where the value fits completely inside a single buffer of this list; positions
     * that split across buffers throw an {@link UnsupportedOperationException} naming the method
     * and index, because cross-part ordered wide access is not atomic-supported.
     */
    @Override
    public void putLongOrderedReverse(final long index, final long value) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                final long bufferPosition = index - position;
                if (capacity >= bufferPosition + Long.BYTES) {
                    buffer.putLongOrderedReverse(bufferPosition, value);
                } else {
                    //value splits across buffers: cross-part ordered wide access is not atomic-supported
                    throw new UnsupportedOperationException("putLongOrderedReverse: index="
                            + index + " splits across buffers (not atomic-supported)");
                }
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void putIntReverse(final long index, final int value) {
        final int bits = Integer.reverseBytes(value);
        putInt(index, bits);
    }

    @Override
    public void putDouble(final long index, final double value) {
        putLong(index, Double.doubleToLongBits(value));
    }

    @Override
    public void putDoubleReverse(final long index, final double value) {
        final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
        putLong(index, bits);
    }

    @Override
    public void putFloat(final long index, final float value) {
        putInt(index, Float.floatToIntBits(value));
    }

    @Override
    public void putFloatReverse(final long index, final float value) {
        final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
        putInt(index, bits);
    }

    @Override
    public void putShort(final long index, final short value) {
        final byte[] writeBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
        writeBuffer[0] = (byte) (value >>> 8);
        writeBuffer[1] = (byte) (value);
        putBytes(index, writeBuffer, 0, Short.BYTES);
    }

    @Override
    public void putShortReverse(final long index, final short value) {
        final short bits = Short.reverseBytes(value);
        putShort(index, bits);
    }

    @Override
    public void putChar(final long index, final char value) {
        final byte[] writeBuffer = InputStreams.LONG_BUFFER_HOLDER.get();
        writeBuffer[0] = (byte) (value >>> 8);
        writeBuffer[1] = (byte) (value);
        putBytes(index, writeBuffer, 0, Short.BYTES);
    }

    @Override
    public void putCharReverse(final long index, final char value) {
        final short bits = Short.reverseBytes((short) value);
        putShort(index, bits);
    }

    @Override
    public void putByte(final long index, final byte value) {
        int position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            final IMemoryBuffer buffer = list.get(buf);
            final long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                buffer.putByte(index - position, value);
                return;
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        return new MemoryBufferInputStream(this, index, length);
    }

    @Override
    public OutputStream asOutputStream(final long index, final long length) {
        if (isExpandable() && index + length >= capacity()) {
            //allow output stream to actually grow the buffer
            return new ExpandableMemoryBufferOutputStream(this, index);
        } else {
            return new MemoryBufferOutputStream(this, index, length);
        }
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        if (list.isEmpty()) {
            if (index == 0 && length == 0) {
                return EmptyByteBuffer.EMPTY_BYTE_BUFFER;
            } else {
                throw EmptyByteBuffer.newEmptyException();
            }
        } else if (list.size() == 1) {
            return list.get(0).asNioByteBuffer(index, length);
        } else {
            //WARNING: this is not a mutable version of the underlying memory
            final java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocate(length);
            getBytes(index, byteBuffer, 0, length);
            return byteBuffer;
        }
    }

    @Override
    public byte[] asByteArrayCopy(final long index, final int length) {
        if (list.isEmpty()) {
            if (index == 0 && length == 0) {
                return Bytes.EMPTY_ARRAY;
            } else {
                throw EmptyByteBuffer.newEmptyException();
            }
        } else if (list.size() == 1) {
            return list.get(0).asByteArrayCopy(index, length);
        } else {
            final byte[] byteArray = ByteBuffers.allocateByteArray(length);
            getBytes(index, byteArray, 0, length);
            return byteArray;
        }
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        if (list.isEmpty()) {
            if (index == 0 && length == 0) {
                return EmptyByteBuffer.EMPTY_AGRONA_BUFFER;
            } else {
                throw EmptyByteBuffer.newEmptyException();
            }
        } else if (list.size() == 1) {
            return list.get(0).asDirectBuffer(index, length);
        } else {
            final UnsafeBuffer directBuffer = new UnsafeBuffer(ByteBuffers.allocateByteArray(length));
            getBytes(index, directBuffer, 0, length);
            return directBuffer;
        }
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        return new MemoryDelegateByteBuffer(newSlice(index, length));
    }

    @Override
    public IMemoryBuffer sliceFrom(final long index) {
        return getMutableSliceFactory().sliceFrom(index);
    }

    @Override
    public IMemoryBuffer slice(final long index, final long length) {
        return getMutableSliceFactory().slice(index, length);
    }

    @Override
    public IMemoryBuffer newSliceFrom(final long index) {
        if (index == 0) {
            return this;
        } else {
            return new SlicedFromDelegateMemoryBuffer(this, index);
        }
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new SlicedDelegateMemoryBuffer(this, index, length);
        }
    }

    @Override
    public String getStringAsciii(final long index, final int length) {
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
        getBytes(index, bytes, 0, length);
        return ByteBuffers.newStringAscii(bytes);
    }

    @Override
    public void getStringAsciii(final long index, final int length, final Appendable dst) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.getStringAsciii(bufferPosition, length, dst);
                    return;
                } else {
                    try {
                        final long limit = index + length;
                        for (long i = index; i < limit;) {
                            while (bufferPosition >= capacity) {
                                buf++;
                                buffer = list.get(buf);
                                capacity = buffer.capacity();
                                bufferPosition = 0;
                            }
                            final char c = (char) buffer.getByte(bufferPosition);
                            dst.append(c > 127 ? '?' : c);
                            i++;
                            bufferPosition++;
                        }
                    } catch (final IOException e) {
                        throw Throwables.propagate(e);
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());

    }

    @Override
    public void putStringAsciii(final long index, final CharSequence value, final int valueIndex, final int length) {
        ensureCapacity(index, length);
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.putStringAsciii(bufferPosition, value, valueIndex, length);
                    return;
                } else {
                    final long limit = index + length;
                    int vi = valueIndex;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        char c = value.charAt(vi);
                        if (c > 127) {
                            c = '?';
                        }
                        buffer.putByte(bufferPosition, (byte) c);
                        i++;
                        vi++;
                        bufferPosition++;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public int putStringUtf8(final long index, final String value) {
        final byte[] bytes = ByteBuffers.newStringUtf8Bytes(value);
        ensureCapacity(index, bytes.length);
        putBytes(index, bytes);
        return bytes.length;
    }

    @Override
    public String getStringUtf8(final long index, final int length) {
        final byte[] bytes = ByteBuffers.allocateByteArray(length);
        getBytes(index, bytes, 0, length);
        return ByteBuffers.newStringUtf8(bytes);
    }

    @Override
    public void getStringUtf8(final long index, final int length, final Appendable dst) {
        final String string = getStringUtf8(index, length);
        try {
            dst.append(string);
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void getBytesTo(final long index, final OutputStream dst, final long length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else if (dst instanceof FileOutputStream && ((FileOutputStream) dst).getChannel() != null) {
            final FileOutputStream cDst = (FileOutputStream) dst;
            getBytesTo(index, cDst.getChannel(), length);
        } else if (dst instanceof DataOutput) {
            getBytesTo(index, (DataOutput) dst, length);
        } else {
            int position = 0;
            for (int buf = 0; buf < list.size(); buf++) {
                IMemoryBuffer buffer = list.get(buf);
                long capacity = buffer.capacity();
                if (index >= position + capacity) {
                    position += capacity;
                    continue;
                } else {
                    long bufferPosition = index - position;
                    if (capacity >= bufferPosition + length) {
                        buffer.getBytesTo(bufferPosition, dst, length);
                        return;
                    } else {
                        try {
                            final long limit = index + length;
                            long remaining = length;
                            for (long i = index; i < limit;) {
                                while (bufferPosition >= capacity) {
                                    buf++;
                                    buffer = list.get(buf);
                                    capacity = buffer.capacity();
                                    bufferPosition = 0;
                                }
                                final long toCopy = Longs.min(remaining, buffer.remaining(bufferPosition));
                                buffer.getBytesTo(bufferPosition, dst, toCopy);
                                remaining -= toCopy;
                                i += toCopy;
                                bufferPosition += toCopy;
                            }
                        } catch (final IOException e) {
                            throw Throwables.propagate(e);
                        }
                        return;
                    }
                }
            }
            throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
        }
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else {
            ensureCapacity(index, length);
            int position = 0;
            for (int buf = 0; buf < list.size(); buf++) {
                IMemoryBuffer buffer = list.get(buf);
                long capacity = buffer.capacity();
                if (index >= position + capacity) {
                    position += capacity;
                    continue;
                } else {
                    long bufferPosition = index - position;
                    if (capacity >= bufferPosition + length) {
                        buffer.putBytesTo(bufferPosition, src, length);
                        return;
                    } else {
                        try {
                            final long limit = index + length;
                            long remaining = length;
                            for (long i = index; i < limit;) {
                                while (bufferPosition >= capacity) {
                                    buf++;
                                    buffer = list.get(buf);
                                    capacity = buffer.capacity();
                                    bufferPosition = 0;
                                }
                                final long toCopy = Longs.min(remaining, buffer.remaining(bufferPosition));
                                buffer.putBytesTo(bufferPosition, src, toCopy);
                                remaining -= toCopy;
                                i += toCopy;
                                bufferPosition += toCopy;
                            }
                        } catch (final IOException e) {
                            throw Throwables.propagate(e);
                        }
                        return;
                    }
                }
            }
            throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
        }
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) throws IOException {
        if (src instanceof ReadableByteChannel) {
            putBytesTo(index, (ReadableByteChannel) src, length);
        } else if (src instanceof FileInputStream && ((FileInputStream) src).getChannel() != null) {
            final FileInputStream cSrc = (FileInputStream) src;
            putBytesTo(index, cSrc.getChannel(), length);
        } else if (src instanceof DataInput) {
            putBytesTo(index, (DataInput) src, length);
        } else {
            ensureCapacity(index, length);
            long position = 0;
            for (int buf = 0; buf < list.size(); buf++) {
                IMemoryBuffer buffer = list.get(buf);
                long capacity = buffer.capacity();
                if (index >= position + capacity) {
                    position += capacity;
                    continue;
                } else {
                    long bufferPosition = index - position;
                    if (capacity >= bufferPosition + length) {
                        buffer.putBytesTo(bufferPosition, src, length);
                        return;
                    } else {
                        try {
                            final long limit = index + length;
                            long remaining = length;
                            for (long i = index; i < limit;) {
                                while (bufferPosition >= capacity) {
                                    buf++;
                                    buffer = list.get(buf);
                                    capacity = buffer.capacity();
                                    bufferPosition = 0;
                                }
                                final long toCopy = Longs.min(remaining, buffer.remaining(bufferPosition));
                                buffer.putBytesTo(bufferPosition, src, toCopy);
                                remaining -= toCopy;
                                i += toCopy;
                                bufferPosition += toCopy;
                            }
                        } catch (final IOException e) {
                            throw Throwables.propagate(e);
                        }
                        return;
                    }
                }
            }
            throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
        }
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        ensureCapacity(index, length);
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.putBytesTo(bufferPosition, src, length);
                    return;
                } else {
                    try {
                        final long limit = index + length;
                        long remaining = length;
                        for (long i = index; i < limit;) {
                            while (bufferPosition >= capacity) {
                                buf++;
                                buffer = list.get(buf);
                                capacity = buffer.capacity();
                                bufferPosition = 0;
                            }
                            final long toCopy = Longs.min(remaining, buffer.remaining(bufferPosition));
                            buffer.putBytesTo(bufferPosition, src, toCopy);
                            remaining -= toCopy;
                            i += toCopy;
                            bufferPosition += toCopy;
                        }
                    } catch (final IOException e) {
                        throw Throwables.propagate(e);
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void getBytesTo(final long index, final DataOutput dst, final long length) throws IOException {
        if (dst instanceof WritableByteChannel) {
            getBytesTo(index, (WritableByteChannel) dst, length);
        } else {
            long position = 0;
            for (int buf = 0; buf < list.size(); buf++) {
                IMemoryBuffer buffer = list.get(buf);
                long capacity = buffer.capacity();
                if (index >= position + capacity) {
                    position += capacity;
                    continue;
                } else {
                    long bufferPosition = index - position;
                    if (capacity >= bufferPosition + length) {
                        buffer.getBytesTo(bufferPosition, dst, length);
                        return;
                    } else {
                        try {
                            final long limit = index + length;
                            long remaining = length;
                            for (long i = index; i < limit;) {
                                while (bufferPosition >= capacity) {
                                    buf++;
                                    buffer = list.get(buf);
                                    capacity = buffer.capacity();
                                    bufferPosition = 0;
                                }
                                final long toCopy = Longs.min(remaining, buffer.remaining(bufferPosition));
                                buffer.getBytesTo(bufferPosition, dst, toCopy);
                                remaining -= toCopy;
                                i += toCopy;
                                bufferPosition += toCopy;
                            }
                        } catch (final IOException e) {
                            throw Throwables.propagate(e);
                        }
                        return;
                    }
                }
            }
            throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
        }
    }

    @Override
    public void getBytesTo(final long index, final WritableByteChannel dst, final long length) throws IOException {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.getBytesTo(bufferPosition, dst, length);
                    return;
                } else {
                    try {
                        final long limit = index + length;
                        long remaining = length;
                        for (long i = index; i < limit;) {
                            while (bufferPosition >= capacity) {
                                buf++;
                                buffer = list.get(buf);
                                capacity = buffer.capacity();
                                bufferPosition = 0;
                            }
                            final long toCopy = Longs.min(remaining, buffer.remaining(bufferPosition));
                            buffer.getBytesTo(bufferPosition, dst, toCopy);
                            remaining -= toCopy;
                            i += toCopy;
                            bufferPosition += toCopy;
                        }
                    } catch (final IOException e) {
                        throw Throwables.propagate(e);
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.putBytes(bufferPosition, src, srcIndex, length);
                    return;
                } else {
                    final long limit = index + length;
                    int remaining = length;
                    int srcPosition = srcIndex;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = ByteBuffers
                                .checkedCast(Longs.min(remaining, buffer.remaining(bufferPosition)));
                        buffer.putBytes(bufferPosition, src, srcPosition, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                        srcPosition += toCopy;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.putBytes(bufferPosition, srcBuffer, srcIndex, length);
                    return;
                } else {
                    final long limit = index + length;
                    int remaining = length;
                    int srcPosition = srcIndex;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = ByteBuffers
                                .checkedCast(Longs.min(remaining, buffer.remaining(bufferPosition)));
                        buffer.putBytes(bufferPosition, srcBuffer, srcPosition, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                        srcPosition += toCopy;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.putBytes(bufferPosition, srcBuffer, srcIndex, length);
                    return;
                } else {
                    final long limit = index + length;
                    int remaining = length;
                    int srcPosition = srcIndex;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = ByteBuffers
                                .checkedCast(Longs.min(remaining, buffer.remaining(bufferPosition)));
                        buffer.putBytes(bufferPosition, srcBuffer, srcPosition, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                        srcPosition += toCopy;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.putBytes(bufferPosition, srcBuffer, srcIndex, length);
                    return;
                } else {
                    final long limit = index + length;
                    int remaining = length;
                    int srcPosition = srcIndex;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = ByteBuffers
                                .checkedCast(Longs.min(remaining, buffer.remaining(bufferPosition)));
                        buffer.putBytes(bufferPosition, srcBuffer, srcPosition, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                        srcPosition += toCopy;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        ensureCapacity(index, length);
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.putBytes(bufferPosition, srcBuffer, srcIndex, length);
                    return;
                } else {
                    final long limit = index + length;
                    long remaining = length;
                    long srcPosition = srcIndex;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final long toCopy = Longs.min(remaining, buffer.remaining(bufferPosition));
                        buffer.putBytes(bufferPosition, srcBuffer, srcPosition, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                        srcPosition += toCopy;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void getBytes(final long index, final byte[] dst, final int dstIndex, final int length) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.getBytes(bufferPosition, dst, dstIndex, length);
                    return;
                } else {
                    final long limit = index + length;
                    int remaining = length;
                    int dstPosition = dstIndex;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = ByteBuffers
                                .checkedCast(Longs.min(remaining, buffer.remaining(bufferPosition)));
                        buffer.getBytes(bufferPosition, dst, dstPosition, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                        dstPosition += toCopy;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void getBytes(final long index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.getBytes(bufferPosition, dstBuffer, dstIndex, length);
                    return;
                } else {
                    final long limit = index + length;
                    int remaining = length;
                    int dstPosition = dstIndex;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = ByteBuffers
                                .checkedCast(Longs.min(remaining, buffer.remaining(bufferPosition)));
                        buffer.getBytes(bufferPosition, dstBuffer, dstPosition, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                        dstPosition += toCopy;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void getBytes(final long index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.getBytes(bufferPosition, dstBuffer, dstIndex, length);
                    return;
                } else {
                    final long limit = index + length;
                    int remaining = length;
                    int dstPosition = dstIndex;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = ByteBuffers
                                .checkedCast(Longs.min(remaining, buffer.remaining(bufferPosition)));
                        buffer.getBytes(bufferPosition, dstBuffer, dstPosition, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                        dstPosition += toCopy;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void getBytes(final long index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.getBytes(bufferPosition, dstBuffer, dstIndex, length);
                    return;
                } else {
                    final long limit = index + length;
                    int remaining = length;
                    int dstPosition = dstIndex;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = ByteBuffers
                                .checkedCast(Longs.min(remaining, buffer.remaining(bufferPosition)));
                        buffer.getBytes(bufferPosition, dstBuffer, dstPosition, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                        dstPosition += toCopy;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void getBytes(final long index, final IMemoryBuffer dstBuffer, final long dstIndex, final long length) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.getBytes(bufferPosition, dstBuffer, dstIndex, length);
                    return;
                } else {
                    final long limit = index + length;
                    long remaining = length;
                    long dstPosition = dstIndex;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final long toCopy = Longs.min(remaining, buffer.remaining(bufferPosition));
                        buffer.getBytes(bufferPosition, dstBuffer, dstPosition, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                        dstPosition += toCopy;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryBuffer buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (index >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = index - position;
                if (capacity >= bufferPosition + length) {
                    buffer.clear(value, bufferPosition, length);
                    return;
                } else {
                    final long limit = index + length;
                    long remaining = length;
                    for (long i = index; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final long toCopy = Longs.min(remaining, buffer.remaining(bufferPosition));
                        buffer.clear(value, bufferPosition, toCopy);
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                    }
                    return;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        if (list.size() == 1) {
            final IMemoryBuffer delegate = list.get(0);
            if (delegate.getClass().isAssignableFrom(type)) {
                return (T) delegate;
            }
        }
        return null;
    }

    @Override
    public IMemoryBuffer clone(final long index, final int length) {
        return MemoryBuffers.wrap(asByteArrayCopy(index, length));
    }

    @Override
    public IMemoryBuffer asImmutableSlice() {
        return this;
    }

}
//CS:ON JavaNCSS
