package de.invesdwin.util.concurrent;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.BooleanUtils;

import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.swing.EventDispatchThreadUtil;
import io.netty.util.concurrent.FastThreadLocal;

@Immutable
public final class RetryThreads {

    private static final FastThreadLocal<Boolean> THREAD_RETRY_DISABLED = new FastThreadLocal<>();
    @GuardedBy("explcitly not volatile since cached information per thread is fine")
    private static boolean registerThreadRetryDisabledUsed;

    private RetryThreads() {}

    public static boolean isThreadRetryDisabled() {
        if (registerThreadRetryDisabledUsed && Booleans.isTrue(THREAD_RETRY_DISABLED.get())) {
            return true;
        }
        return isThreadRetryDisabledDefault();
    }

    public static boolean isThreadRetryDisabledDefault() {
        return AFinalizer.isThreadFinalizerActive() || EventDispatchThreadUtil.isEventDispatchThread()
                || Threads.isInterrupted();
    }

    public static Boolean registerThreadRetryDisabled() {
        return registerThreadRetryDisabled(true);
    }

    public static Boolean registerThreadRetryDisabled(final boolean threadRetryDisabled) {
        final boolean threadRetryDisabledBefore = registerThreadRetryDisabledUsed
                && BooleanUtils.isTrue(THREAD_RETRY_DISABLED.get());
        if (threadRetryDisabledBefore != threadRetryDisabled) {
            THREAD_RETRY_DISABLED.set(threadRetryDisabled);
            registerThreadRetryDisabledUsed = true;
            return threadRetryDisabledBefore;
        } else {
            return null;
        }
    }

    public static void unregisterThreadRetryDisabled(final Boolean registerThreadRetryDisabled) {
        if (registerThreadRetryDisabled == null) {
            //nothing to do since we did not change anything
            return;
        }
        //restore before state
        if (!registerThreadRetryDisabled) {
            THREAD_RETRY_DISABLED.remove();
        } else {
            THREAD_RETRY_DISABLED.set(true);
        }
    }

}
