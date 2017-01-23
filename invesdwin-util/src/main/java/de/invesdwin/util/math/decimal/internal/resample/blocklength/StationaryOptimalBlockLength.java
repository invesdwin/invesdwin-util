package de.invesdwin.util.math.decimal.internal.resample.blocklength;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;

@NotThreadSafe
public class StationaryOptimalBlockLength<E extends ADecimal<E>> extends OptimalBlockLength<E> {

    public StationaryOptimalBlockLength(final IDecimalAggregate<E> parent) {
        super(parent);
    }

    @Override
    protected double determineOptimalBlockLength_multiplicator() {
        return 2.0;
    }

}
