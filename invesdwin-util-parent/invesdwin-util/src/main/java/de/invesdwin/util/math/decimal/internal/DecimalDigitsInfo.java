package de.invesdwin.util.math.decimal.internal;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Shorts;

/**
 * Especially not marked as serializable, this type should be instantiated lazily and stored in a transient field.
 */
@ThreadSafe
public class DecimalDigitsInfo {

    @GuardedBy("none for performance")
    private short wholeNumberDigits = -1;
    @GuardedBy("none for performance")
    private short decimalDigits = -1;
    @GuardedBy("none for performance")
    private short digits = -1;
    private final String toString;

    public DecimalDigitsInfo(final String internalToString) {
        if (internalToString.length() > 1 && internalToString.contains(".")) {
            toString = Strings.removeEnd(Strings.removeTrailing(internalToString, "0"), ".");
        } else {
            toString = internalToString;
        }
    }

    public int getWholeNumberDigits() {
        if (isDigitsNull(wholeNumberDigits)) {
            /*
             * using string operations here because values get distorted even for BigDecimal when using
             * scaleByPowerOfTen
             */
            final String s = toString();
            final int digits = getWholeNumberDigits(s);
            wholeNumberDigits = encodeDigits(digits);
            return digits;
        } else {
            return decodeDigits(wholeNumberDigits);
        }
    }

    private int decodeDigits(final short digits) {
        return Integers.checkedCast(digits);
    }

    private short encodeDigits(final int digits) {
        return Shorts.checkedCast(digits);
    }

    private boolean isDigitsNull(final short digits) {
        return digits == -1;
    }

    /**
     * Returns the real scale without trailing zeros.
     */
    public int getDecimalDigits() {
        if (isDigitsNull(decimalDigits)) {
            /*
             * using string operations here because values get distorted even for BigDecimal when using
             * scaleByPowerOfTen
             */
            final String s = toString();
            final int digits = getDecimalDigits(s);
            decimalDigits = encodeDigits(digits);
            return digits;
        } else {
            return decodeDigits(decimalDigits);
        }
    }

    public int getDigits() {
        if (isDigitsNull(digits)) {
            /*
             * using string operations here because values get distorted even for BigDecimal when using
             * scaleByPowerOfTen
             */
            final String s = toString();
            final int digits = getDigits(s);
            this.digits = encodeDigits(digits);
            return digits;
        } else {
            return decodeDigits(digits);
        }
    }

    @Override
    public String toString() {
        return toString;
    }

    private int getDigits(final String s) {
        final int indexOfDecimalPoint = s.indexOf(".");
        final int digits;
        if (indexOfDecimalPoint != -1) {
            digits = s.length() - 1;
        } else {
            digits = Math.max(1, s.length());
        }
        return digits;
    }

    private int getWholeNumberDigits(final String s) {
        final int indexOfDecimalPoint = s.indexOf(".");
        final int digits;
        if (indexOfDecimalPoint != -1) {
            digits = indexOfDecimalPoint;
        } else {
            digits = Math.max(1, s.length());
        }
        return digits;
    }

    private int getDecimalDigits(final String s) {
        final int indexOfDecimalPoint = s.indexOf(".");
        final int digits;
        if (indexOfDecimalPoint != -1) {
            digits = s.length() - indexOfDecimalPoint - 1;
        } else {
            digits = 0;
        }
        return digits;
    }

}
