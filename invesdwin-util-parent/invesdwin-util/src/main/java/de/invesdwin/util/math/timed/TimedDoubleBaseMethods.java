package de.invesdwin.util.math.timed;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.decimal.Decimal;

@Immutable
public final class TimedDoubleBaseMethods {

    private TimedDoubleBaseMethods() {}

    public static boolean equals(final ITimedDouble thisObj, final Object obj) {
        if (obj instanceof ITimedDouble) {
            final ITimedDouble cObj = (ITimedDouble) obj;
            return Objects.equals(thisObj.getValue(), cObj.getValue());
        } else if (obj instanceof Number) {
            final Number cObj = (Number) obj;
            return Objects.equals(thisObj.getValue(), cObj.doubleValue());
        } else {
            return false;
        }
    }

    public static int hashCode(final ITimedDouble thisObj) {
        return Objects.hashCode(ITimedDouble.class, thisObj.getValue());
    }

    public static String toString(final ITimedDouble thisObj) {
        return thisObj.getTime() + ":" + Decimal.toString(thisObj.getValue());
    }

}
