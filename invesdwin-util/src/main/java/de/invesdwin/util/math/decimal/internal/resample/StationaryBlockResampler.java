package de.invesdwin.util.math.decimal.internal.resample;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.config.BlockBootstrapConfig;
import de.invesdwin.util.math.decimal.internal.DecimalAggregate;
import de.invesdwin.util.math.decimal.internal.resample.blocklength.StationaryOptimalBlockLength;

@NotThreadSafe
public class StationaryBlockResampler<E extends ADecimal<E>> extends CircularBlockResampler<E> {

    private final double divisor;

    public StationaryBlockResampler(final DecimalAggregate<E> parent, final BlockBootstrapConfig config) {
        super(parent, config);
        final int superBlockLength = super.nextBlockLength();
        divisor = Math.log(1D - (1D / superBlockLength)) * -1D;
    }

    @Override
    protected int newOptimalBlockLength(final IDecimalAggregate<E> parent) {
        return new StationaryOptimalBlockLength<E>(parent).getBlockLength();
    }

    @Override
    protected int nextBlockLength() {
        //we randomize the block length for the stationary bootstrap
        int newBlockLength = Math.round((float) (random.nextDouble() / divisor));
        newBlockLength = newBlockLength < 1 ? 1 : newBlockLength;
        return newBlockLength;
    }

}