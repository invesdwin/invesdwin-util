package de.invesdwin.util.collections.loadingcache.historical;

import java.util.Map.Entry;

import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalEntry<V> extends Entry<FDate, V> {

    @Override
    default V setValue(final V value) {
        throw new UnsupportedOperationException();
    }

}
