package de.invesdwin.util.collections.loadingcache.historical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class HistoricalCacheQueryWithFuture<V> extends HistoricalCacheQuery<V> {

    protected HistoricalCacheQueryWithFuture(final AHistoricalCache<V> parent) {
        super(parent);
    }

    @Override
    public HistoricalCacheQueryWithFuture<V> withElementFilter(
            final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        return (HistoricalCacheQueryWithFuture<V>) super.withElementFilter(elementFilter);
    }

    @Override
    public HistoricalCacheQueryWithFuture<V> withFilterDuplicateKeys(final boolean filterDuplicateKeys) {
        return (HistoricalCacheQueryWithFuture<V>) super.withFilterDuplicateKeys(filterDuplicateKeys);
    }

    @Override
    public HistoricalCacheQueryWithFuture<V> withRememberNullValue(final boolean rememberNullValue) {
        return (HistoricalCacheQueryWithFuture<V>) super.withRememberNullValue(rememberNullValue);
    }

    @Override
    public V getValue(final FDate key) {
        return super.getValue(key);
    }

    /**
     * Jumps the specified shiftForwardUnits to the future instead of only one unit.
     */
    public final FDate getNextKey(final FDate key, final int shiftForwardUnits) {
        Assertions.assertThat(shiftForwardUnits).isGreaterThanOrEqualTo(0);

        FDate nextKey = key;
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
                nextNextKey = parent.calculateNextKey(nextKey);
                if (nextNextKey == null) {
                    break;
                }
            }
            //the key of the value is the relevant one
            final Entry<FDate, V> nextEntry = assertValue.assertValue(parent, key, nextNextKey,
                    getValue(nextNextKey, HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE));
            if (nextEntry == null) {
                return null;
            } else {
                final V nextValue = nextEntry.getValue();
                nextKey = parent.extractKey(nextNextKey, nextValue);
            }
        }
        return nextKey;
    }

    /**
     * Skips null values for keys.
     * 
     * Fills the list with keys from the future.
     */
    public ICloseableIterable<FDate> getNextKeys(final FDate key, final int shiftForwardUnits) {
        //This has to work with lists internally to support FilterDuplicateKeys option
        final Collection<FDate> trailing = newKeysCollection();
        //With 10 units this iterates from 9 to 0
        //to go from past to future so that queries get minimized
        //only on the first call we risk multiple queries
        for (int unitsBack = shiftForwardUnits - 1; unitsBack >= 0; unitsBack--) {
            final FDate nextKey = getNextKey(key, unitsBack);
            if (nextKey != null) {
                trailing.add(nextKey);
            }
        }
        if (trailing instanceof List) {
            return new WrapperCloseableIterable<FDate>(trailing);
        } else {
            return new WrapperCloseableIterable<FDate>(new ArrayList<FDate>(trailing));
        }
    }

    public Entry<FDate, V> getNextEntry(final FDate key, final int shiftForwardUnits) {
        final FDate nextKey = getNextKey(key, shiftForwardUnits);
        //the key of the query is the relevant one
        return assertValue.assertValue(parent, key, nextKey,
                getValue(nextKey, HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE));
    }

    public V getNextValue(final FDate key, final int shiftForwardUnits) {
        return HistoricalCacheAssertValue.unwrapEntry(getNextEntry(key, shiftForwardUnits));
    }

    /**
     * Skips null values for keys.
     * 
     * Fills the list with values from the future.
     */
    public ICloseableIterable<Entry<FDate, V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        return new ICloseableIterable<Entry<FDate, V>>() {
            @Override
            public ACloseableIterator<Entry<FDate, V>> iterator() {
                return new ACloseableIterator<Entry<FDate, V>>() {
                    private final ACloseableIterator<FDate> nextKeys = getNextKeys(key, shiftForwardUnits).iterator();

                    @Override
                    protected boolean innerHasNext() {
                        return nextKeys.hasNext();
                    }

                    @Override
                    protected Entry<FDate, V> innerNext() {
                        final FDate nextKey = nextKeys.next();
                        return assertValue.assertValue(parent, key, nextKey,
                                getValue(nextKey, HistoricalCacheAssertValue.ASSERT_VALUE_WITH_FUTURE));
                    }

                    @Override
                    protected void innerClose() {
                        nextKeys.close();
                    }
                };
            }
        };
    }

    public ICloseableIterable<V> getNextValues(final FDate key, final int shiftForwardUnits) {
        return new ICloseableIterable<V>() {
            @Override
            public ACloseableIterator<V> iterator() {
                return new ACloseableIterator<V>() {
                    private final ACloseableIterator<Entry<FDate, V>> nextEntries = getNextEntries(key,
                            shiftForwardUnits).iterator();

                    @Override
                    protected boolean innerHasNext() {
                        return nextEntries.hasNext();
                    }

                    @Override
                    protected V innerNext() {
                        return HistoricalCacheAssertValue.unwrapEntry(nextEntries.next());
                    }

                    @Override
                    protected void innerClose() {
                        nextEntries.close();
                    }
                };
            }
        };
    }

}
