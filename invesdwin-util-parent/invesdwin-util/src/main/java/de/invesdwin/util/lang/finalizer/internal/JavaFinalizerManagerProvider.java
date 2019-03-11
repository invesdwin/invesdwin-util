package de.invesdwin.util.lang.finalizer.internal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadFactory;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.finalizer.FinalizerManager;
import de.invesdwin.util.lang.finalizer.IFinalizerReference;

@ThreadSafe
public class JavaFinalizerManagerProvider implements IFinalizerManagerProvider {

    private static final Object CLEANER;
    private static final MethodHandle CLEANER_REGISTER_METHOD;
    private static final MethodHandle CLEANABLE_CLEAN_METHOD;

    static {
        try {
            final Class<Object> cleanerClass = Reflections.classForName(IFinalizerManagerProvider.JAVA_CLEANER_CLASS);
            final Method registerMethod = cleanerClass.getMethod("register", Object.class, Runnable.class);
            final Lookup lookup = MethodHandles.lookup();
            CLEANER_REGISTER_METHOD = lookup.unreflect(registerMethod);

            final Method createMethod = cleanerClass.getMethod("create", ThreadFactory.class);

            final ThreadFactory threadFactory = Executors
                    .newFastThreadLocalThreadFactory(FinalizerManager.class.getSimpleName());
            CLEANER = createMethod.invoke(null, threadFactory);

            final Class<Object> cleanableClass = Reflections.classForName("java.lang.ref.Cleaner.Cleanable");
            final Method cleanMethod = cleanableClass.getMethod("clean");
            CLEANABLE_CLEAN_METHOD = lookup.unreflect(cleanMethod);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final class FubakuterReference implements IFinalizerReference {

        private Object cleanable;

        private FubakuterReference(final Object cleanable) {
            Assertions.checkNotNull(cleanable);
            this.cleanable = cleanable;
        }

        @Override
        public void cleanReference() {
            if (cleanable != null) {
                try {
                    CLEANABLE_CLEAN_METHOD.invoke(cleanable);
                    cleanable = null;
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @Override
    public IFinalizerReference register(final Object obj, final AFinalizer cleanableAction) {
        try {
            final Object cleanable = CLEANER_REGISTER_METHOD.invoke(CLEANER, obj, cleanableAction);
            return new FubakuterReference(cleanable);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
