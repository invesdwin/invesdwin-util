package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import de.invesdwin.util.time.fdate.FDate;

@FunctionalInterface
public interface IHistoricalCacheExtractKeyProvider<V> {

    FDate extractKey(FDate key, V value);

}
