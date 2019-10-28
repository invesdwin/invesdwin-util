package de.invesdwin.util.lang.finalizer;

import java.lang.ref.WeakReference;
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

    private static final class ThreadLocalFinalizerReference implements IFinalizerReference, Runnable {
        @GuardedBy("this")
        private WeakReference<IFinalizerReference> referenceRef;
        @GuardedBy("this")
        private WeakReference<AFinalizer> finalizerRef;

        private ThreadLocalFinalizerReference(final IFinalizerReference reference, final AFinalizer finalizer) {
            this.referenceRef = new WeakReference<IFinalizerReference>(reference);
            this.finalizerRef = new WeakReference<AFinalizer>(finalizer);
            Assertions.checkTrue(THREAD_LOCAL_FINALIZERS.get().add(this));
        }

        @Override
        public synchronized void cleanReference() {
            if (referenceRef != null) {
                cleanReferenceLocked();
            }
        }

        private void cleanReferenceLocked() {
            finalizerRef = null;
            Assertions.checkTrue(THREAD_LOCAL_FINALIZERS.get().remove(this));
            final IFinalizerReference reference = referenceRef.get();
            if (reference != null) {
                reference.cleanReference();
            }
            referenceRef = null;
        }

        @SuppressWarnings("deprecation")
        @Override
        public synchronized void run() {
            if (finalizerRef != null) {
                final AFinalizer finalizer = finalizerRef.get();
                if (finalizer != null) {
                    finalizer.run();
                }
                cleanReferenceLocked();
            }
        }
    }

}
