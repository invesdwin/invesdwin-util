package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DecimalStreamDrawdown<E extends ADecimal<E>> implements IDecimalStreamAlgorithm<E, Percent> {

    private E maxEquity = null;

    public DecimalStreamDrawdown(final E initialEquity) {
        this.maxEquity = initialEquity;
    }

    @Override
    public Percent process(final E equity) {
        if (equity.isGreaterThanOrEqualTo(maxEquity)) {
            maxEquity = equity;
            return Percent.ZERO_PERCENT;
        } else {
            final E drawdown = maxEquity.subtract(equity);
            //on multimarket strategies the drawdown can actually become positive for orders
            final Percent drawdownPercent = new Percent(drawdown, maxEquity);
            if (!drawdownPercent.isPositive()) {
                throw new IllegalStateException(maxEquity + " -> " + equity + " => " + drawdownPercent);
            }
            return drawdownPercent;
        }
    }

}
