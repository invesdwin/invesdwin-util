package de.invesdwin.util.test;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

/**
 * The netty buffer knobs of this module's own buffer tests, set before any netty class initializes.
 *
 * Migrated from the static blocks of {@code ByteBuffersTest}, {@code MemoryBuffersTest} and
 * {@code AllocateByteArrayTest}: a per-class block only wins that race if its class is initialized first, and netty
 * reads both values once during class initialization. {@code tryReflectionSetAccessible} lets netty take the same
 * reflective path the tests exercise, and {@code uninitializedArrayAllocationThreshold} of 1 makes netty hand out
 * uninitialized arrays so the buffer tests see the allocation behaviour they assert on instead of netty's default
 * zero-filled arrays.
 *
 * This is registered only under {@code src/test/java} of this module, deliberately: it is a statement about how
 * invesdwin-util's buffer tests want netty configured, not a fleet-wide default, so no other module's test JVM inherits
 * it. The module system unlocks every module needs are {@link TestJavaModuleRestrictionsListener}, shipped from this
 * module's main jar.
 */
@NotThreadSafe
public class TestNettyBufferAllocationListener implements LauncherSessionListener {

    public TestNettyBufferAllocationListener() {
        disable();
    }

    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        disable();
    }

    private static void disable() {
        //CHECKSTYLE:OFF
        System.setProperty("io.netty.tryReflectionSetAccessible", "true");
        System.setProperty("io.netty.uninitializedArrayAllocationThreshold", "1");
        //CHECKSTYLE:ON
    }

}
