package de.invesdwin.util.streams.buffer.bytes.timed;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.bytes.IByteBufferProvider;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class IndexedByteBuffer {

    private final long index;
    private final IByteBufferProvider byteBuffer;

    public IndexedByteBuffer(final long index, final IByteBufferProvider byteBuffer) {
        this.index = index;
        this.byteBuffer = byteBuffer;
    }

    public long getIndex() {
        return index;
    }

    public FDate getTime() {
        return new FDate(index);
    }

    public IByteBufferProvider getByteBuffer() {
        return byteBuffer;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(IndexedByteBuffer.class, index);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof IndexedByteBuffer) {
            final IndexedByteBuffer cObj = (IndexedByteBuffer) obj;
            return index == cObj.index;
        }
        return false;
    }

}
