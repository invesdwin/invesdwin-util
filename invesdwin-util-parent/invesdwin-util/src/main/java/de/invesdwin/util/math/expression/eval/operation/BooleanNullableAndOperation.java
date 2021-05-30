package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.BooleanConstantExpression;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.simple.BooleanAndOperation;
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
public class BooleanNullableAndOperation extends DoubleBinaryOperation {

    public BooleanNullableAndOperation(final IParsedExpression left, final IParsedExpression right) {
        super(Op.AND, left, right);
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> {
            final Boolean check = f.evaluateBooleanNullable(key);
            return Doubles.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> {
            final Boolean check = f.evaluateBooleanNullable(key);
            return Doubles.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> {
            final Boolean check = f.evaluateBooleanNullable();
            return Doubles.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> {
            final boolean check = f.evaluateBoolean(key);
            return Integers.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> {
            final boolean check = f.evaluateBoolean(key);
            return Integers.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> {
            final boolean check = f.evaluateBoolean();
            return Integers.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final boolean leftNullable = left.getType().isNullable();
        final boolean rightNullable = right.getType().isNullable();
        if (leftNullable && rightNullable) {
            final IEvaluateBooleanNullableFDate leftF = left.newEvaluateBooleanNullableFDate();
            final IEvaluateBooleanNullableFDate rightF = right.newEvaluateBooleanNullableFDate();
            return key -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable(key);
                if (leftResult == null) {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    return rightResult;
                } else if (!leftResult.booleanValue()) {
                    return Boolean.FALSE;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    if (rightResult == null) {
                        return Boolean.TRUE;
                    } else {
                        return rightResult;
                    }
                }
            };
        } else if (!leftNullable && rightNullable) {
            final IEvaluateBooleanFDate leftF = left.newEvaluateBooleanFDate();
            final IEvaluateBooleanNullableFDate rightF = right.newEvaluateBooleanNullableFDate();
            return key -> {
                final boolean leftResult = leftF.evaluateBoolean(key);
                if (!leftResult) {
                    return Boolean.FALSE;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    if (rightResult == null) {
                        return Boolean.TRUE;
                    } else {
                        return rightResult;
                    }
                }
            };
        } else if (leftNullable && !rightNullable) {
            final IEvaluateBooleanNullableFDate leftF = left.newEvaluateBooleanNullableFDate();
            final IEvaluateBooleanFDate rightF = right.newEvaluateBooleanFDate();
            return key -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable(key);
                if (leftResult == null) {
                    final boolean rightResult = rightF.evaluateBoolean(key);
                    return rightResult;
                } else if (!leftResult.booleanValue()) {
                    return Boolean.FALSE;
                } else {
                    final boolean rightResult = rightF.evaluateBoolean(key);
                    return rightResult;
                }
            };
        } else {
            final IEvaluateBooleanFDate leftF = left.newEvaluateBooleanFDate();
            final IEvaluateBooleanFDate rightF = right.newEvaluateBooleanFDate();
            return key -> {
                return leftF.evaluateBoolean(key) && rightF.evaluateBoolean(key);
            };
        }
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final boolean leftNullable = left.getType().isNullable();
        final boolean rightNullable = right.getType().isNullable();
        if (leftNullable && rightNullable) {
            final IEvaluateBooleanNullableKey leftF = left.newEvaluateBooleanNullableKey();
            final IEvaluateBooleanNullableKey rightF = right.newEvaluateBooleanNullableKey();
            return key -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable(key);
                if (leftResult == null) {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    return rightResult;
                } else if (!leftResult.booleanValue()) {
                    return Boolean.FALSE;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    if (rightResult == null) {
                        return Boolean.TRUE;
                    } else {
                        return rightResult;
                    }
                }
            };
        } else if (!leftNullable && rightNullable) {
            final IEvaluateBooleanKey leftF = left.newEvaluateBooleanKey();
            final IEvaluateBooleanNullableKey rightF = right.newEvaluateBooleanNullableKey();
            return key -> {
                final boolean leftResult = leftF.evaluateBoolean(key);
                if (!leftResult) {
                    return Boolean.FALSE;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    if (rightResult == null) {
                        return Boolean.TRUE;
                    } else {
                        return rightResult;
                    }
                }
            };
        } else if (leftNullable && !rightNullable) {
            final IEvaluateBooleanNullableKey leftF = left.newEvaluateBooleanNullableKey();
            final IEvaluateBooleanKey rightF = right.newEvaluateBooleanKey();
            return key -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable(key);
                if (leftResult == null) {
                    final boolean rightResult = rightF.evaluateBoolean(key);
                    return rightResult;
                } else if (!leftResult.booleanValue()) {
                    return Boolean.FALSE;
                } else {
                    final boolean rightResult = rightF.evaluateBoolean(key);
                    return rightResult;
                }
            };
        } else {
            final IEvaluateBooleanKey leftF = left.newEvaluateBooleanKey();
            final IEvaluateBooleanKey rightF = right.newEvaluateBooleanKey();
            return key -> {
                return leftF.evaluateBoolean(key) && rightF.evaluateBoolean(key);
            };
        }
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final boolean leftNullable = left.getType().isNullable();
        final boolean rightNullable = right.getType().isNullable();
        if (leftNullable && rightNullable) {
            final IEvaluateBooleanNullable leftF = left.newEvaluateBooleanNullable();
            final IEvaluateBooleanNullable rightF = right.newEvaluateBooleanNullable();
            return () -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable();
                if (leftResult == null) {
                    final Boolean rightResult = rightF.evaluateBooleanNullable();
                    return rightResult;
                } else if (!leftResult.booleanValue()) {
                    return Boolean.FALSE;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable();
                    if (rightResult == null) {
                        return Boolean.TRUE;
                    } else {
                        return rightResult;
                    }
                }
            };
        } else if (!leftNullable && rightNullable) {
            final IEvaluateBoolean leftF = left.newEvaluateBoolean();
            final IEvaluateBooleanNullable rightF = right.newEvaluateBooleanNullable();
            return () -> {
                final boolean leftResult = leftF.evaluateBoolean();
                if (!leftResult) {
                    return Boolean.FALSE;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable();
                    if (rightResult == null) {
                        return Boolean.TRUE;
                    } else {
                        return rightResult;
                    }
                }
            };
        } else if (leftNullable && !rightNullable) {
            final IEvaluateBooleanNullable leftF = left.newEvaluateBooleanNullable();
            final IEvaluateBoolean rightF = right.newEvaluateBoolean();
            return () -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable();
                if (leftResult == null) {
                    final boolean rightResult = rightF.evaluateBoolean();
                    return rightResult;
                } else if (!leftResult.booleanValue()) {
                    return Boolean.FALSE;
                } else {
                    final boolean rightResult = rightF.evaluateBoolean();
                    return rightResult;
                }
            };
        } else {
            final IEvaluateBoolean leftF = left.newEvaluateBoolean();
            final IEvaluateBoolean rightF = right.newEvaluateBoolean();
            return () -> {
                return leftF.evaluateBoolean() && rightF.evaluateBoolean();
            };
        }
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final boolean leftNullable = left.getType().isNullable();
        final boolean rightNullable = right.getType().isNullable();
        if (leftNullable && rightNullable) {
            final IEvaluateBooleanNullableFDate leftF = left.newEvaluateBooleanNullableFDate();
            final IEvaluateBooleanNullableFDate rightF = right.newEvaluateBooleanNullableFDate();
            return key -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable(key);
                if (leftResult == null) {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    return Booleans.isTrue(rightResult);
                } else if (!leftResult.booleanValue()) {
                    return false;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    if (rightResult == null) {
                        return true;
                    } else {
                        return rightResult.booleanValue();
                    }
                }
            };
        } else if (!leftNullable && rightNullable) {
            final IEvaluateBooleanFDate leftF = left.newEvaluateBooleanFDate();
            final IEvaluateBooleanNullableFDate rightF = right.newEvaluateBooleanNullableFDate();
            return key -> {
                final boolean leftResult = leftF.evaluateBoolean(key);
                if (!leftResult) {
                    return false;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    if (rightResult == null) {
                        return true;
                    } else {
                        return rightResult.booleanValue();
                    }
                }
            };
        } else if (leftNullable && !rightNullable) {
            final IEvaluateBooleanNullableFDate leftF = left.newEvaluateBooleanNullableFDate();
            final IEvaluateBooleanFDate rightF = right.newEvaluateBooleanFDate();
            return key -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable(key);
                if (leftResult == null) {
                    final boolean rightResult = rightF.evaluateBoolean(key);
                    return rightResult;
                } else if (!leftResult.booleanValue()) {
                    return false;
                } else {
                    final boolean rightResult = rightF.evaluateBoolean(key);
                    return rightResult;
                }
            };
        } else {
            final IEvaluateBooleanFDate leftF = left.newEvaluateBooleanFDate();
            final IEvaluateBooleanFDate rightF = right.newEvaluateBooleanFDate();
            return key -> {
                return leftF.evaluateBoolean(key) && rightF.evaluateBoolean(key);
            };
        }
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final boolean leftNullable = left.getType().isNullable();
        final boolean rightNullable = right.getType().isNullable();
        if (leftNullable && rightNullable) {
            final IEvaluateBooleanNullableKey leftF = left.newEvaluateBooleanNullableKey();
            final IEvaluateBooleanNullableKey rightF = right.newEvaluateBooleanNullableKey();
            return key -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable(key);
                if (leftResult == null) {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    return Booleans.isTrue(rightResult);
                } else if (!leftResult.booleanValue()) {
                    return false;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    if (rightResult == null) {
                        return true;
                    } else {
                        return rightResult.booleanValue();
                    }
                }
            };
        } else if (!leftNullable && rightNullable) {
            final IEvaluateBooleanKey leftF = left.newEvaluateBooleanKey();
            final IEvaluateBooleanNullableKey rightF = right.newEvaluateBooleanNullableKey();
            return key -> {
                final boolean leftResult = leftF.evaluateBoolean(key);
                if (!leftResult) {
                    return false;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable(key);
                    if (rightResult == null) {
                        return true;
                    } else {
                        return rightResult.booleanValue();
                    }
                }
            };
        } else if (leftNullable && !rightNullable) {
            final IEvaluateBooleanNullableKey leftF = left.newEvaluateBooleanNullableKey();
            final IEvaluateBooleanKey rightF = right.newEvaluateBooleanKey();
            return key -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable(key);
                if (leftResult == null) {
                    final boolean rightResult = rightF.evaluateBoolean(key);
                    return rightResult;
                } else if (!leftResult.booleanValue()) {
                    return false;
                } else {
                    final boolean rightResult = rightF.evaluateBoolean(key);
                    return rightResult;
                }
            };
        } else {
            final IEvaluateBooleanKey leftF = left.newEvaluateBooleanKey();
            final IEvaluateBooleanKey rightF = right.newEvaluateBooleanKey();
            return key -> {
                return leftF.evaluateBoolean(key) && rightF.evaluateBoolean(key);
            };
        }
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final boolean leftNullable = left.getType().isNullable();
        final boolean rightNullable = right.getType().isNullable();
        if (leftNullable && rightNullable) {
            final IEvaluateBooleanNullable leftF = left.newEvaluateBooleanNullable();
            final IEvaluateBooleanNullable rightF = right.newEvaluateBooleanNullable();
            return () -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable();
                if (leftResult == null) {
                    final Boolean rightResult = rightF.evaluateBooleanNullable();
                    return Booleans.isTrue(rightResult);
                } else if (!leftResult.booleanValue()) {
                    return false;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable();
                    if (rightResult == null) {
                        return true;
                    } else {
                        return rightResult.booleanValue();
                    }
                }
            };
        } else if (!leftNullable && rightNullable) {
            final IEvaluateBoolean leftF = left.newEvaluateBoolean();
            final IEvaluateBooleanNullable rightF = right.newEvaluateBooleanNullable();
            return () -> {
                final boolean leftResult = leftF.evaluateBoolean();
                if (!leftResult) {
                    return false;
                } else {
                    final Boolean rightResult = rightF.evaluateBooleanNullable();
                    if (rightResult == null) {
                        return true;
                    } else {
                        return rightResult.booleanValue();
                    }
                }
            };
        } else if (leftNullable && !rightNullable) {
            final IEvaluateBooleanNullable leftF = left.newEvaluateBooleanNullable();
            final IEvaluateBoolean rightF = right.newEvaluateBoolean();
            return () -> {
                final Boolean leftResult = leftF.evaluateBooleanNullable();
                if (leftResult == null) {
                    final boolean rightResult = rightF.evaluateBoolean();
                    return rightResult;
                } else if (!leftResult.booleanValue()) {
                    return false;
                } else {
                    final boolean rightResult = rightF.evaluateBoolean();
                    return rightResult;
                }
            };
        } else {
            final IEvaluateBoolean leftF = left.newEvaluateBoolean();
            final IEvaluateBoolean rightF = right.newEvaluateBoolean();
            return () -> {
                return leftF.evaluateBoolean() && rightF.evaluateBoolean();
            };
        }
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateFalseReasonKey() {
        final IEvaluateGenericKey<String> leftF = left.newEvaluateFalseReasonKey();
        final IEvaluateGenericKey<String> rightF = right.newEvaluateFalseReasonKey();
        return key -> {
            final String leftStr = leftF.evaluateGeneric(key);
            if (leftStr != null) {
                return leftStr;
            }
            final String rightStr = rightF.evaluateGeneric(key);
            return rightStr;
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateFalseReason() {
        final IEvaluateGeneric<String> leftF = left.newEvaluateFalseReason();
        final IEvaluateGeneric<String> rightF = right.newEvaluateFalseReason();
        return () -> {
            final String leftStr = leftF.evaluateGeneric();
            if (leftStr != null) {
                return leftStr;
            }
            final String rightStr = rightF.evaluateGeneric();
            return rightStr;
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateFalseReasonFDate() {
        final IEvaluateGenericFDate<String> leftF = left.newEvaluateFalseReasonFDate();
        final IEvaluateGenericFDate<String> rightF = right.newEvaluateFalseReasonFDate();
        return key -> {
            final String leftStr = leftF.evaluateGeneric(key);
            if (leftStr != null) {
                return leftStr;
            }
            final String rightStr = rightF.evaluateGeneric(key);
            return rightStr;
        };
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public IParsedExpression simplify() {
        final IParsedExpression newLeft = left.simplify();
        final IParsedExpression newRight = right.simplify();
        if (newLeft.isConstant()) {
            final Boolean leftResult = newLeft.newEvaluateBooleanNullable().evaluateBooleanNullable();
            if (leftResult == null || leftResult == Boolean.TRUE) {
                if (newRight.isConstant()) {
                    final Boolean rightResult = newRight.newEvaluateBooleanNullable().evaluateBooleanNullable();
                    if (rightResult != null) {
                        return new ConstantExpression(Doubles.fromBoolean(rightResult),
                                ExpressionType.determineSmallestBooleanType(rightResult));
                    } else {
                        return new ConstantExpression(Doubles.fromBoolean(leftResult),
                                ExpressionType.determineSmallestBooleanType(leftResult));
                    }
                } else {
                    return newRight;
                }
            } else {
                return BooleanConstantExpression.FALSE;
            }
        }
        if (newRight.isConstant()) {
            final Boolean rightResult = newRight.newEvaluateBooleanNullable().evaluateBooleanNullable();
            if (rightResult == null || rightResult == Boolean.TRUE) {
                if (newLeft.isConstant()) {
                    final Boolean leftResult = newLeft.newEvaluateBooleanNullable().evaluateBooleanNullable();
                    if (leftResult != null) {
                        return new ConstantExpression(Doubles.fromBoolean(leftResult),
                                ExpressionType.determineSmallestBooleanType(leftResult));
                    } else {
                        return new ConstantExpression(Doubles.fromBoolean(rightResult),
                                ExpressionType.determineSmallestBooleanType(rightResult));
                    }
                } else {
                    return newLeft;
                }
            } else {
                return BooleanConstantExpression.FALSE;
            }
        }
        return simplify(newLeft, newRight);
    }

    @Override
    protected IBinaryOperation newBinaryOperation(final IParsedExpression left, final IParsedExpression right) {
        final ExpressionType simplifyType = op.simplifyType(left, right);
        if (simplifyType == null) {
            return new BooleanNullableAndOperation(left, right);
        } else if (simplifyType == ExpressionType.Boolean) {
            return new BooleanAndOperation(left, right);
        } else {
            throw UnknownArgumentException.newInstance(ExpressionType.class, simplifyType);
        }
    }

}
