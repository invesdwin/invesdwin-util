package de.invesdwin.util.math.decimal.format;

import java.text.DecimalFormatSymbols;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ImmutableDecimalFormatConfig {

    private final String format;
    private final DecimalFormatSymbols symbols;
    private final int multiplier;

    public ImmutableDecimalFormatConfig(final String format, final DecimalFormatSymbols symbols, final int multiplier) {
        this.format = format;
        this.symbols = symbols;
        this.multiplier = multiplier;
    }

    public String getFormat() {
        return format;
    }

    public DecimalFormatSymbols getSymbols() {
        return symbols;
    }

    public int getMultiplier() {
        return multiplier;
    }

}
