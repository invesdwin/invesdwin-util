package de.invesdwin.util.math.decimal;

import javax.annotation.concurrent.Immutable;

@Immutable
public class FormattedDecimal extends Decimal {

    private final String format;

    public FormattedDecimal(final double value, final String format) {
        super(value);
        this.format = format;
    }

    public FormattedDecimal(final Number value, final String format) {
        super(value);
        this.format = format;
    }

    public FormattedDecimal(final String value, final String format) {
        super(value);
        this.format = format;
    }

    @Override
    public String toString() {
        return toFormattedString();
    }

    @Override
    public String toFormattedString() {
        return super.toFormattedString(format);
    }

}
