package de.invesdwin.util.collections.loadingcache.historical;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class AIterableGapHistoricalCache<V> extends AGapHistoricalCache<V> {

    @Override
    protected List<V> readAllValuesAscendingFrom(final FDate key) {
        final List<V> list = new ArrayList<V>();
        for (final V e : getIterable()) {
            final FDate eKey = extractKey(e);
            if (!eKey.isBefore(key)) {
                list.add(e);
            }
        }
        return list;
    }

    @Override
    protected final FDate innerExtractKey(final FDate key, final V value) {
        return extractKey(value);
    }

    @Override
    protected V readLatestValueFor(final FDate key) {
        V previousE = (V) null;
        for (final V e : getIterable()) {
            if (previousE == null) {
                previousE = e;
            } else {
                final FDate eKey = extractKey(e);
                if (key.isAfter(eKey)) {
                    previousE = e;
                } else {
                    break;
                }
            }
        }
        return previousE;
    }

    protected abstract Iterable<V> getIterable();

    protected abstract FDate extractKey(V value);

}
