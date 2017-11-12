package de.invesdwin.util.math.statistics.runningmedian;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.statistics.runningmedian.internal.IndexMaxPQ;
import de.invesdwin.util.math.statistics.runningmedian.internal.IndexMinPQ;

/**
 * https://kartikkukreja.wordpress.com/2013/04/12/median-of-a-dynamic-list/
 * 
 * https://github.com/kartikkukreja/blog-codes/blob/master/src/Running%20Median.java
 */
@NotThreadSafe
public class RunningMedian {

    private static final int MIN_REORGANIZATION_MULTIPLIER = 10;
    private static final int DEFAULT_REORGANIZATION_MULTIPLIER = 100;
    private final NavigableMap<Double, NavigableSet<Integer>> indexMin = new TreeMap<Double, NavigableSet<Integer>>();
    private final NavigableMap<Double, NavigableSet<Integer>> indexMax = new TreeMap<Double, NavigableSet<Integer>>();
    private IndexMinPQ<Double> minPQ;
    private IndexMaxPQ<Double> maxPQ;

    private boolean exchange = false;
    private int iMin = 0;
    private int iMax = 0;
    private final int pqSize;
    private final int pqSizeReorganizationThreshold;

    public RunningMedian(final int size) {
        this(size, DEFAULT_REORGANIZATION_MULTIPLIER);
    }

    public RunningMedian(final int size, final int reorganizationMultiplier) {
        if (reorganizationMultiplier < MIN_REORGANIZATION_MULTIPLIER) {
            throw new IllegalArgumentException("reorganizationMultiplier should be at least 10");
        }
        pqSize = reorganizationMultiplier * size + 1;
        minPQ = new IndexMinPQ<Double>(pqSize);
        maxPQ = new IndexMaxPQ<Double>(pqSize);
        this.pqSizeReorganizationThreshold = pqSize - 10;
    }

    public void remove(final Double x) {
        if (indexMin.containsKey(x)) {
            final int index = indexMin.get(x).first();
            removeFromMap(x, index, indexMin);
            minPQ.delete(index);
            exchange = false;
            if (minPQ.size() < maxPQ.size()) { // delete from MaxPQ and insert into MinPQ
                exchange = true;
                final Double key1 = maxPQ.maxKey();
                final int index1 = maxPQ.delMax().getKey();
                removeFromMap(key1, index1, indexMax);

                minPQ.insert(iMin, key1);
                addToMap(key1, iMin, indexMin);
                iMin++;
            }
        } else if (indexMax.containsKey(x)) {
            final int index = indexMax.get(x).first();
            removeFromMap(x, index, indexMax);
            maxPQ.delete(index);
            exchange = true;
            if (minPQ.size() > maxPQ.size() + 1) { // delete from MinPQ and insert into MaxPQ
                exchange = false;
                final Double key1 = minPQ.minKey();
                final int index1 = minPQ.delMin().getKey();
                removeFromMap(key1, index1, indexMin);

                maxPQ.insert(iMax, key1);
                addToMap(key1, iMax, indexMax);
                iMax++;
            }
        } else {
            throw new java.util.NoSuchElementException("Element not found: " + x);
        }
        maybeReorganize();
    }

    public void add(final Double x) {
        minPQ.insert(iMin, x);
        addToMap(x, iMin, indexMin);
        iMin++;
        if (exchange) {
            final Double key = minPQ.minKey();
            final int index = minPQ.delMin().getKey();
            removeFromMap(key, index, indexMin);
            maxPQ.insert(iMax, key);
            addToMap(key, iMax, indexMax);
            iMax++;
            exchange = false;
        } else {
            exchange = true;
            if (!maxPQ.isEmpty() && maxPQ.maxKey() > minPQ.minKey()) {
                final Double key1 = maxPQ.maxKey();
                final int index1 = maxPQ.delMax().getKey();
                removeFromMap(key1, index1, indexMax);

                final Double key = minPQ.minKey();
                final int index = minPQ.delMin().getKey();
                removeFromMap(key, index, indexMin);
                maxPQ.insert(iMax, key);
                addToMap(key, iMax, indexMax);
                iMax++;

                minPQ.insert(iMin, key1);
                addToMap(key1, iMin, indexMin);
                iMin++;
            }
        }
        maybeReorganize();
    }

    private void maybeReorganize() {
        if (iMin >= pqSizeReorganizationThreshold || iMax >= pqSizeReorganizationThreshold) {
            indexMin.clear();
            indexMax.clear();
            iMin = 0;
            iMax = 0;
            exchange = false;
            final IndexMinPQ<Double> oldMinPQ = minPQ;
            final IndexMaxPQ<Double> oldMaxPQ = maxPQ;
            minPQ = new IndexMinPQ<Double>(pqSize);
            maxPQ = new IndexMaxPQ<Double>(pqSize);
            while (!oldMinPQ.isEmpty() || !oldMaxPQ.isEmpty()) {
                if (!oldMinPQ.isEmpty()) {
                    final Entry<Integer, Double> delMin = oldMinPQ.delMin();
                    add(delMin.getValue());
                }
                if (!oldMaxPQ.isEmpty()) {
                    final Entry<Integer, Double> delMax = oldMaxPQ.delMax();
                    add(delMax.getValue());
                }
            }
        }
    }

    public Double getMedian() {
        if (minPQ.isEmpty()) {
            return null;
        } else if (minPQ.size() > maxPQ.size()) {
            final Double result = minPQ.minKey();
            return result;
        } else {
            final Double result = minPQ.minKey() + maxPQ.maxKey();
            return result / 2;
        }
    }

    private void addToMap(final Double key, final int val, final NavigableMap<Double, NavigableSet<Integer>> map) {
        if (map.containsKey(key)) {
            map.get(key).add(val);
        } else {
            final NavigableSet<Integer> arg = new TreeSet<Integer>();
            arg.add(val);
            map.put(key, arg);
        }
    }

    private void removeFromMap(final Double key, final int val, final NavigableMap<Double, NavigableSet<Integer>> map) {
        final NavigableSet<Integer> arg = map.get(key);
        arg.remove(val);
        if (arg.isEmpty()) {
            map.remove(key);
        }
    }

}