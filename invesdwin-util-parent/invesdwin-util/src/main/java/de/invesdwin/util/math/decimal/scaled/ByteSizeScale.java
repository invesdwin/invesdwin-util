package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.string.Strings;
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

    private static final double MULTIPLICATOR_1024 = 1024D;
    private static final int SCALE_1024 = new Decimal(MULTIPLICATOR_1024).getDigits() - 1;

    private String text;
    private int multiplesOf1024;
    private String symbol;

    ByteSizeScale(final String text, final int multiplesOf1000, final String symbol) {
        this.text = text;
        this.multiplesOf1024 = multiplesOf1000;
        this.symbol = symbol;
    }

    public int getMultiplesOf1024() {
        return multiplesOf1024;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public double convertValue(final ByteSize parent, final double value, final ByteSizeScale scale) {
        return convert(value, scale);
    }

    public double convert(final double value, final ByteSizeScale scale) {
        final double byteValue = value * Doubles.pow(MULTIPLICATOR_1024, scale.getMultiplesOf1024());
        return byteValue / Doubles.pow(MULTIPLICATOR_1024, getMultiplesOf1024());
    }

    @Override
    public int getDefaultDecimalDigits(final ByteSize parent) {
        return getMultiplesOf1024() * SCALE_1024;
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
