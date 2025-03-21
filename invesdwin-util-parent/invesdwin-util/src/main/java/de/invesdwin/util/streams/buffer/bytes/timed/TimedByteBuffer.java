package de.invesdwin.util.streams.buffer.bytes.timed;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.bytes.IByteBufferProvider;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class TimedByteBuffer {

    private final FDate time;
    private final IByteBufferProvider byteBuffer;

    public TimedByteBuffer(final FDate time, final IByteBufferProvider byteBuffer) {
        this.time = time;
        this.byteBuffer = byteBuffer;
    }

    public FDate getTime() {
        return time;
    }

    public IByteBufferProvider getByteBuffer() {
        return byteBuffer;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(TimedByteBuffer.class, time);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TimedByteBuffer) {
            final TimedByteBuffer cObj = (TimedByteBuffer) obj;
            return Objects.equals(time, cObj.time);
        }
        return false;
    }

}
