package de.invesdwin.util.math.decimal.internal.randomizers.impl;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.randomizers.impl.blocklength.StationaryOptimalBlockLength;
import de.invesdwin.util.math.random.IRandomGenerator;

@ThreadSafe
public class StationaryBootstrapRandomizer<E extends ADecimal<E>> extends CircularBootstrapRandomizer<E> {

    private final double divisor;

    public StationaryBootstrapRandomizer(final IDecimalAggregate<E> parent) {
        super(parent);
        final int superBlockLength = super.nextBlockLength(null);
        divisor = Math.log(1D - (1D / superBlockLength)) * -1D;
    }

    @Override
    protected int newOptimalBlockLength(final IDecimalAggregate<E> parent) {
        return new StationaryOptimalBlockLength<E>(parent).getBlockLength();
    }

    @Override
    protected int nextBlockLength(final IRandomGenerator random) {
        //we randomize the block length for the stationary bootstrap
        final int newBlockLength = Math.round((float) (random.nextDouble() / divisor));
        return Math.max(1, newBlockLength);
    }

}