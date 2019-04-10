package de.invesdwin.util.swing.spinner;

import de.invesdwin.util.math.decimal.Decimal;

public interface ISpinnerDecimalModelValidator {
    boolean isValidValue(Decimal value);
}