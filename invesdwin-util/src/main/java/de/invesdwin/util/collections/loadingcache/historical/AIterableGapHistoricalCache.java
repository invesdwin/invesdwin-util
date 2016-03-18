package de.invesdwin.util.collections.loadingcache.historical;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ASkippingIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class AIterableGapHistoricalCache<V> extends AGapHistoricalCache<V> {

    private ICloseableIterable<V> delegate;

    protected synchronized ICloseableIterable<V> getDelegate() {
        if (delegate == null) {
            this.delegate = WrapperCloseableIterable.maybeWrap(createDelegate());
        }
        return delegate;
    }

    protected abstract Iterable<V> createDelegate();

    @Override
    protected Iterable<V> readAllValuesAscendingFrom(final FDate key) {
        return new ASkippingIterable<V>(getDelegate()) {
            @Override
            protected boolean skip(final V element) {
                return extractKey(key, element).isBefore(key);
            }
        };
    }

    @Override
    protected abstract FDate innerExtractKey(final FDate key, final V value);

    @Override
    protected V readLatestValueFor(final FDate key) {
        V previousE = (V) null;
        for (final V e : getDelegate()) {
            if (previousE == null) {
                previousE = e;
            } else {
                final FDate eKey = extractKey(key, e);
                if (key.isAfter(eKey)) {
                    previousE = e;
                } else {
                    break;
                }
            }
        }
        return previousE;
    }

    @Override
    protected FDate innerCalculateNextKey(final FDate key) {

        for (final V value : getDelegate()) {
            final FDate valueKey = extractKey(key, value);

            if (valueKey.isAfter(key)) {
                return valueKey;
            }
        }

        return key;
    }

    @Override
    protected FDate innerCalculatePreviousKey(final FDate key) {
        return key.addMilliseconds(-1);
    }

}
