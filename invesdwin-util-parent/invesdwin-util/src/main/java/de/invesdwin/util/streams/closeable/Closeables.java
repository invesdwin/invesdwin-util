package de.invesdwin.util.streams.closeable;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Executor;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.handler.DisabledExecutorExceptionHandler;
import de.invesdwin.util.error.Throwables;

@Immutable
public final class Closeables {

    public static final Closeable[] EMPTY_ARRAY = new Closeable[0];
    private static final Executor ASYNC_EXECUTOR = Executors
            .newFixedThreadPool(Closeables.class.getSimpleName() + "_ASYNC", Executors.getCpuThreadPoolCount())
            .setDynamicThreadName(false)
            .setExecutorExceptionHandler(DisabledExecutorExceptionHandler.INSTANCE);

    private Closeables() {}

    public static void closeAsync(final Object obj) {
        if (obj == null) {
            return;
        }
        ASYNC_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                Closeables.closeQuietly(obj);
            }
        });
    }

    public static void closeAsync(final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        ASYNC_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                Closeables.closeQuietly(closeable);
            }
        });
    }

    public static void closeOrThrow(final Object obj) {
        if (obj == null) {
            return;
        }
        final Closeable cObj = (Closeable) obj;
        try {
            cObj.close();
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

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
