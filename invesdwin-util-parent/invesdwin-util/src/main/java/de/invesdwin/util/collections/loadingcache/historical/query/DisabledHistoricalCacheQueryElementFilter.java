package de.invesdwin.util.collections.loadingcache.historical.query;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class DisabledHistoricalCacheQueryElementFilter<V> implements IHistoricalCacheQueryElementFilter<V> {

    @SuppressWarnings("rawtypes")
    private static final DisabledHistoricalCacheQueryElementFilter INSTANCE = new DisabledHistoricalCacheQueryElementFilter<>();

    private DisabledHistoricalCacheQueryElementFilter() {}

    @Override
    public boolean isValid(final FDate valueKey, final V value) {
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T> DisabledHistoricalCacheQueryElementFilter<T> getInstance() {
        return INSTANCE;
    }

}
