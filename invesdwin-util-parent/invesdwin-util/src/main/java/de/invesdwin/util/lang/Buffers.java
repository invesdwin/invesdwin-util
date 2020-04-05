package de.invesdwin.util.lang;

import java.nio.Buffer;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Buffers {

    private Buffers() {
    }

    /**
     * Workaround for java 8 compiled on java 9 or higher
     */
    public static void position(final Buffer buffer, final int position) {
        buffer.position(position);
    }

}
