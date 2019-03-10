package de.invesdwin.util.lang.cleanable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.lang.cleanable.internal.FallbackCleanableManagerProvider;
import de.invesdwin.util.lang.cleanable.internal.ICleanableManagerProvider;
import de.invesdwin.util.lang.cleanable.internal.JavaCleanableManagerProvider;

@ThreadSafe
public final class CleanableManager {

    private static final ICleanableManagerProvider PROVIDER;

    static {
        if (Reflections.classExists(ICleanableManagerProvider.JAVA_CLEANER_CLASS)) {
            PROVIDER = new JavaCleanableManagerProvider();
        } else {
            PROVIDER = new FallbackCleanableManagerProvider();
        }
    }

    private CleanableManager() {}

    public static ICleanableReference register(final Object obj, final ACleanableAction cleanableAction) {
        return PROVIDER.register(obj, cleanableAction);
    }

}
