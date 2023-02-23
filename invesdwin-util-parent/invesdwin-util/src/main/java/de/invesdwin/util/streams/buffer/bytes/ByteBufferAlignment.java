package de.invesdwin.util.streams.buffer.bytes;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public enum ByteBufferAlignment {
    TYPE(0x0008),
    CACHE(0x0040),
    PAGE(0x1000);

    private final int alignment;

    ByteBufferAlignment(final int alignment) {
        this.alignment = alignment;
    }

    public int value() {
        return alignment;
    }

}