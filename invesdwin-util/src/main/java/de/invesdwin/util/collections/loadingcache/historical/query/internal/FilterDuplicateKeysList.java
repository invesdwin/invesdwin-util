package de.invesdwin.util.collections.loadingcache.historical.query.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.ADelegateList;
import de.invesdwin.util.collections.Lists;
import de.invesdwin.util.time.fdate.FDate;

/**
 * A very fast duplicate key filtering list implementation that is designed to be only used by HistoricalCacheQuery. It
 * only works correctly when addAll is used with a List that has a descending order.
 */
@NotThreadSafe
public class FilterDuplicateKeysList<V> extends ADelegateList<Entry<FDate, V>> {
    private final int size;
    private Entry<FDate, V> minEntry;
    private Entry<FDate, V> maxEntry;

    public FilterDuplicateKeysList(final int size) {
        this.size = size;
    }

    @Override
    protected List<Entry<FDate, V>> newDelegate() {
        return new ArrayList<Entry<FDate, V>>(size);
    }

    @Override
    protected Collection<Entry<FDate, V>> filterAllowedElements(final Collection<? extends Entry<FDate, V>> c) {
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
}