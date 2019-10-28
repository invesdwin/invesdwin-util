package de.invesdwin.util.lang.finalizer;

import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.lang.finalizer.internal.FallbackFinalizerManagerProvider;
import de.invesdwin.util.lang.finalizer.internal.IFinalizerManagerProvider;
import de.invesdwin.util.lang.finalizer.internal.JavaFinalizerManagerProvider;
import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public final class FinalizerManager {

    private static final class ThreadLocalFinalizerReference implements IFinalizerReference, Runnable {
        @GuardedBy("this")
        private IFinalizerReference reference;
        @GuardedBy("this")
        private AFinalizer finalizer;

        private ThreadLocalFinalizerReference(final IFinalizerReference reference, final AFinalizer finalizer) {
            this.reference = reference;
            this.finalizer = finalizer;
            Assertions.checkTrue(THREAD_LOCAL_FINALIZERS.get().add(this));
        }

        @Override
        public synchronized void cleanReference() {
            if (reference != null) {
                finalizer = null;
                final Set<ThreadLocalFinalizerReference> set = THREAD_LOCAL_FINALIZERS.get();
                Assertions.checkTrue(set.remove(this));
                if (set.isEmpty()) {
                    THREAD_LOCAL_FINALIZERS.remove();
                }
                reference.cleanReference();
                reference = null;
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public synchronized void run() {
            if (finalizer != null) {
                finalizer.run();
            }
        }
    }

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(FinalizerManager.class);

    private static final IFinalizerManagerProvider PROVIDER;

    private static final FastThreadLocal<Set<ThreadLocalFinalizerReference>> THREAD_LOCAL_FINALIZERS = new FastThreadLocal<Set<ThreadLocalFinalizerReference>>() {
        @Override
        protected Set<ThreadLocalFinalizerReference> initialValue() throws Exception {
            return ILockCollectionFactory.getInstance(false).newIdentitySet();
        }

        @Override
        protected void onRemoval(final Set<ThreadLocalFinalizerReference> value) throws Exception {
            //thread context changed, call finalizers
            for (final ThreadLocalFinalizerReference finalizer : value) {
                finalizer.run();
            }
        }
    };

    static {
        if (Reflections.classExists(IFinalizerManagerProvider.JAVA_CLEANER_CLASS)) {
            PROVIDER = new JavaFinalizerManagerProvider();
        } else {
            PROVIDER = new FallbackFinalizerManagerProvider();
        }
    }

    private FinalizerManager() {}

    public static IFinalizerReference register(final Object obj, final AFinalizer finalizer) {
        if (Throwables.isDebugStackTraceEnabled()) {
            try {
                Reflections.assertObjectNotReferenced(obj, finalizer);
            } catch (final Throwable t) {
                LOG.error(Throwables.getFullStackTrace(t));
                throw t;
            }
        }
        final IFinalizerReference reference = PROVIDER.register(obj, finalizer);
        if (finalizer.isThreadLocal()) {
            return new ThreadLocalFinalizerReference(reference, finalizer);
        } else {
            return reference;
        }
    }

}
