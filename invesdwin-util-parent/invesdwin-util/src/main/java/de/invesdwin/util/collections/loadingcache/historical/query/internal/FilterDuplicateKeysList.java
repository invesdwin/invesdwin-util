package de.invesdwin.util.collections.loadingcache.historical.query.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Lists;
import de.invesdwin.util.collections.delegate.ADelegateList;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;

/**
 * A very fast duplicate key filtering list implementation that is designed to be only used by HistoricalCacheQuery. It
 * only works correctly when addAll is used with a List that has a descending order.
 */
@NotThreadSafe
public class FilterDuplicateKeysList<V> extends ADelegateList<IHistoricalEntry<V>> {
    private final int initialCapacity;
    private IHistoricalEntry<V> minEntry;
    private IHistoricalEntry<V> maxEntry;

    public FilterDuplicateKeysList(final int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    @Override
    protected List<IHistoricalEntry<V>> newDelegate() {
        return new ArrayList<IHistoricalEntry<V>>(initialCapacity);
    }

    @Override
    public List<IHistoricalEntry<V>> getDelegate() {
        return super.getDelegate();
    }

    public IHistoricalEntry<V> getMinEntry() {
        return minEntry;
    }

    public IHistoricalEntry<V> getMaxEntry() {
        return maxEntry;
    }

    @Override
    protected List<IHistoricalEntry<V>> filterAllowedElements(final Collection<? extends IHistoricalEntry<V>> c) {
        if (c.isEmpty()) {
            return Collections.emptyList();
        }
        final List<IHistoricalEntry<V>> cList = Lists.toList(c);
        final int highestIndexFirst = 0;
        final IHistoricalEntry<V> highestEntryFirst = cList.get(highestIndexFirst);
        final int lowestIndexLast = cList.size() - 1;
        final IHistoricalEntry<V> lowestEntryLast = cList.get(lowestIndexLast);
        if (highestEntryFirst.getKey().isBefore(lowestEntryLast.getKey())) {
            return filterAllowedElementsReverse(cList);
        } else if (highestEntryFirst.getKey().equals(lowestEntryLast.getKey())) {
            final IHistoricalEntry<V> onlyEntry = cList.get(0);
            if (isAddAllowed(onlyEntry)) {
                @SuppressWarnings("unchecked")
                final List<IHistoricalEntry<V>> onlyEntryList = Arrays.asList(onlyEntry);
                return onlyEntryList;
            } else {
                return Collections.emptyList();
            }
        }

        final Integer minIndex = determineMinIndex(cList, lowestEntryLast, highestIndexFirst, lowestIndexLast);
        if (minIndex == null) {
            return Collections.emptyList();
        }

        final Integer maxIndex = determineMaxIndex(cList, highestEntryFirst, highestIndexFirst, lowestIndexLast);
        if (maxIndex == null) {
            return Collections.emptyList();
        }
        return cList.subList(maxIndex, minIndex + 1);
    }

    private List<IHistoricalEntry<V>> filterAllowedElementsReverse(final List<IHistoricalEntry<V>> cList) {
        final int highestIndexLast = cList.size() - 1;
        final IHistoricalEntry<V> highestEntryLast = cList.get(highestIndexLast);
        final int lowestIndexFirst = 0;
        final IHistoricalEntry<V> lowestEntryFirst = cList.get(lowestIndexFirst);
        if (highestEntryLast.getKey().isBeforeOrEqualTo(lowestEntryFirst.getKey())) {
            throw new IllegalArgumentException("Expecting ascending order: first[" + lowestEntryFirst.getKey()
                    + "] < last[" + highestEntryLast.getKey() + "]");
        }

        final Integer minIndex = determineMinIndexReverse(cList, lowestEntryFirst, highestIndexLast, lowestIndexFirst);
        if (minIndex == null) {
            return Collections.emptyList();
        }

        final Integer maxIndex = determineMaxIndexReverse(cList, highestEntryLast, highestIndexLast, lowestIndexFirst);
        if (maxIndex == null) {
            return Collections.emptyList();
        }
        return cList.subList(minIndex, maxIndex + 1);
    }

    private Integer determineMaxIndex(final List<IHistoricalEntry<V>> cList,
            final IHistoricalEntry<V> highestEntryFirst, final int highestIndexFirst, final int lowestIndexLast) {
        final boolean newMaxEntry;
        Integer maxIndex = null;
        if (maxEntry == null || maxEntry.getKey().isBefore(highestEntryFirst.getKey())) {
            maxEntry = highestEntryFirst;
            maxIndex = highestIndexFirst;
            newMaxEntry = true;
        } else {
            newMaxEntry = false;
        }
        //from 0 until cList.size()-1
        for (int i = highestIndexFirst; i < lowestIndexLast; i++) {
            final IHistoricalEntry<V> curEntry = cList.get(i);
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

    private Integer determineMaxIndexReverse(final List<IHistoricalEntry<V>> cList,
            final IHistoricalEntry<V> highestEntryLast, final int highestIndexLast, final int lowestIndexFirst) {
        final boolean newMaxEntry;
        Integer maxIndex = null;
        if (maxEntry == null || maxEntry.getKey().isBefore(highestEntryLast.getKey())) {
            maxEntry = highestEntryLast;
            maxIndex = highestIndexLast;
            newMaxEntry = true;
        } else {
            newMaxEntry = false;
        }
        //from cList.size()-1 until 0
        //from lowstIndexLast until highestIndexFirst
        for (int i = highestIndexLast; i >= lowestIndexFirst; i--) {
            final IHistoricalEntry<V> curEntry = cList.get(i);
            if (maxEntry.getKey().isAfter(curEntry.getKey())) {
                maxIndex = i;
                if (newMaxEntry) {
                    maxIndex++;
                }
                break;
            }
        }
        return maxIndex;
    }

    private Integer determineMinIndex(final List<IHistoricalEntry<V>> cList, final IHistoricalEntry<V> lowestEntryLast,
            final int highestIndexFirst, final int lowestIndexLast) {
        final boolean newMinEntry;
        Integer minIndex = null;
        if (minEntry == null || minEntry.getKey().isAfter(lowestEntryLast.getKey())) {
            minEntry = lowestEntryLast;
            minIndex = lowestIndexLast;
            newMinEntry = true;
        } else {
            newMinEntry = false;
        }
        //from cList.size()-1 until 0
        //from lowstIndexLast until highestIndexFirst
        for (int i = lowestIndexLast; i >= highestIndexFirst; i--) {
            final IHistoricalEntry<V> curEntry = cList.get(i);
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

    private Integer determineMinIndexReverse(final List<IHistoricalEntry<V>> cList,
            final IHistoricalEntry<V> lowestEntryLast, final int highestIndexFirst, final int lowestIndexLast) {
        final boolean newMinEntry;
        Integer minIndex = null;
        if (minEntry == null || minEntry.getKey().isAfter(lowestEntryLast.getKey())) {
            minEntry = lowestEntryLast;
            minIndex = lowestIndexLast;
            newMinEntry = true;
        } else {
            newMinEntry = false;
        }

        //from 0 until cList.size()-1
        for (int i = lowestIndexLast; i < highestIndexFirst; i++) {
            final IHistoricalEntry<V> curEntry = cList.get(i);
            if (minEntry.getKey().isBefore(curEntry.getKey())) {
                minIndex = i;
                if (newMinEntry) {
                    minIndex--;
                }
                break;
            }
        }
        return minIndex;
    }

    @Override
    protected boolean isSetAllowed(final int index, final IHistoricalEntry<V> e) {
        //we need to support reversal, thus allowing set in all cases
        return true;
    }

    @Override
    public boolean isAddAllowed(final IHistoricalEntry<V> e) {
        if (minEntry == null) {
            minEntry = e;
            maxEntry = e;
            return true;
        } else {
            if (e.getKey().equals(minEntry.getKey()) || e.getKey().equals(maxEntry.getKey())) {
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

}