package de.invesdwin.util.math.doubles;

public interface IFDoubleScale<T extends AScaledFDouble<T, S>, S extends IFDoubleScale<T, S>> {

    double convertValue(T parent, double value, S scale);

    int getDefaultDecimalDigits(T parent);

    String getFormat(T parent, boolean withSymbol, int decimalDigits, boolean decimalDigitsOptional);

    String getSymbol();

}
