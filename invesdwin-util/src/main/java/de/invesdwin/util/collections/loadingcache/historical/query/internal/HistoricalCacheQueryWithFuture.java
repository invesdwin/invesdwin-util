package de.invesdwin.util.collections.loadingcache.historical.query.internal;

import java.util.Collection;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class HistoricalCacheQueryWithFuture<V> extends HistoricalCacheQuery<V>
        implements IHistoricalCacheQueryWithFuture<V> {

    protected HistoricalCacheQueryWithFuture(final IHistoricalCacheQueryCore<V> core) {
        super(core);
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withElementFilter(
            final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        return (IHistoricalCacheQueryWithFuture<V>) super.withElementFilter(elementFilter);
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withFilterDuplicateKeys(final boolean filterDuplicateKeys) {
        return (IHistoricalCacheQueryWithFuture<V>) super.withFilterDuplicateKeys(filterDuplicateKeys);
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withRememberNullValue(final boolean rememberNullValue) {
        return (IHistoricalCacheQueryWithFuture<V>) super.withRememberNullValue(rememberNullValue);
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withFutureNull() {
        return (IHistoricalCacheQueryWithFuture<V>) super.withFutureNull();
    }

    @Override
    public V getValue(final FDate key) {
        return super.getValue(key);
    }

    @Override
    public final FDate getNextKey(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositive(shiftForwardUnits);
        return HistoricalCacheAssertValue.unwrapEntryKey(core.getParent(), getNextEntry(key, shiftForwardUnits));
    }

    @Override
    public ICloseableIterable<FDate> getNextKeys(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
        return new ICloseableIterable<FDate>() {
            @Override
            public ICloseableIterator<FDate> iterator() {
                return new ICloseableIterator<FDate>() {
                    private final ICloseableIterator<Entry<FDate, V>> nextEntries = getNextEntries(key,
                            shiftForwardUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return nextEntries.hasNext();
                    }

                    @Override
                    public FDate next() {
                        return HistoricalCacheAssertValue.unwrapEntryKey(core.getParent(), nextEntries.next());
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
    public Entry<FDate, V> getNextEntry(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositive(shiftForwardUnits);
        FDate nextKey = key;
        Entry<FDate, V> nextEntry = null;
        for (int i = 0; i <= shiftForwardUnits; i++) {
            /*
             * if key of value == key, the same key would be returned on the next call
             * 
             * we decrement by one unit to get the next key
             */

            final FDate nextNextKey;
            if (i == 0) {
                nextNextKey = nextKey;
            } else {
                nextNextKey = core.getParent().calculateNextKey(nextKey);
                if (nextNextKey == null) {
                    break;
                }
            }
            //the key of the value is the relevant one
            final Entry<FDate, V> nextNextEntry = assertValue.assertValue(core.getParent(), key, nextNextKey,
                    core.getValue(internalMethods, nextNextKey, HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE));
            if (nextNextEntry == null) {
                return null;
            } else {
                final V nextValue = nextNextEntry.getValue();
                nextKey = core.getParent().extractKey(nextNextKey, nextValue);
                nextEntry = nextNextEntry;
            }
        }
        return nextEntry;
    }

    @Override
    public V getNextValue(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositive(shiftForwardUnits);
        return HistoricalCacheAssertValue.unwrapEntryValue(getNextEntry(key, shiftForwardUnits));
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
        //This has to work with lists internally to support FilterDuplicateKeys option
        final Collection<Entry<FDate, V>> trailing = newEntriesCollection();
        //With 10 units this iterates from 9 to 0
        //to go from past to future so that queries get minimized
        //only on the first call we risk multiple queries
        for (int unitsBack = shiftForwardUnits - 1; unitsBack >= 0; unitsBack--) {
            final Entry<FDate, V> nextEntry = getNextEntry(key, unitsBack);
            if (nextEntry != null) {
                trailing.add(nextEntry);
            }
        }
        return WrapperCloseableIterable.maybeWrap(trailing);
    }

    @Override
    public ICloseableIterable<V> getNextValues(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {
                    private final ICloseableIterator<Entry<FDate, V>> nextEntries = getNextEntries(key,
                            shiftForwardUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return nextEntries.hasNext();
                    }

                    @Override
                    public V next() {
                        return HistoricalCacheAssertValue.unwrapEntryValue(nextEntries.next());
                    }

                    @Override
                    public void close() {
                        nextEntries.close();
                    }
                };
            }
        };
    }

}
