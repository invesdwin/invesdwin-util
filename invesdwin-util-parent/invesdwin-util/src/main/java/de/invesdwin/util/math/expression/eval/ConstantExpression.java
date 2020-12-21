package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateGeneric;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;

@Immutable
public class ConstantExpression implements IParsedExpression {

    private final ExpressionType type;
    private final double doubleValue;
    private final int intValue;
    private final Boolean booleanNullableValue;
    private final boolean booleanValue;

    public ConstantExpression(final double value) {
        this(value, ExpressionType.determineSmallestDecimalType(value));
    }

    public ConstantExpression(final double value, final ExpressionType type) {
        this.type = type;
        this.doubleValue = value;
        this.intValue = Integers.checkedCastNoOverflow(value);
        this.booleanNullableValue = Doubles.toBooleanNullable(value);
        this.booleanValue = Doubles.toBoolean(value);
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        return key -> doubleValue;
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        return key -> doubleValue;
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        return () -> doubleValue;
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        return key -> intValue;
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        return key -> intValue;
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        return () -> intValue;
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        return key -> booleanNullableValue;
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        return key -> booleanNullableValue;
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        return () -> booleanNullableValue;
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        return key -> booleanValue;
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        return key -> booleanValue;
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        return () -> booleanValue;
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateFalseReasonKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> {
            if (Booleans.isFalse(f.evaluateBooleanNullable(key))) {
                return ConstantExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateFalseReason() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> {
            if (Booleans.isFalse(f.evaluateBooleanNullable())) {
                return ConstantExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateFalseReasonFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> {
            if (Booleans.isFalse(f.evaluateBooleanNullable(key))) {
                return ConstantExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateTrueReasonKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> {
            if (Booleans.isTrue(f.evaluateBooleanNullable(key))) {
                return ConstantExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateTrueReason() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> {
            if (Booleans.isTrue(f.evaluateBooleanNullable())) {
                return ConstantExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> {
            if (Booleans.isTrue(f.evaluateBooleanNullable(key))) {
                return ConstantExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateNullReasonKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> {
            if (Objects.isNull(f.evaluateBooleanNullable(key))) {
                return ConstantExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateNullReason() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> {
            if (Objects.isNull(f.evaluateBooleanNullable())) {
                return ConstantExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateNullReasonFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> {
            if (Objects.isNull(f.evaluateBooleanNullable(key))) {
                return ConstantExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
        return new Decimal(doubleValue).toString();
    }

    @Override
    public IParsedExpression simplify() {
        return this;
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ConstantExpression) {
            final ConstantExpression cObj = (ConstantExpression) obj;
            return doubleValue == cObj.doubleValue;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ConstantExpression.class.hashCode() + Double.hashCode(doubleValue);
    }

    @Override
    public boolean shouldPersist() {
        return false;
    }

    @Override
    public boolean shouldDraw() {
        return true;
    }

    @Override
    public IExpression[] getChildren() {
        return EMPTY_EXPRESSIONS;
    }

    @Override
    public ExpressionType getType() {
        return type;
    }

}
