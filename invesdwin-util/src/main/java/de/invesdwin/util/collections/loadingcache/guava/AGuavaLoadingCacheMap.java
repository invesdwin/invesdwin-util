package de.invesdwin.util.collections.loadingcache.guava;

import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.ADelegateMap;
import de.invesdwin.util.error.Throwables;

@ThreadSafe
public abstract class AGuavaLoadingCacheMap<K, V> extends ADelegateMap<K, V> {

    public static final String RECURSIVE_LOAD_ILLEGAL_STATE_EXCEPTION_TEXT = "recursive load";

    @Override
    protected final Map<K, V> newDelegate() {
        return getConfig().newMap(this);
    }

    /**
     * May be overwritten to reconfigure the cache.
     * 
     * It is unknown if the calculations should only be kept temporarily. Such an assumption here would cause problems
     * elsewhere, thus we don't do eviction per default.
     * 
     * Null may be returned by this.
     */
    protected GuavaLoadingCacheMapConfig getConfig() {
        return new GuavaLoadingCacheMapConfig();
    }

    protected abstract V loadValue(K key);

    @Override
    public V get(final Object key) {
        try {
            return super.get(key);
        } catch (final Throwable e) {
            if (isRecursiveLoadException(e)) {
                //ignore
                return null;
            } else {
                throw Throwables.propagate(e);
            }
        }
    }

    private static boolean isRecursiveLoadException(final Throwable e) {
        //maybe key == valueKey, then recursive load happens which we ignore on first iteration
        final IllegalStateException illegalStateExc = Throwables.getCauseByType(e, IllegalStateException.class);
        return illegalStateExc != null && AGuavaLoadingCacheMap.RECURSIVE_LOAD_ILLEGAL_STATE_EXCEPTION_TEXT
                .equalsIgnoreCase(illegalStateExc.getMessage());
    }

}
