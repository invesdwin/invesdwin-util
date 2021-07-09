package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.IFDateProvider;

@FunctionalInterface
public interface IHistoricalCacheExtractKeyProvider<V> {

    FDate extractKey(IFDateProvider key, V value);

}
