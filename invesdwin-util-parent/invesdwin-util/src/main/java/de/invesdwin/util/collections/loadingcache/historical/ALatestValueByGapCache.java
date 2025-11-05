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
    private FDate lastHighestAllowedKey = FDates.MIN_DATE;
    private boolean queryActive = false;
    private V firstValue;
    private FDate firstValueKey;

    protected abstract int getLastResetIndex();

    protected abstract FDate getHighestAllowedKey(int lastResetIndex);

    private FDate getHighestAllowedKeyMaybe(final FDate date, final int lastResetIndex) {
        if (lastHighestAllowedKey != null && date.isBeforeOrEqualToNotNullSafe(lastHighestAllowedKey)) {
            return lastHighestAllowedKey;
        } else {
            return getHighestAllowedKey(lastResetIndex);
        }
    }

    protected abstract V getFirstValue();

    protected abstract V getLatestValue(FDate key);

    protected abstract V getNextValue(V value, boolean reloadNextKey);

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
                final FDate newHighestAllowedKey = getHighestAllowedKey(lastResetIndex);
                if (newHighestAllowedKey == null) {
                    return getLatestValue(date);
                } else if (date.isAfterOrEqualToNotNullSafe(newHighestAllowedKey)) {
                    lastHighestAllowedKey = newHighestAllowedKey;
                    return getLatestValue(newHighestAllowedKey);
                } else {
                    lastHighestAllowedKey = newHighestAllowedKey;
                    initFirstValue();
                    return init(date, lastResetIndex);
                }
            }
            final FDate newHighestAllowedKey = getHighestAllowedKeyMaybe(date, lastResetIndex);
            if (newHighestAllowedKey != null) {
                if (!lastHighestAllowedKey.equalsNotNullSafe(newHighestAllowedKey)) {
                    final FDate prevHighestAllowedKeyCopy = lastHighestAllowedKey;
                    lastHighestAllowedKey = newHighestAllowedKey;
                    return trailForwardMaybe(date, prevHighestAllowedKeyCopy, newHighestAllowedKey, lastResetIndex);
                } else if (date.isAfterOrEqualToNotNullSafe(newHighestAllowedKey)) {
                    return getLatestValue(newHighestAllowedKey);
                }
            }
            return lookupValue(date, lastResetIndex, true);
        } finally {
            queryActive = false;
        }
    }

    private V lookupValue(final FDate date, final int lastResetIndex, final boolean moveForward) {
        if (firstValueKey != null && date.isBeforeNotNullSafe(firstValueKey)) {
            /*
             * Still init if key equals firstValueKey, so that subsequent calls also go against this cache. Thus only
             * shortcutting here for requests before firstValueKey
             */
            return firstValue;
        }
        final FDate prevPrevKey = getKey(PREV_PREV_INDEX);
        final FDate nextNextKey = getKey(NEXT_NEXT_INDEX);
        if (!date.isBetweenInclusiveNotNullSafe(prevPrevKey, nextNextKey)) {
            return init(date, lastResetIndex);
        } else {
            return bisectValue(date, moveForward);
        }
    }

    private V bisectValue(final FDate date, final boolean moveForward) {
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
            if (moveForward) {
                final FDate nextKey = extractEndTime(nextValue);
                if (date.isAfterNotNullSafe(nextKey)) {
                    //move forward a bit earlier to increase hit rate
                    moveForward();
                }
            }
            return nextValue;
        }
        case NEXT_NEXT_INDEX: {
            final V nextNextValue = values.get(NEXT_NEXT_INDEX);
            if (moveForward) {
                //now for sure move forward
                moveForward();
            }
            return nextNextValue;
        }
        default:
            throw UnknownArgumentException.newInstance(Integer.class, bisect);
        }
    }

    private V trailForwardMaybe(final FDate date, final FDate prevHighestAllowedKey, final FDate newHighestAllowedKey,
            final int lastResetIndex) {
        while (values.size() > 1) {
            final FDate nextNextKey = extractEndTime(values.getReverse(0));
            final FDate nextKey = extractEndTime(values.getReverse(1));
            if (nextNextKey.equalsNotNullSafe(nextKey)) {
                values.removeLast();
            } else {
                break;
            }
        }
        if (values.size() < values.capacity()) {
            if (values.isEmpty()) {
                return init(date, lastResetIndex);
            }
            boolean reloadNextKey = true;
            while (values.size() < values.capacity()) {
                final V tailValue = values.getTail();
                final V nextValue = getNextValueMaybe(tailValue, reloadNextKey);
                values.circularAdd(nextValue);
                reloadNextKey = false;
            }
        }
        if (date.isAfterOrEqualToNotNullSafe(prevHighestAllowedKey)) {
            final FDate prevPrevKey = getKey(PREV_PREV_INDEX);
            final FDate nextNextKey = getKey(NEXT_NEXT_INDEX);
            final long valuesDistance = nextNextKey.millisValue() - prevPrevKey.millisValue();
            final long highestValueDistance = newHighestAllowedKey.millisValue() - prevHighestAllowedKey.millisValue();
            if (highestValueDistance <= valuesDistance) {
                FDate lastKey = nextNextKey;
                while (true) {
                    moveForward();
                    final FDate updatedKey = getKey(NEXT_NEXT_INDEX);
                    if (updatedKey.isBeforeOrEqualToNotNullSafe(lastKey)) {
                        break;
                    } else {
                        lastKey = updatedKey;
                    }
                }
            }
            return lookupValue(date, lastResetIndex, false);
        } else {
            return lookupValue(date, lastResetIndex, true);
        }
    }

    private void initFirstValue() {
        firstValue = getFirstValue();
        if (firstValue != null) {
            firstValueKey = extractEndTime(firstValue);
        } else {
            firstValueKey = null;
        }
    }

    private void moveForward() {
        final V nextNextValue = values.get(NEXT_NEXT_INDEX);
        final FDate nextNextKey = extractEndTime(nextNextValue);
        if (nextNextKey.isAfterOrEqualToNotNullSafe(lastHighestAllowedKey)) {
            //nothing to update to
            return;
        }
        if (nextNextKey.equalsNotNullSafe(getKey(NEXT_INDEX))) {
            //no more data to move to
            return;
        }
        final V nextNextNextValue = getNextValueMaybe(nextNextValue, false);
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
            final V nextValue = getNextValueMaybe(curValue, false);
            final V nextNextValue = getNextValueMaybe(nextValue, false);

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

    private V getNextValueMaybe(final V value, final boolean reloadNextKey) {
        final FDate key = extractEndTime(value);
        if (key.isAfterOrEqualToNotNullSafe(lastHighestAllowedKey)) {
            return value;
        } else {
            return getNextValue(value, reloadNextKey);
        }
    }

}
