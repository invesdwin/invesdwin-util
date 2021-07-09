package de.invesdwin.util.collections.loadingcache.historical;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ASkippingIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.time.date.FDate;

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
                return extractKey(null, element).isBefore(key);
            }
        };
    }

    @Override
    protected abstract FDate innerExtractKey(V value);

    @Override
    protected V readLatestValueFor(final FDate key) {
        V previousE = null;
        try (ICloseableIterator<V> delegate = getDelegate().iterator()) {
            while (true) {
                final V e = delegate.next();
                if (previousE == null) {
                    previousE = e;
                } else {
                    final FDate eKey = extractKey(null, e);
                    if (key.isAfter(eKey)) {
                        previousE = e;
                    } else {
                        break;
                    }
                }
            }
            return previousE;
        } catch (final NoSuchElementException e) {
            return previousE;
        }
    }

    @Override
    protected FDate innerCalculateNextKey(final FDate key) {
        try (ICloseableIterator<V> delegate = getDelegate().iterator()) {
            while (true) {
                final V value = delegate.next();
                final FDate valueKey = extractKey(null, value);

                if (valueKey.isAfter(key)) {
                    return valueKey;
                }
            }
        } catch (final NoSuchElementException e) {
            return key;
        }
    }

    @Override
    protected FDate innerCalculatePreviousKey(final FDate key) {
        return key.addMilliseconds(-1);
    }

}
