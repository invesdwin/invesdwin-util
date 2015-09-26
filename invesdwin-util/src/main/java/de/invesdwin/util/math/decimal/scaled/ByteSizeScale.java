package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public enum ByteSizeScale implements IDecimalScale<ByteSize, ByteSizeScale> {
    BYTES("Bytes", 0, "B"),
    KILOBYTES("KiloBytes", 1, "KB"),
    MEGABYTES("MegaBytes", 2, "MB"),
    GIGABYTES("GigaBytes", 3, "GB"),
    TERABYTES("TeraBytes", 4, "TB"),
    PETABYTES("PetaBytes", 5, "PB");

    private static final Decimal MULTIPLICATOR_1024 = new Decimal("1024");
    private static final int SCALE_1024 = MULTIPLICATOR_1024.getDigits() - 1;

    private String text;
    private int multiplesOf1024;
    private String symbol;

    ByteSizeScale(final String text, final int multiplesOf1024, final String symbol) {
        this.text = text;
        this.multiplesOf1024 = multiplesOf1024;
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
    public Decimal convertValue(final ByteSize parent, final Decimal value, final ByteSizeScale scale) {
        final Decimal byteValue = value.multiply(MULTIPLICATOR_1024.pow(scale.getMultiplesOf1024()));
        return byteValue.divide(MULTIPLICATOR_1024.pow(getMultiplesOf1024()));
    }

    @Override
    public String getFormat(final ByteSize parent, final boolean withSymbol) {
        final int scale = getMultiplesOf1024() * SCALE_1024;
        String format = ",##0";
        if (scale > 0) {
            format += "." + Strings.repeat("#", scale);
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
