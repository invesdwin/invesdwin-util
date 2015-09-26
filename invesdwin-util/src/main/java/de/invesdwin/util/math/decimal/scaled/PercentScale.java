package de.invesdwin.util.math.decimal.scaled;

import java.text.DecimalFormatSymbols;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.decimal.Decimal;

@Immutable
public enum PercentScale implements IDecimalScale<Percent, PercentScale> {
    RATE("Rate", 0, ""),
    PERCENT("Percent", 2, String.valueOf(DecimalFormatSymbols.getInstance().getPercent())),
    PERMILLE("Permille", 3, String.valueOf(DecimalFormatSymbols.getInstance().getPerMill()));

    private final String text;
    private final int scale;
    private String symbol;

    PercentScale(final String text, final int scale, final String symbol) {
        this.text = text;
        this.scale = scale;
        this.symbol = symbol;
    }

    public int getScale() {
        return scale;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public Decimal convertValue(final Percent parent, final Decimal value, final PercentScale scale) {
        final Decimal rateValue = value.scaleByPowerOfTen(-scale.getScale());
        return rateValue.scaleByPowerOfTen(getScale());
    }

    @Override
    public String getFormat(final Percent parent, final boolean withSymbol) {
        final int scale = PercentScale.PERMILLE.getScale() - getScale() + 1;
        String format = ",##0";
        if (scale > 0) {
            format += "." + Strings.repeat("#", scale);
        }
        if (withSymbol && symbol.length() > 0) {
            format += "'" + symbol + "'";
        }
        return format;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

}
