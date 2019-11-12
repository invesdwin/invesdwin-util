package de.invesdwin.util.lang.finalizer;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;

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
            final ConcurrentMap<ThreadLocalFinalizerReference, Boolean> slaveDatasetListeners = Caffeine.newBuilder()
                    .weakKeys()
                    .<ThreadLocalFinalizerReference, Boolean> build()
                    .asMap();
            return Collections.newSetFromMap(slaveDatasetListeners);
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
        if (finalizer.isThreadLocal()) {
            final ThreadLocalFinalizerReference threadLocal = new ThreadLocalFinalizerReference(finalizer);
            final IFinalizerReference reference = PROVIDER.register(obj, threadLocal);
            threadLocal.setReference(reference);
            return threadLocal;
        } else {
            final IFinalizerReference reference = PROVIDER.register(obj, finalizer);
            return reference;
        }
    }

    private static final class ThreadLocalFinalizerReference implements IFinalizerReference, Runnable {
        @GuardedBy("finalizer")
        private IFinalizerReference reference;
        @GuardedBy("finalizer")
        private AFinalizer finalizer;

        private ThreadLocalFinalizerReference(final AFinalizer finalizer) {
            this.finalizer = finalizer;
            THREAD_LOCAL_FINALIZERS.get().add(this);
        }

        //no need to synchronize here
        private void setReference(final IFinalizerReference reference) {
            this.reference = reference;
        }

        @Override
        public void cleanReference() {
            final AFinalizer sync = finalizer;
            if (sync != null) {
                synchronized (sync) {
                    if (finalizer != null) {
                        cleanReferenceLocked();
                    }
                }
            }
        }

        private void cleanReferenceLocked() {
            finalizer = null;
            THREAD_LOCAL_FINALIZERS.get().remove(this);
            reference.cleanReference();
            reference = null;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            final AFinalizer sync = finalizer;
            if (sync != null) {
                synchronized (sync) {
                    if (finalizer != null) {
                        finalizer.run();
                        cleanReferenceLocked();
                    }
                }
            }
        }
    }

}
