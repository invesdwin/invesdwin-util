package de.invesdwin.util.test;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

import de.invesdwin.util.lang.reflection.Reflections;

/**
 * Disables the Java module system restrictions ONCE per test JVM, before JUnit discovers or initializes anything.
 *
 * Without this the result depends on the filesystem scan order of the single shared surefire fork (forkCount=1,
 * reuseForks=true): the first test class that touches {@code de.invesdwin.util.streams.buffer.ByteBuffers} triggers the
 * {@code org.agrona.UnsafeApi} static initializer, which fails with {@code IllegalAccessError} while {@code java.base}
 * does not export {@code jdk.internal.misc} - and a failed class initializer is PERMANENT for the JVM, so every later
 * agrona-dependent test dies with {@code NoClassDefFoundError: Could not initialize class org.agrona.BufferUtil}. A
 * per-class {@code static { Reflections.disableJavaModuleSystemRestrictions(); }} block protects the JVM only if that
 * class happens to be initialized first, which is what made those suites a matter of order luck instead of green.
 *
 * The ServiceLoader instantiates this at Launcher creation, which precedes discovery, so the unlocks are in place
 * before any test class is loaded. It lives in {@code src/main/java} with its registration in
 * {@code META-INF/services/org.junit.platform.launcher.LauncherSessionListener} of the same jar - the same placement
 * this module already uses for {@code META-INF/services/org.junit.platform.engine.TestEngine} - which is what lets every
 * module that depends on invesdwin-util get it from the dependency instead of carrying a copy under its own
 * {@code src/test/java}. Nothing in a production run ever asks for a {@code LauncherSessionListener}, so the service is
 * only ever picked up when the JUnit platform launcher is on the classpath.
 *
 * The netty buffer knobs are deliberately not here: they belong to this module's own buffer tests and are set there, so
 * no other module's test JVM inherits an allocation policy it never asked for.
 */
@NotThreadSafe
public class TestJavaModuleRestrictionsListener implements LauncherSessionListener {

    public TestJavaModuleRestrictionsListener() {
        Reflections.disableJavaModuleSystemRestrictions();
    }

    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        Reflections.disableJavaModuleSystemRestrictions();
    }

}
