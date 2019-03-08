package de.invesdwin.util.collections.loadingcache.historical;

import java.util.Map.Entry;

import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalEntry<V> extends Entry<FDate, V> {

    @Override
    default V setValue(final V value) {
        throw new UnsupportedOperationException();
    }

    static <V> V unwrapEntryValue(final IHistoricalEntry<V> entry) {
        if (entry == null) {
            return null;
        } else {
            return entry.getValue();
        }
    }

    static <V> FDate unwrapEntryKey(final IHistoricalEntry<V> entry) {
        if (entry == null) {
            return null;
        } else {
            return entry.getKey(); //internalAssertValue already has made sure that the entry key is the valueKey
        }
    }

}
