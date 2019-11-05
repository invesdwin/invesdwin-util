package de.invesdwin.util.lang;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.Throwables;

@Immutable
public final class Closeables {

    private Closeables() {}

    public static void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    public static void close(final Object obj) {
        if (obj instanceof Closeable) {
            final Closeable cObj = (Closeable) obj;
            try {
                cObj.close();
            } catch (final IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    public static void closeQuietly(final Object obj) {
        try {
            close(obj);
        } catch (final Throwable e) {
            //ignore
        }
    }

    /**
     * Not only ignores IOException, but any exception
     */
    public static void closeQuietly(final Closeable closeable) {
        try {
            close(closeable);
        } catch (final Throwable e) {
            //ignore
        }
    }

}
