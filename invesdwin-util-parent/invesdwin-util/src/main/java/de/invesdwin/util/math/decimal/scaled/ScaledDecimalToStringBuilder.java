package de.invesdwin.util.math.decimal.scaled;

import java.text.DecimalFormat;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.AScaledDecimal;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class ScaledDecimalToStringBuilder<T extends AScaledDecimal<T, S>, S extends IDecimalScale<T, S>> {

    private final T parent;
    private S scale;
    private boolean symbol = true;
    private Integer decimalDigits;
    private boolean decimalDigitsOptional = true;
    private boolean decimalDigitsTrailing;

    public ScaledDecimalToStringBuilder(final T parent) {
        this.parent = parent;
        this.scale = parent.getScale();
    }

    public ScaledDecimalToStringBuilder<T, S> setScale(final S scale) {
        this.scale = scale;
        return this;
    }

    public ScaledDecimalToStringBuilder<T, S> setSymbolDisabled() {
        return setSymbol(false);
    }

    public ScaledDecimalToStringBuilder<T, S> setSymbol(final boolean symbol) {
        this.symbol = symbol;
        return this;
    }

    public boolean isSymbol() {
        return symbol;
    }

    public ScaledDecimalToStringBuilder<T, S> setDecimalDigits(final Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
        return this;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public ScaledDecimalToStringBuilder<T, S> setDecimalDigitsRequired() {
        setDecimalDigitsOptional(false);
        setDecimalDigitsTrailing(false);
        return this;
    }

    public ScaledDecimalToStringBuilder<T, S> setDecimalDigitsOptional(final boolean decimalDigitsOptional) {
        this.decimalDigitsOptional = decimalDigitsOptional;
        return this;
    }

    public ScaledDecimalToStringBuilder<T, S> setDecimalDigitsTrailing() {
        setDecimalDigitsTrailing(true);
        return this;
    }

    public ScaledDecimalToStringBuilder<T, S> setDecimalDigitsTrailing(final boolean decimalDigitsTrailing) {
        this.decimalDigitsTrailing = decimalDigitsTrailing;
        return this;
    }

    public boolean isDecimalDigitsOptional() {
        return decimalDigitsOptional;
    }

    public T getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return toString(getFormat());
    }

    public String getFormat() {
        final int providedDecimalDigits;
        if (decimalDigits == null) {
            providedDecimalDigits = getDefaultDecimalDigits();
        } else {
            providedDecimalDigits = decimalDigits;
        }
        final int trailingDecimalDigits;
        if (decimalDigitsTrailing) {
            final double value = parent.getValue(scale);
            trailingDecimalDigits = Doubles.getTrailingDecimalDigitsScale(value, providedDecimalDigits,
                    providedDecimalDigits * 3);
        } else {
            trailingDecimalDigits = providedDecimalDigits;
        }
        final String formatStr = scale.getFormat(parent, symbol, trailingDecimalDigits, decimalDigitsOptional);
        return formatStr;
    }

    public int getDefaultDecimalDigits() {
        return scale.getDefaultDecimalDigits(parent);
    }

    public String toString(final String format) {
        final DecimalFormat formatter = Decimal.newDecimalFormatInstance(format);
        final double value = parent.getValue(scale);
        final String str = formatter.format(value);
        if (symbol) {
            return normalizeNegativeZero(str, scale.getSymbol().length());
        } else {
            return normalizeNegativeZero(str, 0);
        }
    }

    public static String normalizeNegativeZero(final String str, final int skipSuffixLength) {
        if (str.length() >= 2 && str.charAt(0) == '-' && str.charAt(1) == '0') {
            if (str.length() > 3 && (str.charAt(2) == '.' || str.charAt(2) == ',')) {
                for (int i = 3; i < str.length() - skipSuffixLength; i++) {
                    if (str.charAt(i) != '0') {
                        return str;
                    }
                }
            }
            return Strings.removeStart(str, 1);
        } else {
            return str;
        }
    }

}
