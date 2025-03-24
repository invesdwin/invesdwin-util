package de.invesdwin.util.collections.loadingcache.historical;

import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.time.date.FDate;

public interface IValuesMap<V> extends ILoadingCache<FDate, IHistoricalEntry<V>> {

    void putDirectly(FDate key, IHistoricalEntry<V> value);

}
