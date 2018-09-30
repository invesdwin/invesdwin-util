package de.invesdwin.util.collections.loadingcache.historical;

public interface IHistoricalValue<V> {

    IHistoricalEntry<? extends V> asHistoricalEntry();

}
