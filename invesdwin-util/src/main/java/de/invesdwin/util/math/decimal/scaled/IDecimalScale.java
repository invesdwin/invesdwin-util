package de.invesdwin.util.math.decimal.scaled;

import de.invesdwin.util.math.decimal.AScaledDecimal;
import de.invesdwin.util.math.decimal.Decimal;

public interface IDecimalScale<T extends AScaledDecimal<T, S>, S extends IDecimalScale<T, S>> {

    Decimal convertValue(T parent, final Decimal value, final S scale);

    int getDefaultDecimalDigits(T parent);

    String getFormat(T parent, boolean withSymbol, int decimalDigits, boolean decimalDigitsOptional);

    String getSymbol();

}
