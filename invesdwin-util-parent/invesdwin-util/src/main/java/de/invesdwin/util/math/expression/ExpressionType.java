package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.eval.IParsedExpression;

@Immutable
public enum ExpressionType {
    Double(4),
    Integer(3),
    BooleanNullable(2),
    Boolean(1);

    private final int size;

    ExpressionType(final int size) {
        this.size = size;
    }

    public static ExpressionType determineType(final ExpressionType defaultType,
            final IParsedExpression... expressions) {
        ExpressionType common = null;
        for (int i = 0; i < expressions.length; i++) {
            if (common == null) {
                common = expressions[i].getType();
            } else {
                final boolean type = common != expressions[i].getType();
                if (type) {
                    return defaultType;
                }
            }
        }
        if (common != null) {
            return common;
        } else {
            return defaultType;
        }
    }

    public static ExpressionType simplifyReturnType(final ExpressionType defaultType,
            final IParsedExpression... expressions) {
        return simplifyType(defaultType, determineType(defaultType, expressions));
    }

    public static ExpressionType simplifyType(final ExpressionType defaultType, final ExpressionType commonType) {
        switch (defaultType) {
        case Boolean:
            //we treat everything as boolean
            return ExpressionType.Boolean;
        case BooleanNullable:
            switch (commonType) {
            case Double:
            case BooleanNullable:
                //we can simplify to boolean nullable
                return ExpressionType.BooleanNullable;
            case Integer:
            case Boolean:
                //we can simplify to boolean
                return ExpressionType.Boolean;
            default:
                throw UnknownArgumentException.newInstance(ExpressionType.class, commonType);
            }
        case Double:
            switch (commonType) {
            case Double:
            case BooleanNullable:
                //nothing to simplify
                return ExpressionType.Double;
            case Integer:
            case Boolean:
                //we can simplify to integer
                return ExpressionType.Integer;
            default:
                throw UnknownArgumentException.newInstance(ExpressionType.class, commonType);
            }
        case Integer:
            //nothing to simplify
            return ExpressionType.Integer;
        default:
            throw UnknownArgumentException.newInstance(ExpressionType.class, defaultType);
        }
    }

    public static ExpressionType determineSmallestDecimalType(final double value) {
        final double roundedValue = Doubles.round(value);
        if (Doubles.isNaN(roundedValue)) {
            return ExpressionType.BooleanNullable;
        } else if (Doubles.isInteger(roundedValue)) {
            final int intValue = (int) roundedValue;
            if (intValue == 1 || intValue == 0) {
                return ExpressionType.Boolean;
            } else {
                return ExpressionType.Integer;
            }
        } else {
            return ExpressionType.Double;
        }
    }

    public static ExpressionType determineSmallestBooleanType(final Boolean result) {
        if (result == null) {
            return ExpressionType.BooleanNullable;
        } else {
            return ExpressionType.Boolean;
        }
    }

    public boolean isSmallerThanOrEqualTo(final ExpressionType type) {
        return size <= type.size;
    }
}
