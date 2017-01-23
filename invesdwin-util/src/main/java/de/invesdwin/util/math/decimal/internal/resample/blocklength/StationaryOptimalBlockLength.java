package de.invesdwin.util.math.decimal.internal.resample.blocklength;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;

@NotThreadSafe
public class StationaryOptimalBlockLength<E extends ADecimal<E>> extends CircularOptimalBlockLength<E> {

    private static final double MULTIPLICATOR_TWO = 2;

    public StationaryOptimalBlockLength(final IDecimalAggregate<E> parent) {
        super(parent);
    }

    @Override
    protected double determineOptimalBlockLength_blockLengthMultiplicator() {
        return MULTIPLICATOR_TWO;
    }

}
