package net.openhft.chronicle.core;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;

@Immutable
public final class OSAccessor {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(OSAccessor.class);
    private static final AtomicLong MEMORY_MAPPED;
    private static final MethodHandle UNMAPP0_MH;

    static {
        MEMORY_MAPPED = Reflections.staticField("memoryMapped").ofType(AtomicLong.class).in(OS.class).get();
        UNMAPP0_MH = Reflections.staticField("UNMAPP0_MH").ofType(MethodHandle.class).in(OS.class).get();
    }

    private OSAccessor() {}

    public static long mapUnaligned(final FileChannel fileChannel, final FileChannel.MapMode mode, final long start,
            final long size) throws IOException, IllegalArgumentException {
        if (OS.isWindows() && size > 4L << 30) {
            throw new IllegalArgumentException(
                    "Mapping more than 4096 MiB is unusable on Windows, size = " + (size >> 20) + " MiB");
        }
        final long address = OS.map0(fileChannel, OS.imodeFor(mode), start, size);
        final long threshold = Math.min(64 * size, 32L << 40);
        if (OS.isLinux() && (address > 0 && address < threshold) && Jvm.is64bit()) {
            final double ratio = (double) threshold / address;
            final long durationMs = Math.max(5000, (long) (250 * ratio * ratio * ratio));
            //CHECKSTYLE:OFF
            LOG.warn("Running low on virtual memory, pausing {} ms, address: {}", durationMs,
                    Long.toUnsignedString(address, 16));
            //CHECKSTYLE:ON
            Jvm.pause(durationMs);
        }
        return address;
    }

    public static void unmapUnaligned(final long address, final long size) throws IOException {
        try {
            // n must be used here
            final int n = (int) UNMAPP0_MH.invokeExact(address, size);
            MEMORY_MAPPED.addAndGet(-size);
        } catch (final Throwable e) {
            throw asAnIOException(e);
        }
    }

    public static IOException asAnIOException(final Throwable t) {
        Throwable e = t;
        if (e instanceof InvocationTargetException) {
            e = e.getCause();
        }
        if (e instanceof IOException) {
            return (IOException) e;
        }
        return new IOException(e);
    }

}
