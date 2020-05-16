package de.invesdwin.util.math.stream.doubl.zigzag;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.math.stream.doubl.IDoubleStreamAlgorithm;

@NotThreadSafe
public class DoubleStreamZigZag implements IDoubleStreamAlgorithm {

    public static final Percent DEFAULT_REVERSAL_THRESHOLD = new Percent(5D, PercentScale.PERCENT);
    private final Percent reversalThreshold;
    private long troughIndex = 0;
    private double trough = Double.NaN;
    private long peakIndex = 0;
    private double peak = Double.NaN;
    private long curIndex = 0;
    private double current = Double.NaN;
    private double previous = Double.NaN;

    public DoubleStreamZigZag() {
        this(DEFAULT_REVERSAL_THRESHOLD);
    }

    public DoubleStreamZigZag(final Percent reversalThreshold) {
        Assertions.assertThat(reversalThreshold).isGreaterThan(Percent.ZERO_PERCENT);
        this.reversalThreshold = reversalThreshold;
    }

    @Override
    public double process(final double value) {
        curIndex++;
        if (Doubles.isNaN(peak)) {
            peak = value;
            peakIndex = curIndex;
            trough = value;
            troughIndex = curIndex;
        }
        final double reversalReference;
        final PriceDirection currentDirection = getDirection();
        switch (currentDirection) {
        case RISING:
            if (value > peak) {
                peak = value;
                peakIndex = curIndex;
                reversalReference = Double.NaN;
            } else {
                reversalReference = peak;
            }
            break;
        case FALLING:
            if (value < trough) {
                trough = value;
                troughIndex = curIndex;
                reversalReference = Double.NaN;
            } else {
                reversalReference = trough;
            }
            break;
        case UNCHANGED:
            //does not matter which side
            reversalReference = peak;
            break;
        default:
            throw UnknownArgumentException.newInstance(PriceDirection.class, currentDirection);
        }
        if (!Doubles.isNaN(reversalReference)) {
            final Percent change = Percent.relativeDifference(reversalReference, value);
            if (change.abs().isGreaterThanOrEqualTo(reversalThreshold)) {
                if (change.isPositive()) {
                    if (currentDirection != PriceDirection.RISING) {
                        //reference point was a trough, so we were falling before
                        peak = value;
                        peakIndex = curIndex;
                        previous = current;
                        current = trough;
                    }
                } else {
                    if (currentDirection != PriceDirection.FALLING) {
                        //reference point was a peak, so we were rising before
                        trough = value;
                        troughIndex = curIndex;
                        previous = current;
                        current = peak;
                    }
                }
            }
        }
        return Double.NaN;
    }

    public double getTrough() {
        return trough;
    }

    public double getPeak() {
        return peak;
    }

    public double getCurrent() {
        return current;
    }

    public double getPrevious() {
        return previous;
    }

    public PriceDirection getDirection() {
        if (Doubles.isNaN(trough) || Doubles.isNaN(peak)) {
            return PriceDirection.UNCHANGED;
        } else if (troughIndex < peakIndex) {
            return PriceDirection.RISING;
        } else if (peakIndex < troughIndex) {
            return PriceDirection.FALLING;
        } else {
            return PriceDirection.UNCHANGED;
        }
    }

}
