package de.invesdwin.util.math.decimal.format;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.ADecimal;

@Immutable
public final class ImmutableDecimalFormat extends DecimalFormat {

    public ImmutableDecimalFormat(final ImmutableDecimalFormatConfig config) {
        super(config.getFormat(), config.getSymbols());
        super.setRoundingMode(ADecimal.DEFAULT_ROUNDING_MODE);
        super.setMultiplier(config.getMultiplier());
    }

    @Deprecated
    @Override
    public void setCurrency(final Currency currency) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setDecimalFormatSymbols(final DecimalFormatSymbols newSymbols) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setDecimalSeparatorAlwaysShown(final boolean newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setGroupingSize(final int newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setGroupingUsed(final boolean newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setMaximumFractionDigits(final int newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setMaximumIntegerDigits(final int newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setMinimumFractionDigits(final int newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setMinimumIntegerDigits(final int newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setMultiplier(final int newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setNegativePrefix(final String newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setNegativeSuffix(final String newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setParseBigDecimal(final boolean newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setParseIntegerOnly(final boolean value) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setPositivePrefix(final String newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setPositiveSuffix(final String newValue) {
        throw newUnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setRoundingMode(final RoundingMode roundingMode) {
        throw newUnsupportedOperationException();
    }

    private UnsupportedOperationException newUnsupportedOperationException() {
        return new UnsupportedOperationException("Unmodifiable");
    }

}