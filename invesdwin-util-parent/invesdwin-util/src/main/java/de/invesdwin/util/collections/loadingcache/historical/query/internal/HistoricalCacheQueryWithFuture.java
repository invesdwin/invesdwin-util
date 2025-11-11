package de.invesdwin.util.collections.loadingcache.historical.query.internal;

import java.util.Optional;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.skip.ASkippingIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.DisabledHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public class HistoricalCacheQueryWithFuture<V> extends HistoricalCacheQuery<V>
        implements IHistoricalCacheQueryWithFuture<V> {

    public HistoricalCacheQueryWithFuture(final IHistoricalCacheInternalMethods<V> internalMethods) {
        super(internalMethods);
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> setElementFilter(
            final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        return (IHistoricalCacheQueryWithFuture<V>) super.setElementFilter(elementFilter);
    }

    @Deprecated
    @Override
    public IHistoricalCacheQueryWithFuture<V> setFutureNullEnabled() {
        throw new IllegalStateException(
                "withFuture() has already been called. Please create a new query for this and call withFutureNull() directly");
    }

    @Override
    public V getValue(final FDate key) {
        return super.getValue(key);
    }

    @Override
    public final FDate getNextKey(final FDate key, final int shiftForwardUnits) {
        final IHistoricalCacheQuery<?> interceptor = getKeysQueryInterceptor();
        if (interceptor != null) {
            return interceptor.setFutureEnabled().getNextKey(key, shiftForwardUnits);
        }
        assertShiftUnitsPositive(shiftForwardUnits);
        return IHistoricalEntry.unwrapEntryKey(getNextEntry(key, shiftForwardUnits));
    }

    @Override
    public ICloseableIterable<FDate> getNextKeys(final FDate key, final int shiftForwardUnits) {
        final IHistoricalCacheQuery<?> interceptor = getKeysQueryInterceptor();
        if (interceptor != null) {
            return interceptor.setFutureEnabled().getNextKeys(key, shiftForwardUnits);
        }
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
        return new ICloseableIterable<FDate>() {
            @Override
            public ICloseableIterator<FDate> iterator() {
                return new ICloseableIterator<FDate>() {
                    private final ICloseableIterator<IHistoricalEntry<V>> nextEntries = getNextEntries(key,
                            shiftForwardUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return nextEntries.hasNext();
                    }

                    @Override
                    public FDate next() {
                        return nextEntries.next().getKey();
                    }

                    @Override
                    public void close() {
                        nextEntries.close();
                    }
                };
            }
        };
    }

    @Override
    public IHistoricalEntry<V> getNextEntry(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositive(shiftForwardUnits);
        final Optional<? extends IHistoricalEntry<V>> optionalInterceptor = internalMethods.getQueryCore()
                .getParent()
                .getNextQueryInterceptor()
                .getNextEntry(key, shiftForwardUnits);
        if (optionalInterceptor != null) {
            final IHistoricalCacheQueryElementFilter<V> elementFilter = getElementFilter();
            if (elementFilter == null || elementFilter instanceof DisabledHistoricalCacheQueryElementFilter) {
                return optionalInterceptor.orElse(null);
            } else {
                final IHistoricalEntry<V> element = optionalInterceptor.orElse(null);
                if (element == null) {
                    return null;
                } else if (!elementFilter.isValid(element.getKey(), element.getValue())) {
                    return null;
                } else {
                    return element;
                }
            }
        } else {
            return getNextEntryCached(key, shiftForwardUnits);
        }
    }

    @Override
    public V getNextValue(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositive(shiftForwardUnits);
        return IHistoricalEntry.unwrapEntryValue(getNextEntry(key, shiftForwardUnits));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
        final ICloseableIterable<? extends IHistoricalEntry<V>> iterableInterceptor = internalMethods.getQueryCore()
                .getParent()
                .getNextQueryInterceptor()
                .getNextEntries(key, shiftForwardUnits);
        if (iterableInterceptor != null) {
            final IHistoricalCacheQueryElementFilter<V> elementFilter = getElementFilter();
            if (elementFilter == null || elementFilter instanceof DisabledHistoricalCacheQueryElementFilter) {
                return (ICloseableIterable) iterableInterceptor;
            } else {
                return new ASkippingIterable<IHistoricalEntry<V>>(iterableInterceptor) {
                    @Override
                    protected boolean skip(final IHistoricalEntry<V> element) {
                        if (!elementFilter.isValid(element.getKey(), element.getValue())) {
                            throw FastNoSuchElementException.getInstance(
                                    "HistoricalCacheQuery: getNextEntries elementFilter found not valid element");
                        }
                        return false;
                    }
                };
            }
        } else {
            return getNextEntriesCached(key, shiftForwardUnits);
        }
    }

    @Override
    public ICloseableIterable<V> getNextValues(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {
                    private final ICloseableIterator<IHistoricalEntry<V>> nextEntries = getNextEntries(key,
                            shiftForwardUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return nextEntries.hasNext();
                    }

                    @Override
                    public V next() {
                        return nextEntries.next().getValue();
                    }

                    @Override
                    public void close() {
                        nextEntries.close();
                    }
                };
            }
        };
    }

    @Override
    public final FDate getNextKeyCached(final FDate key, final int shiftForwardUnits) {
        final IHistoricalCacheQuery<?> interceptor = getKeysQueryInterceptor();
        if (interceptor != null) {
            return interceptor.setFutureEnabled().getNextKeyCached(key, shiftForwardUnits);
        }
        assertShiftUnitsPositive(shiftForwardUnits);
        return IHistoricalEntry.unwrapEntryKey(getNextEntryCached(key, shiftForwardUnits));
    }

    @Override
    public ICloseableIterable<FDate> getNextKeysCached(final FDate key, final int shiftForwardUnits) {
        final IHistoricalCacheQuery<?> interceptor = getKeysQueryInterceptor();
        if (interceptor != null) {
            return interceptor.setFutureEnabled().getNextKeysCached(key, shiftForwardUnits);
        }
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
        return new ICloseableIterable<FDate>() {
            @Override
            public ICloseableIterator<FDate> iterator() {
                return new ICloseableIterator<FDate>() {
                    private final ICloseableIterator<IHistoricalEntry<V>> nextEntries = getNextEntriesCached(key,
                            shiftForwardUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return nextEntries.hasNext();
                    }

                    @Override
                    public FDate next() {
                        return nextEntries.next().getKey();
                    }

                    @Override
                    public void close() {
                        nextEntries.close();
                    }
                };
            }
        };
    }

    @Override
    public IHistoricalEntry<V> getNextEntryCached(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositive(shiftForwardUnits);
        return internalMethods.getQueryCore().getNextEntry(this, key, shiftForwardUnits);
    }

    @Override
    public V getNextValueCached(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositive(shiftForwardUnits);
        return IHistoricalEntry.unwrapEntryValue(getNextEntryCached(key, shiftForwardUnits));
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getNextEntriesCached(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
        return internalMethods.getQueryCore().getNextEntries(this, key, shiftForwardUnits);
    }

    @Override
    public ICloseableIterable<V> getNextValuesCached(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {
                    private final ICloseableIterator<IHistoricalEntry<V>> nextEntries = getNextEntriesCached(key,
                            shiftForwardUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return nextEntries.hasNext();
                    }

                    @Override
                    public V next() {
                        return nextEntries.next().getValue();
                    }

                    @Override
                    public void close() {
                        nextEntries.close();
                    }
                };
            }
        };
    }

    @Override
    protected HistoricalCacheQueryWithFuture<V> newFutureQuery() {
        return this;
    }

}
