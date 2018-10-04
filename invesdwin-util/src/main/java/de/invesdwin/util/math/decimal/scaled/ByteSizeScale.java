package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public enum ByteSizeScale implements IDecimalScale<ByteSize, ByteSizeScale> {
    BYTES("Bytes", 0, "B"),
    KILOBYTES("KiloBytes", 1, "KB"),
    MEGABYTES("MegaBytes", 2, "MB"),
    GIGABYTES("GigaBytes", 3, "GB"),
    TERABYTES("TeraBytes", 4, "TB"),
    PETABYTES("PetaBytes", 5, "PB");

    private static final double MULTIPLICATOR_1000 = 1000D;
    private static final int SCALE_1000 = new Decimal(MULTIPLICATOR_1000).getDigits() - 1;

    private String text;
    private int multiplesOf1000;
    private String symbol;

    ByteSizeScale(final String text, final int multiplesOf1000, final String symbol) {
        this.text = text;
        this.multiplesOf1000 = multiplesOf1000;
        this.symbol = symbol;
    }

    public int getMultiplesOf1000() {
        return multiplesOf1000;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public double convertValue(final ByteSize parent, final double value, final ByteSizeScale scale) {
        final double byteValue = value * Doubles.pow(MULTIPLICATOR_1000, scale.getMultiplesOf1000());
        return byteValue / Doubles.pow(MULTIPLICATOR_1000, getMultiplesOf1000());
    }

    @Override
    public int getDefaultDecimalDigits(final ByteSize parent) {
        return getMultiplesOf1000() * SCALE_1000;
    }

    @Override
    public String getFormat(final ByteSize parent, final boolean withSymbol, final int decimalDigits,
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
