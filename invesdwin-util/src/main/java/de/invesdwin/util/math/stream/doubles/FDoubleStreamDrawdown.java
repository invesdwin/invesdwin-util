package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.doubles.scaled.FPercent;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class FDoubleStreamDrawdown<E extends AFDouble<E>> implements IStreamAlgorithm<E, FPercent> {

    private double maxEquity;

    public FDoubleStreamDrawdown(final E initialEquity) {
        this.maxEquity = initialEquity.getDefaultValue();
    }

    @Override
    public FPercent process(final E equity) {
        final double equityDouble = equity.getDefaultValue();
        if (equityDouble >= maxEquity) {
            maxEquity = equityDouble;
            return FPercent.ZERO_PERCENT;
        } else {
            final double drawdown = maxEquity - equityDouble;
            //on multimarket strategies the drawdown can actually become positive for orders
            final FPercent drawdownPercent = new FPercent(drawdown, maxEquity);
            if (drawdownPercent.getRate() <= 0) {
                throw new IllegalStateException(maxEquity + " -> " + equityDouble + " => " + drawdownPercent);
            }
            return drawdownPercent;
        }
    }

}
