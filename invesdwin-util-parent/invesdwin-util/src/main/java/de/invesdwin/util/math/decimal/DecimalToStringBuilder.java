package de.invesdwin.util.math.decimal;

import java.text.DecimalFormat;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class DecimalToStringBuilder {

    private final Decimal parent;
    private Integer decimalDigits;
    private boolean decimalDigitsOptional = true;
    private boolean decimalDigitsTrailing;

    public DecimalToStringBuilder(final Decimal parent) {
        this.parent = parent;
    }

    public DecimalToStringBuilder setDecimalDigits(final Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
        return this;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public DecimalToStringBuilder setDecimalDigitsRequired() {
        setDecimalDigitsOptional(false);
        return this;
    }

    public DecimalToStringBuilder setDecimalDigitsOptional(final boolean decimalDigitsOptional) {
        this.decimalDigitsOptional = decimalDigitsOptional;
        return this;
    }

    public DecimalToStringBuilder setDecimalDigitsTrailing() {
        setDecimalDigitsTrailing(true);
        return this;
    }

    public DecimalToStringBuilder setDecimalDigitsTrailing(final boolean decimalDigitsTrailing) {
        this.decimalDigitsTrailing = decimalDigitsTrailing;
        return this;
    }

    public boolean isDecimalDigitsOptional() {
        return decimalDigitsOptional;
    }

    public Decimal getParent() {
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
            final double value = parent.getValue();
            trailingDecimalDigits = Doubles.getTrailingDecimalDigitsScale(value, providedDecimalDigits,
                    providedDecimalDigits * 3);
        } else {
            trailingDecimalDigits = providedDecimalDigits;
        }
        final String formatStr = newFormat(trailingDecimalDigits, decimalDigitsOptional);
        return formatStr;
    }

    public int getDefaultDecimalDigits() {
        return Decimal.MONEY_PRECISION;
    }

    public String toString(final String format) {
        final DecimalFormat formatter = Decimal.newDecimalFormatInstance(format);
        final double value = parent.getValue();
        final String str = formatter.format(value);
        return normalizeNegativeZero(str, 0);
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

    public String newFormat(final int decimalDigits, final boolean decimalDigitsOptional) {
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
        return format;
    }

}
