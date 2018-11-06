package de.invesdwin.util.collections.loadingcache.historical.query.internal;

import java.util.Optional;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ASkippingIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.DisabledHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class HistoricalCacheQuery<V> implements IHistoricalCacheQuery<V> {

    private static final HistoricalCacheAssertValue DEFAULT_ASSERT_VALUE = HistoricalCacheAssertValue.ASSERT_VALUE_WITHOUT_FUTURE;

    protected final IHistoricalCacheInternalMethods<V> internalMethods;
    protected HistoricalCacheAssertValue assertValue = DEFAULT_ASSERT_VALUE;
    private IHistoricalCacheQueryElementFilter<V> elementFilter = DisabledHistoricalCacheQueryElementFilter
            .getInstance();

    public HistoricalCacheQuery(final IHistoricalCacheInternalMethods<V> internalMethods) {
        this.internalMethods = internalMethods;
    }

    @Override
    public void resetQuerySettings() {
        assertValue = DEFAULT_ASSERT_VALUE;
        elementFilter = null;
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
    public IHistoricalCacheQuery<V> withElementFilter(final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        if (elementFilter == null) {
            this.elementFilter = DisabledHistoricalCacheQueryElementFilter.getInstance();
        } else {
            this.elementFilter = elementFilter;
        }
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
    public final IHistoricalEntry<V> getEntry(final FDate key) {
        return internalMethods.getQueryCore().getEntry(this, key, assertValue);
    }

    @Override
    public V getValue(final FDate key) {
        return internalMethods.getQueryCore().getValue(this, key, assertValue);
    }

    @Override
    public final ICloseableIterable<IHistoricalEntry<V>> getEntries(final Iterable<FDate> keys) {
        return getEntries(keys, assertValue);
    }

    @Override
    public final ICloseableIterable<V> getValues(final Iterable<FDate> keys) {
        return getValues(keys, assertValue);
    }

    /**
     * If a key returns null, it will get skipped.
     */
    protected final ICloseableIterable<IHistoricalEntry<V>> getEntries(final Iterable<FDate> keys,
            final HistoricalCacheAssertValue assertValue) {
        return new ICloseableIterable<IHistoricalEntry<V>>() {
            @Override
            public ICloseableIterator<IHistoricalEntry<V>> iterator() {
                return new ICloseableIterator<IHistoricalEntry<V>>() {
                    private final ICloseableIterator<FDate> keysIterator = WrapperCloseableIterable.maybeWrap(keys)
                            .iterator();

                    @Override
                    public boolean hasNext() {
                        return keysIterator.hasNext();
                    }

                    @Override
                    public IHistoricalEntry<V> next() {
                        return internalMethods.getQueryCore()
                                .getEntry(HistoricalCacheQuery.this, keysIterator.next(), assertValue);
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
                    private final ICloseableIterator<IHistoricalEntry<V>> entriesIterator = getEntries(keys,
                            assertValue).iterator();

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return entriesIterator.next().getValue();
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
        final IHistoricalCacheQuery<?> interceptor = newKeysQueryInterceptor();
        if (interceptor != null) {
            return interceptor.getKey(key);
        }
        if (key == null) {
            return null;
        }
        return HistoricalCacheAssertValue
                .unwrapEntryKey(internalMethods.getQueryCore().getEntry(this, key, assertValue));
    }

    @Override
    public final FDate getPreviousKey(final FDate key, final int shiftBackUnits) {
        final IHistoricalCacheQuery<?> interceptor = newKeysQueryInterceptor();
        if (interceptor != null) {
            return interceptor.getPreviousKey(key, shiftBackUnits);
        }
        assertShiftUnitsPositive(shiftBackUnits);
        final Optional<FDate> optionalInterceptor = internalMethods.getQueryCore()
                .getParent()
                .getPreviousKeysQueryInterceptor()
                .getPreviousKey(key, shiftBackUnits);
        if (optionalInterceptor != null) {
            return optionalInterceptor.get();
        }
        return HistoricalCacheAssertValue.unwrapEntryKey(getPreviousEntry(key, shiftBackUnits));
    }

    /**
     * Skips null values for keys.
     * 
     * Fills the list with keys from the past.
     */
    @Override
    public final ICloseableIterable<FDate> getPreviousKeys(final FDate key, final int shiftBackUnits) {
        final IHistoricalCacheQuery<?> interceptor = newKeysQueryInterceptor();
        if (interceptor != null) {
            return interceptor.getPreviousKeys(key, shiftBackUnits);
        }
        assertShiftUnitsPositiveNonZero(shiftBackUnits);
        final ICloseableIterable<FDate> iterableInterceptor = internalMethods.getQueryCore()
                .getParent()
                .getPreviousKeysQueryInterceptor()
                .getPreviousKeys(key, shiftBackUnits);
        if (iterableInterceptor != null) {
            return iterableInterceptor;
        }
        return new ICloseableIterable<FDate>() {
            @Override
            public ICloseableIterator<FDate> iterator() {
                return new ICloseableIterator<FDate>() {
                    private final ICloseableIterator<IHistoricalEntry<V>> previousEntries = getPreviousEntries(key,
                            shiftBackUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return previousEntries.hasNext();
                    }

                    @Override
                    public FDate next() {
                        return previousEntries.next().getKey();
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
                    private final ICloseableIterator<IHistoricalEntry<V>> previousEntries = getPreviousEntries(key,
                            shiftBackUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return previousEntries.hasNext();
                    }

                    @Override
                    public V next() {
                        return previousEntries.next().getValue();
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
        final IHistoricalCacheQuery<?> interceptor = newKeysQueryInterceptor();
        if (interceptor != null) {
            return interceptor.getKeys(from, to);
        }
        final ICloseableIterable<FDate> iterableInterceptor = internalMethods.getQueryCore()
                .getParent()
                .getRangeQueryInterceptor()
                .getKeys(from, to);
        if (iterableInterceptor != null) {
            return iterableInterceptor;
        } else {
            return new ICloseableIterable<FDate>() {
                @Override
                public ICloseableIterator<FDate> iterator() {
                    return new ICloseableIterator<FDate>() {

                        private final ICloseableIterator<IHistoricalEntry<V>> entriesIterator = getEntries(from, to)
                                .iterator();

                        @Override
                        public boolean hasNext() {
                            return entriesIterator.hasNext();
                        }

                        @Override
                        public FDate next() {
                            return entriesIterator.next().getKey();
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
    public ICloseableIterable<IHistoricalEntry<V>> getEntries(final FDate from, final FDate to) {
        final ICloseableIterable<IHistoricalEntry<V>> iterableInterceptor = internalMethods.getQueryCore()
                .getParent()
                .getRangeQueryInterceptor()
                .getEntries(from, to);
        if (iterableInterceptor != null) {
            if (elementFilter == null || elementFilter instanceof DisabledHistoricalCacheQueryElementFilter) {
                return iterableInterceptor;
            } else {
                return new ASkippingIterable<IHistoricalEntry<V>>(iterableInterceptor) {
                    @Override
                    protected boolean skip(final IHistoricalEntry<V> element) {
                        if (!elementFilter.isValid(element.getKey(), element.getValue())) {
                            throw new FastNoSuchElementException(
                                    "HistoricalCacheQuery: getEntries elementFilter found not valid element");
                        }
                        return false;
                    }
                };
            }
        } else {
            return new ICloseableIterable<IHistoricalEntry<V>>() {
                @Override
                public ICloseableIterator<IHistoricalEntry<V>> iterator() {
                    return new ICloseableIterator<IHistoricalEntry<V>>() {
                        private final IHistoricalCacheQueryWithFuture<V> future = withFuture();
                        private IHistoricalEntry<V> nextEntry;
                        private FDate nextEntryKey;

                        {
                            nextEntry = future.getNextEntry(from, 0);
                            if (nextEntry != null) {
                                nextEntryKey = extractKey(nextEntry);
                                if (nextEntryKey.isBefore(from)) {
                                    //skip initial value if it is not inside requested range
                                    nextEntry = future.getNextEntry(nextEntryKey, 1);
                                    nextEntryKey = extractKey(nextEntry);
                                }
                            }
                        }

                        @Override
                        public boolean hasNext() {
                            return nextEntryKey != null && !nextEntryKey.isAfter(to);
                        }

                        private FDate extractKey(final IHistoricalEntry<V> e) {
                            return HistoricalCacheAssertValue.unwrapEntryKey(e);
                        }

                        @Override
                        public IHistoricalEntry<V> next() {
                            if (hasNext()) {
                                //always returning current and reading ahead once
                                final IHistoricalEntry<V> currentEntry = nextEntry;
                                final FDate currentEntryKey = extractKey(currentEntry);
                                nextEntry = future.getNextEntry(currentEntryKey, 1);
                                nextEntryKey = extractKey(nextEntry);
                                if (nextEntry != null && !nextEntryKey.isAfter(currentEntryKey)) {
                                    nextEntry = null;
                                    nextEntryKey = null;
                                }
                                return currentEntry;
                            } else {
                                throw new FastNoSuchElementException(
                                        "HistoricalCacheQuery: getEntries hasNext is false");
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

                    private final ICloseableIterator<IHistoricalEntry<V>> entriesIterator;

                    {
                        final ICloseableIterable<IHistoricalEntry<V>> entries = getEntries(from, to);
                        entriesIterator = entries.iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return entriesIterator.next().getValue();
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
    public FDate getPreviousKeyWithSameValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalEntry<V> entry = getPreviousEntryWithSameValueBetween(from, to, value);
        return HistoricalCacheAssertValue.unwrapEntryKey(entry);
    }

    @Override
    public V getPreviousValueWithSameValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalEntry<V> entry = getPreviousEntryWithSameValueBetween(from, to, value);
        return HistoricalCacheAssertValue.unwrapEntryValue(entry);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithSameValueBetween(final FDate from, final FDate to, final V value) {
        FDate curKey = to;
        final Optional<V> optionalValue = Optional.ofNullable(value);
        boolean firstTry = true;
        while (true) {
            final int shiftBackUnits;
            if (firstTry) {
                shiftBackUnits = 0;
            } else {
                shiftBackUnits = 1;
            }
            final IHistoricalEntry<V> previousEntry = getPreviousEntry(curKey, shiftBackUnits);
            if (previousEntry == null) {
                return null;
            }
            final FDate previousKey = previousEntry.getKey();
            if (!firstTry && !previousKey.isBefore(curKey)) {
                return null;
            }
            curKey = previousKey;
            if (curKey.isBefore(from)) {
                return null;
            }
            final Optional<V> optionalCurValue = Optional.ofNullable(previousEntry.getValue());
            if (optionalValue.equals(optionalCurValue)) {
                return previousEntry;
            }
            firstTry = false;
        }
    }

    @Override
    public FDate getPreviousKeyWithDifferentValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalEntry<V> entry = getPreviousEntryWithDifferentValueBetween(from, to, value);
        return HistoricalCacheAssertValue.unwrapEntryKey(entry);
    }

    @Override
    public V getPreviousValueWithDifferentValueBetween(final FDate from, final FDate to, final V value) {
        final IHistoricalEntry<V> entry = getPreviousEntryWithDifferentValueBetween(from, to, value);
        return HistoricalCacheAssertValue.unwrapEntryValue(entry);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithDifferentValueBetween(final FDate from, final FDate to,
            final V value) {
        FDate curKey = to;
        final Optional<V> optionalValue = Optional.ofNullable(value);
        boolean firstTry = true;
        while (true) {
            final int shiftBackUnits;
            if (firstTry) {
                shiftBackUnits = 0;
            } else {
                shiftBackUnits = 1;
            }
            final IHistoricalEntry<V> previousEntry = getPreviousEntry(curKey, shiftBackUnits);
            if (previousEntry == null) {
                return null;
            }
            final FDate previousKey = previousEntry.getKey();
            if (!firstTry && !previousKey.isBefore(curKey)) {
                return null;
            }
            curKey = previousKey;
            if (curKey.isBefore(from)) {
                return null;
            }
            final Optional<V> optionalCurValue = Optional.ofNullable(previousEntry.getValue());
            if (!optionalValue.equals(optionalCurValue)) {
                return previousEntry;
            }
            firstTry = false;
        }
    }

    @Override
    public FDate getPreviousKeyWithSameValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final IHistoricalEntry<V> entry = getPreviousEntryWithSameValue(key, maxShiftBackUnits, value);
        return HistoricalCacheAssertValue.unwrapEntryKey(entry);
    }

    @Override
    public V getPreviousValueWithSameValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final IHistoricalEntry<V> entry = getPreviousEntryWithSameValue(key, maxShiftBackUnits, value);
        return HistoricalCacheAssertValue.unwrapEntryValue(entry);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithSameValue(final FDate key, final int maxShiftBackUnits,
            final V value) {
        FDate curKey = key;
        final Optional<V> optionalValue = Optional.ofNullable(value);
        int curTry = 0;
        while (true) {
            final int curShiftBackUnits;
            if (curTry == 0) {
                curShiftBackUnits = 0;
            } else {
                curShiftBackUnits = 1;
            }
            final IHistoricalEntry<V> previousEntry = getPreviousEntry(curKey, curShiftBackUnits);
            if (previousEntry == null) {
                return null;
            }
            final FDate previousKey = previousEntry.getKey();
            if (curTry > 0 && !previousKey.isBefore(curKey)) {
                return null;
            }
            curKey = previousKey;
            final Optional<V> optionalCurValue = Optional.ofNullable(previousEntry.getValue());
            if (optionalValue.equals(optionalCurValue)) {
                return previousEntry;
            }
            curTry++;
            if (curTry > maxShiftBackUnits) {
                return null;
            }
        }
    }

    @Override
    public FDate getPreviousKeyWithDifferentValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final IHistoricalEntry<V> entry = getPreviousEntryWithDifferentValue(key, maxShiftBackUnits, value);
        return HistoricalCacheAssertValue.unwrapEntryKey(entry);
    }

    @Override
    public V getPreviousValueWithDifferentValue(final FDate key, final int maxShiftBackUnits, final V value) {
        final IHistoricalEntry<V> entry = getPreviousEntryWithDifferentValue(key, maxShiftBackUnits, value);
        return HistoricalCacheAssertValue.unwrapEntryValue(entry);
    }

    @Override
    public IHistoricalEntry<V> getPreviousEntryWithDifferentValue(final FDate key, final int maxShiftBackUnits,
            final V value) {
        FDate curKey = key;
        final Optional<V> optionalValue = Optional.ofNullable(value);
        int curTry = 0;
        while (true) {
            final int curShiftBackUnits;
            if (curTry == 0) {
                curShiftBackUnits = 0;
            } else {
                curShiftBackUnits = 1;
            }
            final IHistoricalEntry<V> previousEntry = getPreviousEntry(curKey, curShiftBackUnits);
            if (previousEntry == null) {
                return null;
            }
            final FDate previousKey = previousEntry.getKey();
            if (curTry > 0 && !previousKey.isBefore(curKey)) {
                return null;
            }
            curKey = previousKey;
            final Optional<V> optionalCurValue = Optional.ofNullable(previousEntry.getValue());
            if (!optionalValue.equals(optionalCurValue)) {
                return previousEntry;
            }
            curTry++;
            if (curTry > maxShiftBackUnits) {
                return null;
            }
        }
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void copyQuerySettings(final IHistoricalCacheQuery copyFrom) {
        this.assertValue = copyFrom.getAssertValue();
        this.elementFilter = copyFrom.getElementFilter();
    }

    protected HistoricalCacheQueryWithFuture<V> newFutureQuery() {
        final HistoricalCacheQueryWithFuture<V> query = new HistoricalCacheQueryWithFuture<V>(internalMethods);
        query.copyQuerySettings(this);
        return query;
    }

    protected IHistoricalCacheQuery<?> newKeysQueryInterceptor() {
        if (elementFilter == null || elementFilter instanceof DisabledHistoricalCacheQueryElementFilter) {
            final IHistoricalCacheQuery<?> interceptor = internalMethods.getQueryCore()
                    .getParent()
                    .newKeysQueryInterceptor();
            if (interceptor != null) {
                interceptor.copyQuerySettings(this);
                return interceptor;
            }
        }
        return null;
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
    public final IHistoricalEntry<V> getPreviousEntry(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositive(shiftBackUnits);
        return internalMethods.getQueryCore().getPreviousEntry(this, key, shiftBackUnits);
    }

    /**
     * Skips null values for keys.
     * 
     * Fills the list with values from the past.
     */
    @Override
    public final ICloseableIterable<IHistoricalEntry<V>> getPreviousEntries(final FDate key, final int shiftBackUnits) {
        assertShiftUnitsPositiveNonZero(shiftBackUnits);
        return internalMethods.getQueryCore().getPreviousEntries(this, key, shiftBackUnits);
    }

    @Override
    public IHistoricalEntry<V> computeEntry(final FDate key) {
        return internalMethods.getQueryCore().computeEntry(this, key, assertValue);
    }

    @Override
    public V computeValue(final FDate key) {
        return HistoricalCacheAssertValue
                .unwrapEntryValue(internalMethods.getQueryCore().computeEntry(this, key, assertValue));
    }

    @Override
    public FDate computeKey(final FDate key) {
        return HistoricalCacheAssertValue
                .unwrapEntryKey(internalMethods.getQueryCore().computeEntry(this, key, assertValue));
    }

}
