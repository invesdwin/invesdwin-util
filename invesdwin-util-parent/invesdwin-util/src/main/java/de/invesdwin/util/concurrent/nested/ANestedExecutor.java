package de.invesdwin.util.concurrent.nested;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.lang.string.Strings;

@ThreadSafe
public abstract class ANestedExecutor implements INestedExecutor {

    private static final String NESTED_LEVEL_SEPARATOR = "_";
    private final ALoadingCache<String, WrappedExecutorService> nestedExecutor = new ALoadingCache<String, WrappedExecutorService>() {
        @Override
        protected boolean isHighConcurrency() {
            return true;
        }

        @Override
        protected WrappedExecutorService loadValue(final String key) {
            final WrappedExecutorService executor = newNestedExecutor(name + NESTED_LEVEL_SEPARATOR + key);
            Assertions.checkTrue(executor.isDynamicThreadName(),
                    "%s does not work correctly without dynamicThreadNames", ANestedExecutor.class.getSimpleName());
            return executor;
        }

    };
    private final String name;

    public ANestedExecutor(final String name) {
        if (Strings.isBlankOrNullText(name)) {
            throw new NullPointerException("name should not be blank or null: " + name);
        }
        this.name = name;
    }

    @Override
    public final WrappedExecutorService getNestedExecutor() {
        return nestedExecutor.get(String.valueOf(getCurrentNestedThreadLevel()));
    }

    @Override
    public final WrappedExecutorService getNestedExecutor(final String suffix) {
        return nestedExecutor.get(getCurrentNestedThreadLevel() + NESTED_LEVEL_SEPARATOR + suffix);
    }

    @Override
    public int getCurrentNestedThreadLevel() {
        return Threads.getCurrentNestedThreadLevel(name + NESTED_LEVEL_SEPARATOR);
    }

    protected abstract WrappedExecutorService newNestedExecutor(String nestedName);

    @Override
    public void close() {
        if (!nestedExecutor.isEmpty()) {
            for (final WrappedExecutorService executor : nestedExecutor.values()) {
                executor.shutdownNow();
            }
            nestedExecutor.clear();
        }
    }
}
