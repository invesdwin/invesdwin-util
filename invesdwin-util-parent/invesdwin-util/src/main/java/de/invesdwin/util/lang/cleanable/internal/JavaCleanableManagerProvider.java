package de.invesdwin.util.lang.cleanable.internal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadFactory;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.lang.cleanable.ACleanableAction;
import de.invesdwin.util.lang.cleanable.CleanableManager;
import de.invesdwin.util.lang.cleanable.ICleanableReference;

@ThreadSafe
public class JavaCleanableManagerProvider implements ICleanableManagerProvider {

    private static final Object CLEANER;
    private static final MethodHandle CLEANER_REGISTER_METHOD;
    private static final MethodHandle CLEANABLE_CLEAN_METHOD;

    static {
        try {
            final Class<Object> cleanerClass = Reflections.classForName(ICleanableManagerProvider.JAVA_CLEANER_CLASS);
            final Method registerMethod = cleanerClass.getMethod("register", Object.class, Runnable.class);
            final Lookup lookup = MethodHandles.lookup();
            CLEANER_REGISTER_METHOD = lookup.unreflect(registerMethod);

            final Method createMethod = cleanerClass.getMethod("create", ThreadFactory.class);

            final ThreadFactory threadFactory = Executors
                    .newFastThreadLocalThreadFactory(CleanableManager.class.getSimpleName());
            CLEANER = createMethod.invoke(null, threadFactory);

            final Class<Object> cleanableClass = Reflections.classForName("java.lang.ref.Cleaner.Cleanable");
            final Method cleanMethod = cleanableClass.getMethod("clean");
            CLEANABLE_CLEAN_METHOD = lookup.unreflect(cleanMethod);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final class CleanableInvoker implements ICleanableReference {

        private Object cleanable;

        private CleanableInvoker(final Object cleanable) {
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
    public ICleanableReference register(final Object obj, final ACleanableAction cleanableAction) {
        try {
            final Object cleanable = CLEANER_REGISTER_METHOD.invoke(CLEANER, obj, cleanableAction);
            return new CleanableInvoker(cleanable);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
