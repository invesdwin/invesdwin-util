package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.IFDateProvider;

@FunctionalInterface
public interface IHistoricalCacheExtractKeyProvider<V> {

    FDate extractKey(IFDateProvider key, V value);

}
