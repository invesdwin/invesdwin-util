package de.invesdwin.util.math.decimal.internal.resample;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.resample.blocklength.StationaryOptimalBlockLength;

@NotThreadSafe
public class StationaryBlockResampler<E extends ADecimal<E>> extends CircularBlockResampler<E> {

    private final RandomGenerator random = newRandomGenerator();

    private final double divisor;

    public StationaryBlockResampler(final de.invesdwin.util.math.decimal.internal.DecimalAggregate<E> parent) {
        super(parent);
        divisor = -Math.log(1.0 - 1.0 / super.newBlockLength());
    }

    @Override
    protected long newBlockLength(final IDecimalAggregate<E> parent) {
        return new StationaryOptimalBlockLength<E>(parent).getBlockLength();
    }

    @Override
    protected int newBlockLength() {
        //we randomize the block length for the stationary bootstrap
        int newBlockLength = Math.round((float) (random.nextDouble() / divisor));
        newBlockLength = newBlockLength < 1 ? 1 : newBlockLength;
        return newBlockLength;
    }

    protected JDKRandomGenerator newRandomGenerator() {
        return new JDKRandomGenerator();
    }

}