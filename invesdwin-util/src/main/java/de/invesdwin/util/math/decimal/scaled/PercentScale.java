package de.invesdwin.util.math.decimal.scaled;

import java.text.DecimalFormatSymbols;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Doubles;

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
    public double convertValue(final Percent parent, final double value, final PercentScale scale) {
        final double rateValue = Doubles.scaleByPowerOfTen(value, -scale.getScale());
        return Doubles.scaleByPowerOfTen(rateValue, getScale());
    }

    @Override
    public int getDefaultDecimalDigits(final Percent parent) {
        return PercentScale.PERMILLE.getScale() - getScale() + 1;
    }

    @Override
    public String getFormat(final Percent parent, final boolean withSymbol, final int decimalDigits,
            final boolean decimalDigitsOptional) {
        String format = ",##0";
        if (decimalDigits > 0) {
            final String decimalDigitsStr;
            if (decimalDigitsOptional) {
                decimalDigitsStr = "#";
            } else {
                decimalDigitsStr = "0";
            }
            format += "." + Strings.repeat(decimalDigitsStr, decimalDigits);
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
