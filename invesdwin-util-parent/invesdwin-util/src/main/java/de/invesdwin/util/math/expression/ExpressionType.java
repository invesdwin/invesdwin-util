package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.eval.IParsedExpression;

@Immutable
public enum ExpressionType {
    Double,
    Integer,
    Boolean,
    BooleanNullable;

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

    public static ExpressionType determineDecimalType(final double value) {
        if (Doubles.isInteger(Doubles.round(value))) {
            return ExpressionType.Integer;
        } else {
            return ExpressionType.Double;
        }
    }

    public static ExpressionType determineBooleanType(final Boolean result) {
        if (result == null) {
            return ExpressionType.BooleanNullable;
        } else {
            return ExpressionType.Boolean;
        }
    }
}
