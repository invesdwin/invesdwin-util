package de.invesdwin.util.collections.loadingcache.historical.query.impl;

import java.util.NoSuchElementException;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public class ShiftForwardUnitsLoopIterator<V> implements ICloseableIterator<V> {

    private final FDate date;
    private final int shiftForwardUnits;
    private final Function<V, FDate> extractEndTimeF;
    private int shiftForwardRemaining;
    private final ICloseableIterator<? extends V> delegate;
    private V cachedReadNext;

    public ShiftForwardUnitsLoopIterator(final FDate date, final int shiftForwardUnits,
            final Function<V, FDate> extractEndTimeF, final ICloseableIterator<? extends V> delegate) {
        this.date = date;
        this.shiftForwardUnits = shiftForwardUnits;
        this.shiftForwardRemaining = shiftForwardUnits;
        this.extractEndTimeF = extractEndTimeF;
        this.delegate = delegate;
    }

    public int getShiftForwardRemaining() {
        return shiftForwardRemaining;
    }

    public void skip(final int count) {
        this.shiftForwardRemaining -= count;
    }

    @Override
    public boolean hasNext() {
        return readNext() != null;
    }

    @Override
    public V next() {
        final V readNext = readNext();
        cachedReadNext = null;
        if (readNext == null) {
            throw FastNoSuchElementException.getInstance("ShiftForwardUnitsLoopIterator: readNext is null");
        }
        return readNext;
    }

    private V readNext() {
        if (cachedReadNext != null) {
            return cachedReadNext;
        } else {
            try {
                /*
                 * workaround for determining next key with multiple values at the same millisecond (without this
                 * workaround we would return a duplicate that might produce an endless loop)
                 */
                if (shiftForwardUnits == 0) {
                    while (shiftForwardRemaining == 0) {
                        final V nextNextValue = delegate.next();
                        final FDate nextNextValueKey = extractEndTimeF.apply(nextNextValue);
                        if (!nextNextValueKey.isBeforeNotNullSafe(date)) {
                            cachedReadNext = nextNextValue;
                            shiftForwardRemaining--;
                            break;
                        }
                    }
                } else if (shiftForwardUnits == 1) {
                    while (shiftForwardRemaining >= 0) {
                        final V nextNextValue = delegate.next();
                        final FDate nextNextValueKey = extractEndTimeF.apply(nextNextValue);
                        if (shiftForwardRemaining == 1 || date.isBeforeNotNullSafe(nextNextValueKey)) {
                            cachedReadNext = nextNextValue;
                            shiftForwardRemaining--;
                            break;
                        }
                    }
                } else {
                    while (shiftForwardRemaining >= 0) {
                        final V nextNextValue = delegate.next();
                        cachedReadNext = nextNextValue;
                        shiftForwardRemaining--;
                        break;
                    }
                }
                //catching nosuchelement might be faster sometimes than checking hasNext(), e.g. for LevelDB
            } catch (final NoSuchElementException e) {
                close();
                return null;
            }
            return cachedReadNext;
        }
    }

    @Override
    public void close() {
        delegate.close();
    }

}
