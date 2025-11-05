package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledHistoricalCacheRangeQueryInterceptor<V>
        extends HistoricalCacheRangeQueryInterceptorSupport<V> {

    @SuppressWarnings("rawtypes")
    private static final DisabledHistoricalCacheRangeQueryInterceptor INSTANCE = new DisabledHistoricalCacheRangeQueryInterceptor<>();

    private DisabledHistoricalCacheRangeQueryInterceptor() {}

    @SuppressWarnings("unchecked")
    public static <T> DisabledHistoricalCacheRangeQueryInterceptor<T> getInstance() {
        return INSTANCE;
    }

}
