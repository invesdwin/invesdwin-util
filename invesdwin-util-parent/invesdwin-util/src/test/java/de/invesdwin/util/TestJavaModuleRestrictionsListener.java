package de.invesdwin.util;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

import de.invesdwin.util.lang.reflection.Reflections;

/**
 * Disables the Java module system restrictions ONCE per test JVM, before JUnit discovers or
 * initializes anything. Without this, the outcome depends on filesystem scan order in the single
 * shared surefire fork (forkCount=1, reuseForks=true): the first test class touching
 * {@code de.invesdwin.util.streams.buffer.ByteBuffers} triggers {@code org.agrona.UnsafeApi}
 * static init, which fails with {@code IllegalAccessError} when {@code java.base} does not export
 * {@code jdk.internal.misc} -- and a failed {@code <clinit>} is PERMANENT for the JVM, so every
 * later agrona-dependent test dies ({@code NoClassDefFoundError: Could not initialize class
 * org.agrona.BufferUtil}). A per-class {@code static { Reflections.disableJavaModuleSystem
 * Restrictions(); }} block (the {@code ByteBuffersTest.java:63} convention) only protects the JVM
 * if that class happens to run first.
 *
 * <p>
 * Registered via {@code META-INF/services/org.junit.platform.launcher.LauncherSessionListener};
 * the ServiceLoader instantiates it at Launcher creation, which precedes discovery. The existing
 * per-class blocks remain harmless ({@link Reflections#disableJavaModuleSystemRestrictions()} is
 * idempotent) and still protect runs that bypass the JUnit launcher.
 */
public class TestJavaModuleRestrictionsListener implements LauncherSessionListener {

    public TestJavaModuleRestrictionsListener() {
        disable();
    }

    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        disable();
    }

    private static void disable() {
        //CHECKSTYLE:OFF
        //netty knobs, migrated from the static blocks of ByteBuffersTest/MemoryBuffersTest/
        //AllocateByteArrayTest -- they must be set BEFORE netty's classes initialize, which no
        //per-class block could guarantee (only the first-initialized class won that race).
        System.setProperty("io.netty.tryReflectionSetAccessible", "true");
        System.setProperty("io.netty.uninitializedArrayAllocationThreshold", "1");
        //CHECKSTYLE:ON
        Reflections.disableJavaModuleSystemRestrictions();
    }

}
