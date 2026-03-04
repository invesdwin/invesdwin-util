package de.invesdwin.util.math.decimal.format;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.ADecimal;

@Immutable
public final class ImmutableDecimalFormat extends DecimalFormat {

    private final boolean initialized;

    public ImmutableDecimalFormat(final ImmutableDecimalFormatConfig config) {
        super(config.getFormat(), config.getSymbols());
        super.setRoundingMode(ADecimal.DEFAULT_ROUNDING_MODE);
        super.setMultiplier(config.getMultiplier());
        initialized = true;
    }

    @Deprecated
    @Override
    public void setCurrency(final Currency currency) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setCurrency(currency);
        }
    }

    @Deprecated
    @Override
    public void setDecimalFormatSymbols(final DecimalFormatSymbols newSymbols) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setDecimalFormatSymbols(newSymbols);
        }
    }

    @Deprecated
    @Override
    public void setDecimalSeparatorAlwaysShown(final boolean newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setDecimalSeparatorAlwaysShown(newValue);
        }
    }

    @Deprecated
    @Override
    public void setGroupingSize(final int newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setGroupingSize(newValue);
        }
    }

    @Deprecated
    @Override
    public void setGroupingUsed(final boolean newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setGroupingUsed(newValue);
        }
    }

    @Deprecated
    @Override
    public void setMaximumFractionDigits(final int newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setMaximumFractionDigits(newValue);
        }
    }

    @Deprecated
    @Override
    public void setMaximumIntegerDigits(final int newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setMaximumIntegerDigits(newValue);
        }
    }

    @Deprecated
    @Override
    public void setMinimumFractionDigits(final int newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setMinimumFractionDigits(newValue);
        }
    }

    @Deprecated
    @Override
    public void setMinimumIntegerDigits(final int newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setMinimumIntegerDigits(newValue);
        }
    }

    @Deprecated
    @Override
    public void setMultiplier(final int newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setMultiplier(newValue);
        }
    }

    @Deprecated
    @Override
    public void setNegativePrefix(final String newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setNegativePrefix(newValue);
        }
    }

    @Deprecated
    @Override
    public void setNegativeSuffix(final String newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setNegativeSuffix(newValue);
        }
    }

    @Deprecated
    @Override
    public void setParseBigDecimal(final boolean newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setParseBigDecimal(newValue);
        }
    }

    @Deprecated
    @Override
    public void setParseIntegerOnly(final boolean value) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setParseIntegerOnly(value);
        }
    }

    @Deprecated
    @Override
    public void setPositivePrefix(final String newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setPositivePrefix(newValue);
        }
    }

    @Deprecated
    @Override
    public void setPositiveSuffix(final String newValue) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setPositiveSuffix(newValue);
        }
    }

    @Deprecated
    @Override
    public void setRoundingMode(final RoundingMode roundingMode) {
        if (initialized) {
            throw newUnsupportedOperationException();
        } else {
            super.setRoundingMode(roundingMode);
        }
    }

    private UnsupportedOperationException newUnsupportedOperationException() {
        return new UnsupportedOperationException("Unmodifiable");
    }

}