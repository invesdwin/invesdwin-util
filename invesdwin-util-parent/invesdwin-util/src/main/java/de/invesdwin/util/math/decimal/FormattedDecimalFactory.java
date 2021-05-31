package de.invesdwin.util.math.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class FormattedDecimalFactory {

    private final int trailingDigits;
    private final int maxDigits;

    private int digits;
    private String format;

    public FormattedDecimalFactory(final int trailingDigits, final int maxDigits) {
        this.trailingDigits = trailingDigits;
        this.maxDigits = maxDigits;

        digits = 0;
        format = newFormat(digits);
    }

    protected String newFormat(final int digits) {
        return Decimal.newMoneyDecimalFormat(digits);
    }

    public FormattedDecimal format(final double value) {
        if (Doubles.isNaN(value)) {
            return null;
        }
        if (digits < maxDigits && !Doubles.isInteger(value)) {
            final int decimalDigits = Doubles.getTrailingDecimalDigitsScale(value, trailingDigits, maxDigits);
            if (decimalDigits > digits) {
                digits = decimalDigits;
                format = newFormat(decimalDigits);
            }
        }
        return new FormattedDecimal(value, format);
    }

}
