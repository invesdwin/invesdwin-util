package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DecimalStreamDrawdown<E extends ADecimal<E>> implements IDecimalStreamAlgorithm<E, Percent> {

    private double maxEquity;

    public DecimalStreamDrawdown(final E initialEquity) {
        this.maxEquity = initialEquity.getDefaultValue().doubleValueRaw();
    }

    @Override
    public Percent process(final E equity) {
        final double equityDouble = equity.getDefaultValue().doubleValueRaw();
        if (equityDouble >= maxEquity) {
            maxEquity = equityDouble;
            return Percent.ZERO_PERCENT;
        } else {
            final double drawdown = maxEquity - equityDouble;
            //on multimarket strategies the drawdown can actually become positive for orders
            final Percent drawdownPercent = new Percent(drawdown, maxEquity);
            if (drawdownPercent.doubleValueRaw() <= 0) {
                throw new IllegalStateException(maxEquity + " -> " + equityDouble + " => " + drawdownPercent);
            }
            return drawdownPercent;
        }
    }

}
