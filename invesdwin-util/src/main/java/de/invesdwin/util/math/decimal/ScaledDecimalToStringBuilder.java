package de.invesdwin.util.math.decimal;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.decimal.scaled.IDecimalScale;

@NotThreadSafe
public class ScaledDecimalToStringBuilder<T extends AScaledDecimal<T, S>, S extends IDecimalScale<T, S>> {

    private final T parent;
    private S scale;
    private boolean withSymbol = true;
    private Integer decimalDigits;
    private boolean decimalDigitsOptional = true;

    public ScaledDecimalToStringBuilder(final T parent) {
        this.parent = parent;
        this.scale = parent.getScale();
    }

    public ScaledDecimalToStringBuilder<T, S> withScale(final S scale) {
        this.scale = scale;
        return this;
    }

    public ScaledDecimalToStringBuilder<T, S> withoutSymbol() {
        withSymbol = false;
        return this;
    }

    public ScaledDecimalToStringBuilder<T, S> withSymbol(final boolean withSymbol) {
        this.withSymbol = withSymbol;
        return this;
    }

    public boolean isWithSymbol() {
        return withSymbol;
    }

    public ScaledDecimalToStringBuilder<T, S> withDecimalDigits(final Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
        return this;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public ScaledDecimalToStringBuilder<T, S> withDecimalDigitsRequired() {
        withDecimalDigitsOptional(false);
        return this;
    }

    public ScaledDecimalToStringBuilder<T, S> withDecimalDigitsOptional(final boolean decimalDigitsOptional) {
        this.decimalDigitsOptional = decimalDigitsOptional;
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
        final int usedDecimalDigits;
        if (decimalDigits == null) {
            usedDecimalDigits = getDefaultDecimalDigits();
        } else {
            usedDecimalDigits = decimalDigits;
        }
        final String formatStr = scale.getFormat(parent, withSymbol, usedDecimalDigits, decimalDigitsOptional);
        return formatStr;
    }

    public int getDefaultDecimalDigits() {
        return scale.getDefaultDecimalDigits(parent);
    }

    public String toString(final String format) {
        final DecimalFormat formatter = Decimal.newDecimalFormatInstance(format);
        final Number value = parent.getValue(scale).getImpl().numberValue();
        final String str = formatter.format(value);
        String negativeZeroMatchStr = "-0([\\.,](0)*)?";
        if (withSymbol) {
            negativeZeroMatchStr += Pattern.quote(scale.getSymbol());
        }
        if (str.startsWith("-0") && str.matches(negativeZeroMatchStr)) {
            return Strings.removeStart(str, "-");
        } else {
            return str;
        }
    }

}
