package de.invesdwin.util.collections.loadingcache.historical.interceptor;

import de.invesdwin.util.time.date.FDate;

public interface IHistoricalCacheSizeQueryInterceptor {

    /**
     * Return Longs.MIN_VALUE to skip/disable the interceptor.
     */
    long size(FDate from, FDate to);

}
