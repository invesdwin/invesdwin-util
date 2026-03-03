package de.invesdwin.util.math.decimal.format;

import java.text.DecimalFormatSymbols;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;

@Immutable
public final class ImmutableDecimalFormatConfig {

    private final String format;
    private final DecimalFormatSymbols symbols;
    private final int multiplier;
    private final int hashCode;

    public ImmutableDecimalFormatConfig(final String format, final DecimalFormatSymbols symbols, final int multiplier) {
        this.format = format;
        this.symbols = symbols;
        this.multiplier = multiplier;
        this.hashCode = newHashCode();
    }

    private int newHashCode() {
        return Objects.hashCode(format, symbols, multiplier);
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

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ImmutableDecimalFormatConfig) {
            final ImmutableDecimalFormatConfig cObj = (ImmutableDecimalFormatConfig) obj;
            return Objects.equals(format, cObj.format) && Objects.equals(symbols, cObj.symbols)
                    && multiplier == cObj.multiplier;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("format", format)
                .add("symbols", symbols)
                .add("multiplier", multiplier)
                .toString();
    }

}
