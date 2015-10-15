package de.invesdwin.util.collections.loadingcache.historical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Optional;

import de.invesdwin.util.assertions.Assertions;
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
        return HistoricalCacheAssertValue.unwrapEntry(getEntry(key, assertValue));
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
                return new WrapperCloseableIterator<Entry<FDate, V>>(new Iterator<Entry<FDate, V>>() {
                    private final Iterator<FDate> keysIterator = keys.iterator();

                    @Override
                    public boolean hasNext() {
                        return keysIterator.hasNext();
                    }

                    @Override
                    public Entry<FDate, V> next() {
                        return getEntry(keysIterator.next(), assertValue);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                });
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
                        return HistoricalCacheAssertValue.unwrapEntry(entriesIterator.next());
                    }

                    @Override
                    public void close() {
                        entriesIterator.close();
                    }
                };
            }
        };
    }

    /**
     * Jumps the specified shiftBackUnits to the past instead of only one unit.
     */
    public final FDate getPreviousKey(final FDate key, final int shiftBackUnits) {
        Assertions.assertThat(shiftBackUnits).isGreaterThanOrEqualTo(0);

        FDate previousKey = key;
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
            final Entry<FDate, V> previousEntry = assertValue.assertValue(parent, key, previousPreviousKey,
                    getValue(previousPreviousKey, HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE));
            if (previousEntry == null) {
                return null;
            } else {
                final V previousValue = previousEntry.getValue();
                previousKey = parent.extractKey(previousPreviousKey, previousValue);
            }
        }
        return previousKey;
    }

    /**
     * Skips null values for keys.
     * 
     * Fills the list with keys from the past.
     */
    public final ICloseableIterable<FDate> getPreviousKeys(final FDate key, final int shiftBackUnits) {
        //This has to work with lists internally to support FilterDuplicateKeys option
        final Collection<FDate> trailing = newKeysCollection();
        //With 10 units this iterates from 9 to 0
        //to go from past to future so that queries get minimized
        //only on the first call we risk multiple queries
        for (int unitsBack = shiftBackUnits - 1; unitsBack >= 0; unitsBack--) {
            final FDate previousKey = getPreviousKey(key, unitsBack);
            if (previousKey != null) {
                trailing.add(previousKey);
            }
        }
        if (trailing instanceof List) {
            return new WrapperCloseableIterable<FDate>(trailing);
        } else {
            return new WrapperCloseableIterable<FDate>(new ArrayList<FDate>(trailing));
        }
    }

    public final Entry<FDate, V> getPreviousEntry(final FDate key, final int shiftBackUnits) {
        final FDate previousKey = getPreviousKey(key, shiftBackUnits);
        //the key of the query is the relevant one
        return assertValue.assertValue(parent, key, previousKey,
                getValue(previousKey, HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE));
    }

    public final V getPreviousValue(final FDate key, final int shiftBackUnits) {
        return HistoricalCacheAssertValue.unwrapEntry(getPreviousEntry(key, shiftBackUnits));
    }

    /**
     * Skips null values for keys.
     * 
     * Fills the list with values from the past.
     */
    public final ICloseableIterable<Entry<FDate, V>> getPreviousEntries(final FDate key, final int shiftBackUnits) {
        return new ICloseableIterable<Entry<FDate, V>>() {
            @Override
            public ICloseableIterator<Entry<FDate, V>> iterator() {
                return new ICloseableIterator<Entry<FDate, V>>() {
                    private final ICloseableIterator<FDate> previousKeys = getPreviousKeys(key, shiftBackUnits)
                            .iterator();

                    @Override
                    public boolean hasNext() {
                        return previousKeys.hasNext();
                    }

                    @Override
                    public Entry<FDate, V> next() {
                        final FDate previousKey = previousKeys.next();
                        return assertValue.assertValue(parent, key, previousKey,
                                getValue(previousKey, HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE));
                    }

                    @Override
                    public void close() {
                        previousKeys.close();
                    }
                };
            }

        };
    }

    public final ICloseableIterable<V> getPreviousValues(final FDate key, final int shiftBackUnits) {
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
                        return HistoricalCacheAssertValue.unwrapEntry(previousEntries.next());
                    }

                    @Override
                    public void close() {
                        previousEntries.close();
                    }
                };
            }

        };
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
                    return new WrapperCloseableIterator<FDate>(new Iterator<FDate>() {
                        private final HistoricalCacheQueryWithFuture<V> future = withFuture();
                        private FDate nextKey = future.getNextKey(from, 0);

                        @Override
                        public boolean hasNext() {
                            return nextKey != null && !nextKey.isAfter(to);
                        }

                        @Override
                        public FDate next() {
                            if (hasNext()) {
                                //always returning current and reading ahead once
                                final FDate currentKey = nextKey;
                                nextKey = future.getNextKey(currentKey, 1);
                                if (nextKey != null && !nextKey.isAfter(currentKey)) {
                                    nextKey = null;
                                }
                                return currentKey;
                            } else {
                                throw new NoSuchElementException();
                            }
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    });
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

                        private final ICloseableIterator<FDate> keysIterator = getKeys(from, to).iterator();

                        @Override
                        public boolean hasNext() {
                            return keysIterator.hasNext();
                        }

                        @Override
                        public Entry<FDate, V> next() {
                            return getEntry(keysIterator.next());
                        }

                        @Override
                        public void close() {
                            keysIterator.close();
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
                        return HistoricalCacheAssertValue.unwrapEntry(entriesIterator.next());
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
            int shiftBackUnits;
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

    protected final Collection<FDate> newKeysCollection() {
        if (filterDuplicateKeys) {
            return new LinkedHashSet<FDate>();
        } else {
            return new ArrayList<FDate>();
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
