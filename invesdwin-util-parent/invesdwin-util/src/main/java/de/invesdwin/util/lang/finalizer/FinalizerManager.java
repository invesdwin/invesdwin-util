package de.invesdwin.util.lang.finalizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.reference.persistent.ACompressingWeakReference;
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

    private static final FastThreadLocal<List<WeakThreadLocalFinalizerReference>> THREAD_LOCAL_FINALIZERS = new FastThreadLocal<List<WeakThreadLocalFinalizerReference>>() {
        @Override
        protected List<WeakThreadLocalFinalizerReference> initialValue() throws Exception {
            //garbage collector thread accesses this too
            return Collections.synchronizedList(new ArrayList<WeakThreadLocalFinalizerReference>());
        }

        @Override
        protected void onRemoval(final List<WeakThreadLocalFinalizerReference> value) throws Exception {
            //thread context changed, call finalizers
            try {
                for (int i = 0; i < value.size(); i++) {
                    final WeakThreadLocalFinalizerReference v = value.get(i);
                    if (v != null) {
                        final ThreadLocalFinalizerReference reference = v.get();
                        if (reference != null) {
                            reference.run();
                        }
                    }
                }
            } catch (final ArrayIndexOutOfBoundsException e) {
                //end reached, might happen due to compressing reference removing last element
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

    private static final class WeakThreadLocalFinalizerReference
            extends ACompressingWeakReference<ThreadLocalFinalizerReference, Void> {

        private final Collection<WeakThreadLocalFinalizerReference> collection;

        private WeakThreadLocalFinalizerReference(final Collection<WeakThreadLocalFinalizerReference> collection,
                final ThreadLocalFinalizerReference referent) {
            super(referent);
            this.collection = collection;
        }

        @Override
        protected Void toCompressed(final ThreadLocalFinalizerReference referent) throws Exception {
            collection.remove(this);
            return null;
        }

        @Override
        protected ThreadLocalFinalizerReference fromCompressed(final Void compressed) throws Exception {
            return null;
        }

    }

    private static final class ThreadLocalFinalizerReference implements IFinalizerReference, Runnable {
        private final WeakThreadLocalFinalizerReference weakReference;
        @GuardedBy("finalizer")
        private IFinalizerReference reference;
        @GuardedBy("finalizer")
        private AFinalizer finalizer;

        private ThreadLocalFinalizerReference(final AFinalizer finalizer) {
            this.finalizer = finalizer;
            final Collection<WeakThreadLocalFinalizerReference> collection = THREAD_LOCAL_FINALIZERS.get();
            this.weakReference = new WeakThreadLocalFinalizerReference(collection, this);
            collection.add(weakReference);
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
            final Collection<WeakThreadLocalFinalizerReference> collection = THREAD_LOCAL_FINALIZERS.get();
            collection.remove(weakReference);
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
