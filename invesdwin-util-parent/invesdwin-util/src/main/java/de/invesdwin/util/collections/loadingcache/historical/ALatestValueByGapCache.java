package de.invesdwin.util.collections.loadingcache.historical;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.circular.CircularGenericArrayQueue;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.time.date.BisectDuplicateKeyHandling;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@NotThreadSafe
public abstract class ALatestValueByGapCache<V> {

    private static final int PREV_PREV_INDEX = 0;
    private static final int PREV_INDEX = 1;
    private static final int CUR_INDEX = 2;
    private static final int NEXT_INDEX = 3;
    private static final int NEXT_NEXT_INDEX = 4;

    private final CircularGenericArrayQueue<V> values = new CircularGenericArrayQueue<>(5);
    private int prevResetIndex = getLastResetIndex() - 1;
    private FDate prevHighestAllowedKey = FDates.MIN_DATE;
    private boolean queryActive = false;
    private V firstValue;
    private FDate firstValueKey;

    protected abstract int getLastResetIndex();

    protected abstract FDate getHighestAllowedKey();

    protected abstract V getLatestValue(FDate key);

    protected abstract V getNextValue(V value);

    protected abstract V getPreviousValue(V value);

    protected abstract FDate extractEndTime(V value);

    private FDate getKey(final int index) {
        final V value = values.get(index);
        return extractEndTime(value);
    }

    public V getLatestValueByGap(final FDate date) {
        if (queryActive) {
            return getLatestValue(date);
        }
        queryActive = true;
        try {
            final int lastResetIndex = getLastResetIndex();
            if (prevResetIndex != lastResetIndex) {
                initFirstValue();
                return init(date, lastResetIndex);
            }
            final FDate highestAllowedKey = getHighestAllowedKey();
            if (highestAllowedKey != null && !prevHighestAllowedKey.equalsNotNullSafe(highestAllowedKey)) {
                prevHighestAllowedKey = highestAllowedKey;
                return init(date, lastResetIndex);
            }
            final FDate prevPrevKey = getKey(PREV_PREV_INDEX);
            final FDate nextNextKey = getKey(NEXT_NEXT_INDEX);
            if (!date.isBetweenInclusiveNotNullSafe(prevPrevKey, nextNextKey)) {
                return init(date, lastResetIndex);
            }
            final int bisect = FDates.bisect(this::extractEndTime, values, date, BisectDuplicateKeyHandling.UNDEFINED);
            switch (bisect) {
            case PREV_PREV_INDEX: {
                final V prevPrevValue = values.get(PREV_PREV_INDEX);
                //now for sure move backward
                moveBackward();
                return prevPrevValue;
            }
            case PREV_INDEX: {
                return values.get(PREV_INDEX);
            }
            case CUR_INDEX: {
                return values.get(CUR_INDEX);
            }
            case NEXT_INDEX: {
                final V nextValue = values.get(NEXT_INDEX);
                final FDate nextKey = extractEndTime(nextValue);
                if (date.isAfterNotNullSafe(nextKey)) {
                    //move forward a bit earlier to increase hit rate
                    moveForward();
                }
                return nextValue;
            }
            case NEXT_NEXT_INDEX: {
                final V nextNextValue = values.get(NEXT_NEXT_INDEX);
                //now for sure move forward
                moveForward();
                return nextNextValue;
            }
            default:
                throw UnknownArgumentException.newInstance(Integer.class, bisect);
            }
        } finally {
            queryActive = false;
        }
    }

    private void initFirstValue() {
        firstValue = getLatestValue(FDates.MIN_DATE);
        if (firstValue != null) {
            firstValueKey = extractEndTime(firstValue);
        } else {
            firstValueKey = null;
        }
    }

    private void moveForward() {
        final V nextNextValue = values.get(NEXT_NEXT_INDEX);
        final FDate nextNextKey = extractEndTime(nextNextValue);
        if (nextNextKey.equalsNotNullSafe(getKey(NEXT_INDEX))) {
            //no more data to move to
            return;
        }
        final V nextNextNextValue = getNextValueMaybe(nextNextValue);
        values.circularAdd(nextNextNextValue);
    }

    private void moveBackward() {
        final V prevPrevValue = values.get(PREV_PREV_INDEX);
        final FDate prevPrevKey = extractEndTime(prevPrevValue);
        if (prevPrevKey.equalsNotNullSafe(getKey(PREV_INDEX))) {
            //no more data to move to
            return;
        }
        final V prevPrevPrevValue = getPreviousValueMaybe(prevPrevValue);
        values.circularPrepend(prevPrevPrevValue);
    }

    private V init(final FDate date, final int lastResetIndex) {
        final V curValue = getLatestValue(date);
        if (curValue != null) {
            final V prevValue = getPreviousValueMaybe(curValue);
            final V prevPrevValue = getPreviousValueMaybe(prevValue);
            final V nextValue = getNextValueMaybe(curValue);
            final V nextNextValue = getNextValueMaybe(nextValue);

            values.pretendClear();
            values.add(prevPrevValue);
            values.add(prevValue);
            values.add(curValue);
            values.add(nextValue);
            values.add(nextNextValue);

            prevResetIndex = lastResetIndex;
            return curValue;
        } else {
            prevResetIndex = lastResetIndex - 1;
            return null;
        }
    }

    private V getPreviousValueMaybe(final V value) {
        if (firstValueKey == null) {
            initFirstValue();
            if (firstValueKey == null) {
                throw new IllegalStateException("Unable to determine firstValueKey while value is available: " + value);
            }
        }
        final FDate key = extractEndTime(value);
        if (key.isBeforeOrEqualToNotNullSafe(firstValueKey)) {
            return firstValue;
        } else {
            return getPreviousValue(value);
        }
    }

    private V getNextValueMaybe(final V value) {
        final FDate key = extractEndTime(value);
        if (key.isAfterOrEqualToNotNullSafe(prevHighestAllowedKey)) {
            return value;
        } else {
            return getNextValue(value);
        }
    }

}
