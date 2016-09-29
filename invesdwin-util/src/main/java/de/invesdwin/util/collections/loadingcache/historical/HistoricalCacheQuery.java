package de.invesdwin.util.collections.loadingcache.historical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Optional;

import de.invesdwin.util.bean.tuple.KeyIdentityEntry;
import de.invesdwin.util.collections.ADelegateSet;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterator;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class HistoricalCacheQuery<V> {

    private static final HistoricalCacheAssertValue DEFAULT_ASSERT_VALUE = HistoricalCacheAssertValue.ASSERT_VALUE_WITHOUT_FUTURE;
    protected HistoricalCacheAssertValue assertValue = DEFAULT_ASSERT_VALUE;
    protected final AHistoricalCache<V> parent;
    private boolean filterDuplicateKeys = true;
    private boolean rememberNullValue = false;
    private IHistoricalCacheQueryElementFilter<V> elementFilter;

    protected HistoricalCacheQuery(final AHistoricalCache<V> parent) {
        this.parent = parent;
    }

    public HistoricalCacheQuery<V> withElementFilter(final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        if (elementFilter == null) {
            this.elementFilter = null;
        } else {
            this.elementFilter = elementFilter;
        }
        return this;
    }

    /**
     * Default is true. Filters key and thus values that have already been added to the result list. Thus the result
     * list might contain less values than shiftBackUnits specified.
     */
    public HistoricalCacheQuery<V> withFilterDuplicateKeys(final boolean filterDuplicateKeys) {
        this.filterDuplicateKeys = filterDuplicateKeys;
        return this;
    }

    public HistoricalCacheQuery<V> withRememberNullValue(final boolean rememberNullValue) {
        this.rememberNullValue = rememberNullValue;
        return this;
    }

    /**
     * Prevents exeption on future value and instead returns null.
     */
    public final HistoricalCacheQuery<V> withFutureNull() {
        checkAssertValueUnchangedAndSet(HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE_NULL);
        return this;
    }

    /**
     * Allows values from future.
     */
    public final HistoricalCacheQueryWithFuture<V> withFuture() {
        checkAssertValueUnchangedAndSet(HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE);
        return newFutureQuery();
    }

    public final Entry<FDate, V> getEntry(final FDate key) {
        return getEntry(key, assertValue);
    }

    public V getValue(final FDate key) {
        return getValue(key, assertValue);
    }

    protected final Entry<FDate, V> getEntry(final FDate key, final HistoricalCacheAssertValue assertValue) {
        V value = parent.getValuesMap().get(key);
        if (!rememberNullValue && value == null) {
            final FDate adjKey = parent.getAdjustKeyProvider().adjustKey(key);
            parent.remove(adjKey);
        }
        if (value != null && elementFilter != null) {
            FDate curKey = parent.extractKey(key, value);
            while (!elementFilter.isValid(curKey, value)) {
                value = null;
                //try earlier dates to find a valid element
                final FDate previousKey = parent.calculatePreviousKey(curKey);
                final V previousValue = parent.getValuesMap().get(previousKey);
                if (previousValue == null) {
                    break;
                }
                final FDate previousValueKey = parent.extractKey(previousKey, previousValue);
                if (previousValueKey.equals(curKey)) {
                    break;
                }
                curKey = previousKey;
                value = previousValue;
            }
        }
        return assertValue.assertValue(parent, key, key, value);
    }

    protected final V getValue(final FDate key, final HistoricalCacheAssertValue assertValue) {
        if (key == null) {
            return (V) null;
        }
        return HistoricalCacheAssertValue.unwrapEntryValue(getEntry(key, assertValue));
    }

    public final ICloseableIterable<Entry<FDate, V>> getEntries(final Iterable<FDate> keys) {
        return getEntries(keys, assertValue);
    }

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
                        return getEntry(keysIterator.next(), assertValue);
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

    public FDate getKey(final FDate key) {
        if (key == null) {
            return null;
        }
        return HistoricalCacheAssertValue.unwrapEntryKey(parent, getEntry(key, assertValue));
    }

    /**
     * Jumps the specified shiftBackUnits to the past instead of only one unit. 0 results in current value.
     */
    public final FDate getPreviousKey(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositive(shiftBackUnits);
        return HistoricalCacheAssertValue.unwrapEntryKey(parent, getPreviousEntry(key, shiftBackUnits));
    }

    /**
     * Skips null values for keys.
     * 
     * Fills the list with keys from the past.
     */
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
                        return HistoricalCacheAssertValue.unwrapEntryKey(parent, previousEntries.next());
                    }

                    @Override
                    public void close() {
                        previousEntries.close();
                    }
                };
            }

        };
    }

    public final Entry<FDate, V> getPreviousEntry(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositive(shiftBackUnits);

        FDate previousKey = key;
        Entry<FDate, V> previousEntry = null;

        for (int i = 0; i <= shiftBackUnits; i++) {
            /*
             * if key of value == key, the same key would be returned on the next call
             * 
             * we decrement by one unit to get the previous key
             */

            final FDate previousPreviousKey;
            if (i == 0) {
                previousPreviousKey = previousKey;
            } else {
                previousPreviousKey = parent.calculatePreviousKey(previousKey);
            }
            if (previousPreviousKey == null) {
                break;
            }
            //the key of the value is the relevant one
            final Entry<FDate, V> previousPreviousEntry = assertValue.assertValue(parent, key, previousPreviousKey,
                    getValue(previousPreviousKey, HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE));
            if (previousPreviousEntry == null) {
                if (previousKey.equals(key)) {
                    return null;
                } else {
                    return previousEntry;
                }
            } else {
                final V previousValue = previousPreviousEntry.getValue();
                previousKey = parent.extractKey(previousPreviousKey, previousValue);
                previousEntry = previousPreviousEntry;
            }
        }
        return previousEntry;
    }

    public final V getPreviousValue(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositive(shiftBackUnits);
        return HistoricalCacheAssertValue.unwrapEntryValue(getPreviousEntry(key, shiftBackUnits));
    }

    /**
     * Skips null values for keys.
     * 
     * Fills the list with values from the past.
     */
    public final ICloseableIterable<Entry<FDate, V>> getPreviousEntries(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositiveNonZero(shiftBackUnits);
        //This has to work with lists internally to support FilterDuplicateKeys option
        final Collection<Entry<FDate, V>> trailing = newEntriesCollection();
        //With 10 units this iterates from 9 to 0
        //to go from past to future so that queries get minimized
        //only on the first call we risk multiple queries
        for (int unitsBack = shiftBackUnits - 1; unitsBack >= 0; unitsBack--) {
            final Entry<FDate, V> previousEntry = getPreviousEntry(key, unitsBack);
            if (previousEntry != null) {
                trailing.add(previousEntry);
            }
        }
        return WrapperCloseableIterable.maybeWrap(trailing);
    }

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
    public ICloseableIterable<FDate> getKeys(final FDate from, final FDate to) {
        final ICloseableIterable<FDate> interceptor = parent.getQueryInterceptor().getKeys(from, to);
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
                            return HistoricalCacheAssertValue.unwrapEntryKey(parent, entriesIterator.next());
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
    public ICloseableIterable<Entry<FDate, V>> getEntries(final FDate from, final FDate to) {
        final ICloseableIterable<Entry<FDate, V>> interceptor = parent.getQueryInterceptor().getEntries(from, to);
        if (interceptor != null) {
            return interceptor;
        } else {
            return new ICloseableIterable<Entry<FDate, V>>() {
                @Override
                public ICloseableIterator<Entry<FDate, V>> iterator() {
                    return new ICloseableIterator<Entry<FDate, V>>() {
                        private final HistoricalCacheQueryWithFuture<V> future = withFuture();
                        private Entry<FDate, V> nextEntry = future.getNextEntry(from, 0);
                        private FDate nextEntryKey = extractKey(nextEntry);

                        @Override
                        public boolean hasNext() {
                            return nextEntryKey != null && !nextEntryKey.isAfter(to);
                        }

                        private FDate extractKey(final Entry<FDate, V> e) {
                            return HistoricalCacheAssertValue.unwrapEntryKey(parent, e);
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

    public FDate getPreviousValueKey(final FDate from, final FDate to, final V value) {
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
            final FDate previousKey = parent.extractKey(previousEntry.getKey(), previousEntry.getValue());
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
        final HistoricalCacheQueryWithFuture<V> query = new HistoricalCacheQueryWithFuture<V>(parent);
        copyQuerySettings(query);
        return query;
    }

    protected final Collection<Entry<FDate, V>> newEntriesCollection() {
        if (filterDuplicateKeys) {
            return new ADelegateSet<Entry<FDate, V>>() {
                @Override
                protected Set<Entry<FDate, V>> createDelegate() {
                    return new LinkedHashSet<Entry<FDate, V>>();
                }

                @Override
                public boolean add(final Entry<FDate, V> e) {
                    return super.add(KeyIdentityEntry.of(e.getKey(), e.getValue()));
                }
            };
        } else {
            return new ArrayList<Entry<FDate, V>>();
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

}
