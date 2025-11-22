package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledHistoricalCacheNextQueryInterceptor<V>
        extends HistoricalCacheNextQueryInterceptorSupport<V> {

    @SuppressWarnings("rawtypes")
    private static final DisabledHistoricalCacheNextQueryInterceptor INSTANCE = new DisabledHistoricalCacheNextQueryInterceptor<>();

    private DisabledHistoricalCacheNextQueryInterceptor() {}

    @SuppressWarnings("unchecked")
    public static <T> DisabledHistoricalCacheNextQueryInterceptor<T> getInstance() {
        return INSTANCE;
    }

}
