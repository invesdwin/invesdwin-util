package de.invesdwin.util.collections.loadingcache.historical.internal;

import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.time.fdate.FDate;

public interface IValuesMap<V> extends ILoadingCache<FDate, IHistoricalEntry<V>> {

    void putDirectly(FDate key, IHistoricalEntry<V> value);

}
