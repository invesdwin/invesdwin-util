package de.invesdwin.util.math.decimal.scaled;

import de.invesdwin.util.math.decimal.AScaledDecimal;

public interface IDecimalScale<T extends AScaledDecimal<T, S>, S extends IDecimalScale<T, S>> {

    double convertValue(T parent, double value, S scale);

    int getDefaultDecimalDigits(T parent);

    String getFormat(T parent, boolean withSymbol, int decimalDigits, boolean decimalDigitsOptional);

    String getSymbol();

}
