package de.invesdwin.util.lang;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Closeables {

    private Closeables() {}

    public static void maybeClose(final Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }

    public static void maybeClose(final Object obj) throws IOException {
        if (obj instanceof Closeable) {
            final Closeable cObj = (Closeable) obj;
            cObj.close();
        }
    }

    public static void closeQuietly(final Object obj) {
        try {
            maybeClose(obj);
        } catch (final Throwable e) {
            //ignore
        }
    }

    /**
     * Not only ignores IOException, but any exception
     */
    public static void closeQuietly(final Closeable closeable) {
        try {
            maybeClose(closeable);
        } catch (final Throwable e) {
            //ignore
        }
    }

}
