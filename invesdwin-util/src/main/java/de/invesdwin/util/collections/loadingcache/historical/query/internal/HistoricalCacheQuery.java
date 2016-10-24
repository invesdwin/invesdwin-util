package de.invesdwin.util.collections.loadingcache.historical.query.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Optional;

import de.invesdwin.util.collections.ADelegateList;
import de.invesdwin.util.collections.Lists;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryInternalMethods;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class HistoricalCacheQuery<V> implements IHistoricalCacheQuery<V> {

    private static final HistoricalCacheAssertValue DEFAULT_ASSERT_VALUE = HistoricalCacheAssertValue.ASSERT_VALUE_WITHOUT_FUTURE;
    protected HistoricalCacheAssertValue assertValue = DEFAULT_ASSERT_VALUE;
    protected final IHistoricalCacheQueryCore<V> core;
    protected final IHistoricalCacheQueryInternalMethods<V> internalMethods = new IHistoricalCacheQueryInternalMethods<V>() {

        @Override
        public boolean isRememberNullValue() {
            return rememberNullValue;
        }

        @Override
        public HistoricalCacheAssertValue getAssertValue() {
            return assertValue;
        }

        @Override
        public IHistoricalCacheQueryElementFilter<V> getElementFilter() {
            return elementFilter;
        }

        @Override
        public List<Entry<FDate, V>> newEntriesList(final int size) {
            return HistoricalCacheQuery.this.newEntriesList(size);
        }

    };
    private boolean filterDuplicateKeys = true;
    private boolean rememberNullValue = false;
    private IHistoricalCacheQueryElementFilter<V> elementFilter;

    public HistoricalCacheQuery(final IHistoricalCacheQueryCore<V> core) {
        this.core = core;
    }

    @Override
    public IHistoricalCacheQuery<V> withElementFilter(final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        if (elementFilter == null) {
            this.elementFilter = null;
        } else {
            this.elementFilter = elementFilter;
        }
        return this;
    }

    @Override
    public IHistoricalCacheQuery<V> withFilterDuplicateKeys(final boolean filterDuplicateKeys) {
        this.filterDuplicateKeys = filterDuplicateKeys;
        return this;
    }

    @Override
    public IHistoricalCacheQuery<V> withRememberNullValue(final boolean rememberNullValue) {
        this.rememberNullValue = rememberNullValue;
        return this;
    }

    /**
     * Prevents exeption on future value and instead returns null.
     */
    @Override
    public IHistoricalCacheQuery<V> withFutureNull() {
        checkAssertValueUnchangedAndSet(HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE_NULL);
        return this;
    }

    /**
     * Allows values from future.
     */
    @Override
    public final IHistoricalCacheQueryWithFuture<V> withFuture() {
        checkAssertValueUnchangedAndSet(HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE);
        return newFutureQuery();
    }

    @Override
    public final Entry<FDate, V> getEntry(final FDate key) {
        return core.getEntry(internalMethods, key, assertValue);
    }

    @Override
    public V getValue(final FDate key) {
        return core.getValue(internalMethods, key, assertValue);
    }

    @Override
    public final ICloseableIterable<Entry<FDate, V>> getEntries(final Iterable<FDate> keys) {
        return getEntries(keys, assertValue);
    }

    @Override
    public final ICloseableIterable<V> getValues(final Iterable<FDate> keys) {
        return getValues(keys, assertValue);
    }

    /**
     * If a key returns null, it will get skipped.
     */
    protected final ICloseableIterable<Entry<FDate, V>> getEntries(final Iterable<FDate> keys,
            final HistoricalCacheAssertValue assertValue) {
        return new ICloseableIterable<Entry<FDate, V>>() {
            @Override
            public ICloseableIterator<Entry<FDate, V>> iterator() {
                return new ICloseableIterator<Entry<FDate, V>>() {
                    private final ICloseableIterator<FDate> keysIterator = WrapperCloseableIterator
                            .maybeWrap(keys.iterator());

                    @Override
                    public boolean hasNext() {
                        return keysIterator.hasNext();
                    }

                    @Override
                    public Entry<FDate, V> next() {
                        return core.getEntry(internalMethods, keysIterator.next(), assertValue);
                    }

                    @Override
                    public void close() {
                        keysIterator.close();
                    }
                };
            }
        };
    }

    protected final ICloseableIterable<V> getValues(final Iterable<FDate> keys,
            final HistoricalCacheAssertValue assertValue) {
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {
                    private final ICloseableIterator<Entry<FDate, V>> entriesIterator = getEntries(keys, assertValue)
                            .iterator();

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return HistoricalCacheAssertValue.unwrapEntryValue(entriesIterator.next());
                    }

                    @Override
                    public void close() {
                        entriesIterator.close();
                    }
                };
            }
        };
    }

    @Override
    public FDate getKey(final FDate key) {
        if (key == null) {
            return null;
        }
        return HistoricalCacheAssertValue.unwrapEntryKey(core.getParent(),
                core.getEntry(internalMethods, key, assertValue));
    }

    @Override
    public final FDate getPreviousKey(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositive(shiftBackUnits);
        return HistoricalCacheAssertValue.unwrapEntryKey(core.getParent(), getPreviousEntry(key, shiftBackUnits));
    }

    /**
     * Skips null values for keys.
     * 
     * Fills the list with keys from the past.
     */
    @Override
    public final ICloseableIterable<FDate> getPreviousKeys(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositiveNonZero(shiftBackUnits);
        return new ICloseableIterable<FDate>() {
            @Override
            public ICloseableIterator<FDate> iterator() {
                return new ICloseableIterator<FDate>() {
                    private final ICloseableIterator<Entry<FDate, V>> previousEntries = getPreviousEntries(key,
                            shiftBackUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return previousEntries.hasNext();
                    }

                    @Override
                    public FDate next() {
                        return HistoricalCacheAssertValue.unwrapEntryKey(core.getParent(), previousEntries.next());
                    }

                    @Override
                    public void close() {
                        previousEntries.close();
                    }
                };
            }

        };
    }

    @Override
    public final V getPreviousValue(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositive(shiftBackUnits);
        return HistoricalCacheAssertValue.unwrapEntryValue(getPreviousEntry(key, shiftBackUnits));
    }

    @Override
    public final ICloseableIterable<V> getPreviousValues(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositiveNonZero(shiftBackUnits);
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {
                    private final ICloseableIterator<Entry<FDate, V>> previousEntries = getPreviousEntries(key,
                            shiftBackUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return previousEntries.hasNext();
                    }

                    @Override
                    public V next() {
                        return HistoricalCacheAssertValue.unwrapEntryValue(previousEntries.next());
                    }

                    @Override
                    public void close() {
                        previousEntries.close();
                    }
                };
            }

        };
    }

    protected void assertShiftUnitsPositiveNonZero(final int shiftUnits) {
        if (shiftUnits <= 0) {
            throw new IllegalArgumentException("shiftUnits needs to be a positive non zero value: " + shiftUnits);
        }
    }

    protected void assertShiftUnitsPositive(final int shiftUnits) {
        if (shiftUnits < 0) {
            throw new IllegalArgumentException("shiftUnits needs to be a positive value: " + shiftUnits);
        }
    }

    /**
     * Returns all keys in the given time range.
     */
    @Override
    public ICloseableIterable<FDate> getKeys(final FDate from, final FDate to) {
        final ICloseableIterable<FDate> interceptor = core.getParent().getQueryInterceptor().getKeys(from, to);
        if (interceptor != null) {
            return interceptor;
        } else {
            return new ICloseableIterable<FDate>() {
                @Override
                public ICloseableIterator<FDate> iterator() {
                    return new ICloseableIterator<FDate>() {

                        private final ICloseableIterator<Entry<FDate, V>> entriesIterator = getEntries(from, to)
                                .iterator();

                        @Override
                        public boolean hasNext() {
                            return entriesIterator.hasNext();
                        }

                        @Override
                        public FDate next() {
                            return HistoricalCacheAssertValue.unwrapEntryKey(core.getParent(), entriesIterator.next());
                        }

                        @Override
                        public void close() {
                            entriesIterator.close();
                        }

                    };
                }
            };
        }
    }

    /**
     * Returns all values in the given time range.
     */
    @Override
    public ICloseableIterable<Entry<FDate, V>> getEntries(final FDate from, final FDate to) {
        final ICloseableIterable<Entry<FDate, V>> interceptor = core.getParent().getQueryInterceptor().getEntries(from,
                to);
        if (interceptor != null) {
            return interceptor;
        } else {
            return new ICloseableIterable<Entry<FDate, V>>() {
                @Override
                public ICloseableIterator<Entry<FDate, V>> iterator() {
                    return new ICloseableIterator<Entry<FDate, V>>() {
                        private final IHistoricalCacheQueryWithFuture<V> future = withFuture();
                        private Entry<FDate, V> nextEntry = future.getNextEntry(from, 0);
                        private FDate nextEntryKey = extractKey(nextEntry);

                        @Override
                        public boolean hasNext() {
                            return nextEntryKey != null && !nextEntryKey.isAfter(to);
                        }

                        private FDate extractKey(final Entry<FDate, V> e) {
                            return HistoricalCacheAssertValue.unwrapEntryKey(core.getParent(), e);
                        }

                        @Override
                        public Entry<FDate, V> next() {
                            if (hasNext()) {
                                //always returning current and reading ahead once
                                final Entry<FDate, V> currentEntry = nextEntry;
                                final FDate currentEntryKey = extractKey(currentEntry);
                                nextEntry = future.getNextEntry(currentEntryKey, 1);
                                nextEntryKey = extractKey(nextEntry);
                                if (nextEntry != null && !nextEntryKey.isAfter(currentEntryKey)) {
                                    nextEntry = null;
                                    nextEntryKey = null;
                                }
                                return currentEntry;
                            } else {
                                throw new NoSuchElementException();
                            }
                        }

                        @Override
                        public void close() {
                            nextEntry = null;
                            nextEntryKey = null;
                        }

                    };
                }
            };
        }
    }

    @Override
    public ICloseableIterable<V> getValues(final FDate from, final FDate to) {
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {

                    private final ICloseableIterator<Entry<FDate, V>> entriesIterator = getEntries(from, to).iterator();

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return HistoricalCacheAssertValue.unwrapEntryValue(entriesIterator.next());
                    }

                    @Override
                    public void close() {
                        entriesIterator.close();
                    }

                };
            }
        };
    }

    @Override
    public FDate getPreviousValueKeyBetween(final FDate from, final FDate to, final V value) {
        FDate curKey = to;
        final Optional<V> optionalValue = Optional.fromNullable(value);
        boolean firstTry = true;
        while (true) {
            final int shiftBackUnits;
            if (firstTry) {
                shiftBackUnits = 0;
            } else {
                shiftBackUnits = 1;
            }
            final Entry<FDate, V> previousEntry = getPreviousEntry(curKey, shiftBackUnits);
            if (previousEntry == null) {
                return null;
            }
            final FDate previousKey = core.getParent().extractKey(previousEntry.getKey(), previousEntry.getValue());
            if (!firstTry && !previousKey.isBefore(curKey)) {
                return null;
            }
            curKey = previousKey;
            if (curKey.isBefore(from)) {
                return null;
            }
            final Optional<V> optionalCurValue = Optional.fromNullable(previousEntry.getValue());
            if (optionalValue.equals(optionalCurValue)) {
                return curKey;
            }
            firstTry = false;
        }
    }

    private void copyQuerySettings(final HistoricalCacheQuery<V> query) {
        query.assertValue = assertValue;
        query.filterDuplicateKeys = filterDuplicateKeys;
        query.elementFilter = elementFilter;
    }

    private HistoricalCacheQueryWithFuture<V> newFutureQuery() {
        final HistoricalCacheQueryWithFuture<V> query = new HistoricalCacheQueryWithFuture<V>(core);
        copyQuerySettings(query);
        return query;
    }

    protected final List<Entry<FDate, V>> newEntriesList(final int size) {
        if (filterDuplicateKeys) {
            /*
             * duplicates will only occur on the edged, never in the middle, so we can use this fast implementation
             */
            return new ADelegateList<Entry<FDate, V>>() {

                private Entry<FDate, V> minEntry;
                private Entry<FDate, V> maxEntry;

                @Override
                protected List<Entry<FDate, V>> newDelegate() {
                    return new ArrayList<Entry<FDate, V>>(size);
                }

                @Override
                protected Collection<Entry<FDate, V>> filterAllowedElements(
                        final Collection<? extends Entry<FDate, V>> c) {
                    if (c.isEmpty()) {
                        return Collections.emptyList();
                    }
                    final List<Entry<FDate, V>> cList = Lists.toList(c);
                    final int highestIndexFirst = 0;
                    final Entry<FDate, V> highestEntry = cList.get(highestIndexFirst);
                    final int lowestIndexLast = cList.size() - 1;
                    final Entry<FDate, V> lowestEntry = cList.get(lowestIndexLast);
                    if (highestEntry.getKey().isBefore(lowestEntry.getKey())) {
                        throw new IllegalArgumentException("Expecting desceding order: first[" + highestEntry.getKey()
                                + "] >= last[" + lowestEntry.getKey() + "]");
                    } else if (highestEntry.getKey().equals(lowestEntry.getKey())) {
                        final Entry<FDate, V> onlyEntry = cList.get(0);
                        if (isAddAllowed(onlyEntry)) {
                            @SuppressWarnings("unchecked")
                            final List<Entry<FDate, V>> onlyEntryList = Arrays.asList(onlyEntry);
                            return onlyEntryList;
                        } else {
                            return Collections.emptyList();
                        }
                    }

                    final Integer minIndex = determineMinIndex(cList, lowestEntry, highestIndexFirst, lowestIndexLast);
                    if (minIndex == null) {
                        return Collections.emptyList();
                    }

                    final Integer maxIndex = determineMaxIndex(cList, highestEntry, highestIndexFirst, lowestIndexLast);
                    if (maxIndex == null) {
                        return Collections.emptyList();
                    }
                    return cList.subList(maxIndex, minIndex + 1);
                }

                private Integer determineMaxIndex(final List<Entry<FDate, V>> cList, final Entry<FDate, V> highestEntry,
                        final int highestIndexFirst, final int lowestIndexLast) {
                    final boolean newMaxEntry;
                    Integer maxIndex = null;
                    if (maxEntry == null) {
                        maxEntry = highestEntry;
                        maxIndex = highestIndexFirst;
                        newMaxEntry = true;
                    } else {
                        newMaxEntry = false;
                    }
                    //from 0 until cList.size()-1
                    for (int i = highestIndexFirst; i < lowestIndexLast; i++) {
                        final Entry<FDate, V> curEntry = cList.get(i);
                        if (maxEntry.getKey().isAfter(curEntry.getKey())) {
                            maxIndex = i;
                            if (newMaxEntry) {
                                maxIndex--;
                            }
                            break;
                        }
                    }
                    return maxIndex;
                }

                private Integer determineMinIndex(final List<Entry<FDate, V>> cList, final Entry<FDate, V> lowestEntry,
                        final int highestIndexFirst, final int lowestIndexLast) {
                    final boolean newMinEntry;
                    Integer minIndex = null;
                    if (minEntry == null) {
                        minEntry = lowestEntry;
                        minIndex = lowestIndexLast;
                        newMinEntry = true;
                    } else {
                        newMinEntry = false;
                    }
                    //from cList.size()-1 until 0
                    //from lowstIndexLast until highestIndexFirst
                    for (int i = lowestIndexLast; i >= highestIndexFirst; i--) {
                        final Entry<FDate, V> curEntry = cList.get(i);
                        if (minEntry.getKey().isBefore(curEntry.getKey())) {
                            minIndex = i;
                            if (newMinEntry) {
                                minIndex++;
                            }
                            break;
                        }
                    }
                    return minIndex;
                }

                @Override
                public boolean isAddAllowed(final Entry<FDate, V> e) {
                    if (minEntry == null) {
                        minEntry = e;
                        maxEntry = e;
                        return true;
                    } else {
                        //we need to support reversal, thus doing identity check
                        if (e.getKey().equals(minEntry.getKey()) && e != minEntry
                                || e.getKey().equals(maxEntry.getKey()) && e != maxEntry) {
                            return false;
                        } else {
                            if (e.getKey().isBefore(minEntry.getKey())) {
                                minEntry = e;
                            } else if (e.getKey().isAfter(maxEntry.getKey())) {
                                maxEntry = e;
                            }
                            return true;
                        }
                    }
                }

            };
        } else {
            return new ArrayList<Entry<FDate, V>>(size);
        }
    }

    private void checkAssertValueUnchangedAndSet(final HistoricalCacheAssertValue newAssertValue) {
        if (newAssertValue != assertValue) {
            if (assertValue != DEFAULT_ASSERT_VALUE) {
                throw new IllegalStateException("Either withFuture() or withFutureNull() can be used, but not both!");
            }
            this.assertValue = newAssertValue;
        }
    }

    @Override
    public final Entry<FDate, V> getPreviousEntry(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositive(shiftBackUnits);
        return core.getPreviousEntry(internalMethods, key, shiftBackUnits);
    }

    /**
     * Skips null values for keys.
     * 
     * Fills the list with values from the past.
     */
    @Override
    public final ICloseableIterable<Entry<FDate, V>> getPreviousEntries(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositiveNonZero(shiftBackUnits);
        return core.getPreviousEntries(internalMethods, key, shiftBackUnits);
    }

}
