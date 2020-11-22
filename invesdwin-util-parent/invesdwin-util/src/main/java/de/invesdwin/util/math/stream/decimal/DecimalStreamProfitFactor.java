package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.doubl.DoubleStreamProfitFactor;

@NotThreadSafe
public class DecimalStreamProfitFactor<E extends ADecimal<E>> implements IStreamAlgorithm<E, Void> {

    private final DoubleStreamProfitFactor delegate = new DoubleStreamProfitFactor();

    @Override
    public Void process(final E value) {
        final double doubleValue = value.doubleValue();
        delegate.process(doubleValue);
        return null;
    }

    public Percent getProfitFactor() {
        return new Percent(delegate.getProfitFactor(), PercentScale.RATE);
    }

}
