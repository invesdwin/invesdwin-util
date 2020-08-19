package de.invesdwin.util.math.expression;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.time.fdate.IFDateProvider;

public interface IExpression {

    IExpression[] EMPTY_EXPRESSIONS = new IExpression[0];

    /**
     * evaluates the expression using the current time key
     */
    double evaluateDouble(IFDateProvider key);

    /**
     * evaluates the expression using the current int key
     */
    double evaluateDouble(int key);

    /**
     * evaluates the expression using the current available time/int key
     */
    double evaluateDouble();

    /**
     * Double.NaN is interpreted as 0.
     */
    default int evaluateInteger(final IFDateProvider key) {
        return Integers.checkedCast(evaluateDouble(key));
    }

    /**
     * Double.NaN is interpreted as 0.
     */
    default int evaluateInteger(final int key) {
        return Integers.checkedCast(evaluateDouble(key));
    }

    /**
     * Double.NaN is interpreted as 0.
     */
    default int evaluateInteger() {
        return Integers.checkedCast(evaluateDouble());
    }

    /**
     * Double.NaN is interpreted as false.
     */
    boolean evaluateBoolean();

    /**
     * Double.NaN is interpreted as false.
     */
    boolean evaluateBoolean(IFDateProvider key);

    /**
     * Double.NaN is interpreted as false.
     */
    boolean evaluateBoolean(int key);

    /**
     * Double.NaN is interpreted as null.
     */
    Boolean evaluateBooleanNullable();

    /**
     * Double.NaN is interpreted as null.
     */
    Boolean evaluateBooleanNullable(IFDateProvider key);

    /**
     * Double.NaN is interpreted as null.
     */
    Boolean evaluateBooleanNullable(int key);

    boolean isConstant();

    String getContext();

    /**
     * Return true if values are only availble point in time without history. E.g. dependant on active orders and thus
     * should be persisted for charts.
     */
    boolean shouldPersist();

    /**
     * Return true if this expression can be drawn. This might be false for command expressions that always return NaN.
     * In that case the children might be drawn.
     */
    boolean shouldDraw();

    IExpression[] getChildren();

}
