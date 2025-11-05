package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledHistoricalCachePreviousKeysQueryInterceptor
        extends HistoricalCachePreviousKeysQueryInterceptorSupport {

    private static final DisabledHistoricalCachePreviousKeysQueryInterceptor INSTANCE = new DisabledHistoricalCachePreviousKeysQueryInterceptor();

    private DisabledHistoricalCachePreviousKeysQueryInterceptor() {}

    public static DisabledHistoricalCachePreviousKeysQueryInterceptor getInstance() {
        return INSTANCE;
    }

}
