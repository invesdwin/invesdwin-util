package de.invesdwin.util.lang;

import java.io.Closeable;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Closeables {

    private Closeables() {}

    /**
     * Not only ignores IOException, but any exception
     */
    public static void closeQuietly(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final Throwable e) {
            //ignore
        }
    }

}
