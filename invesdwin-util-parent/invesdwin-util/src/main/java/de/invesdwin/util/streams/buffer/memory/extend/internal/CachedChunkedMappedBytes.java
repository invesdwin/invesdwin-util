package de.invesdwin.util.streams.buffer.memory.extend.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;

import javax.annotation.concurrent.NotThreadSafe;

import net.openhft.chronicle.core.annotation.NonNegative;
import net.openhft.chronicle.core.io.ClosedIllegalStateException;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.io.ThreadingIllegalStateException;

@NotThreadSafe
public class CachedChunkedMappedBytes extends net.openhft.chronicle.bytes.internal.ChunkedMappedBytes {

    private final CachedChunkedMappedFile mappedFile;

    public CachedChunkedMappedBytes(final CachedChunkedMappedFile mappedFile) {
        super(mappedFile);
        this.mappedFile = mappedFile;
    }

    @Override
    protected void writeCheckOffset(@NonNegative final long offset, @NonNegative final long adding)
            throws BufferOverflowException, ClosedIllegalStateException, ThreadingIllegalStateException {
        //        super.writeCheckOffset(offset, adding);
        try {
            this.bytesStore = mappedFile.acquireByteStore(this, offset, this.bytesStore);
        } catch (final IOException e) {
            throw new IORuntimeException(
                    //CHECKSTYLE:OFF
                    String.format("Failed to acquireByteStore start: 0x%X offset: 0x%X safeLimit: 0x%X", start(),
                            offset, safeLimit()),
                    //CHECKSTYLE:ON
                    e);
        }
    }

}
