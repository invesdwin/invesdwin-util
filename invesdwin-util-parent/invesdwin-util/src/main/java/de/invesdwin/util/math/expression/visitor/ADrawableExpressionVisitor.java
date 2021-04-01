package de.invesdwin.util.math.expression.visitor;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.IExpression;

@Immutable
public abstract class ADrawableExpressionVisitor extends AExpressionVisitor {

    @Override
    public void process(final IExpression expression) {
        final IExpression e;
        if (expression.getChildren().length == 1) {
            e = ExpressionProperties.getDrawable(expression);
        } else {
            e = expression;
        }
        super.process(e);
    }

}
