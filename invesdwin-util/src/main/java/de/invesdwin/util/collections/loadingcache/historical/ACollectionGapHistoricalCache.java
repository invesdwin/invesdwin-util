package de.invesdwin.util.collections.loadingcache.historical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class ACollectionGapHistoricalCache<V> extends AGapHistoricalCache<V> {

    private Collection<V> delegate;

    protected synchronized Collection<V> getDelegate() {
        if (delegate == null) {
            this.delegate = createDelegate();
        }
        return delegate;
    }

    /**
     * The collection needs to be ordered by time!
     */
    protected abstract Collection<V> createDelegate();

    @Override
    protected List<V> readAllValuesAscendingFrom(final FDate key) {
        return new ArrayList<V>(getDelegate());
    }

    @Override
    protected V readLatestValueFor(final FDate key) {
        V latestValue = (V) null;
        for (final V value : getDelegate()) {
            if (latestValue == null) {
                latestValue = value;
            } else if (!extractKey(key, value).isAfter(key)) {
                latestValue = value;
            } else {
                break;
            }
        }
        return latestValue;
    }

}
