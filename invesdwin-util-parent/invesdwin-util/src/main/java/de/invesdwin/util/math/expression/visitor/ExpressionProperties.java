package de.invesdwin.util.math.expression.visitor;

import java.util.Collection;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.expression.IExpression;

@Immutable
public final class ExpressionProperties {

    /**
     * Returns true when a function is used that enters orders as a side effect. E.g. enterLongAtMarket(...)
     * 
     * DEFAULT when undefined is false.
     */
    public static final String ENTER_ORDER = "ENTER_ORDER";
    /**
     * Returns true when a function is used that acceses ITechnicalAnalysisLastTrades queries
     * 
     * DEFAULT when undefined is false.
     */
    public static final String LAST_TRADES = "LAST_TRADES";
    /**
     * Return true if this variable does not depend on an outside context, e.g. current trade information, strategy
     * samples or values from other indicators.
     * 
     * When this is true, results might be cached and compressed as double/integer/boolean arrays or bitsets.
     * 
     * DEFAULT when undefined is true.
     */
    public static final String COMPRESS = "COMPRESS";
    /**
     * Return true if this expression can be drawn. This might be false for command expressions that always return NaN.
     * In that case the children might be drawn.
     * 
     * DEFAULT when undefined is true.
     */
    public static final String DRAW = "DRAW";
    /**
     * Return true if values are only availble point in time without history. E.g. dependant on active orders and thus
     * should be persisted for charts.
     * 
     * DEFAULT when undefined is false.
     */
    public static final String PERSIST = "PERSIST";
    /**
     * Return true if this is a parameter that can be optimized.
     * 
     * DEFAULT when undefined is false.
     */
    public static final String OPTIMIZE = "OPTIMIZE";

    /**
     * An event that update caches based on optimization variables that have been modified
     */
    public static final String AFTER_OPTIMIZATION_MODIFICATION = "AFTER_OPTIMIZATION_MODIFICATION";

    /**
     * Return a single or a collection of optimization variables.
     * 
     * DEFAULT when undefined is an empty collection.
     */
    public static final String OPTIMIZATION_VARIABLES = "OPTIMIZATION_VARIABLES";

    private ExpressionProperties() {
    }

    public static boolean isCompress(final IExpression expression) {
        if (Booleans.isFalse((Boolean) expression.getProperty(COMPRESS))) {
            return false;
        }
        final IExpression[] parameters = expression.getChildren();
        for (int i = 0; i < parameters.length; i++) {
            if (!isCompress(parameters[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPersist(final IExpression expression) {
        if (Booleans.isTrue((Boolean) expression.getProperty(PERSIST))) {
            return true;
        }
        final IExpression[] parameters = expression.getChildren();
        for (int i = 0; i < parameters.length; i++) {
            if (isPersist(parameters[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEnterOrder(final IExpression expression) {
        if (Booleans.isTrue((Boolean) expression.getProperty(ENTER_ORDER))) {
            return true;
        }
        final IExpression[] parameters = expression.getChildren();
        for (int i = 0; i < parameters.length; i++) {
            if (isEnterOrder(parameters[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLastTrades(final IExpression expression) {
        if (Booleans.isTrue((Boolean) expression.getProperty(LAST_TRADES))) {
            return true;
        }
        final IExpression[] parameters = expression.getChildren();
        for (int i = 0; i < parameters.length; i++) {
            if (isLastTrades(parameters[i])) {
                return true;
            }
        }
        return false;
    }

    public static IExpression getDrawable(final IExpression expression) {
        if (Booleans.isFalse((Boolean) expression.getProperty(ExpressionProperties.DRAW))) {
            final IExpression[] children = expression.getChildren();
            if (children.length == 1) {
                final IExpression child = children[0];
                final IExpression drawable = getDrawable(child);
                if (drawable != null) {
                    return drawable;
                }
            } else {
                return null;
            }
        }
        return expression;
    }

    public static boolean isOptimize(final IExpression expression) {
        if (Booleans.isTrue((Boolean) expression.getProperty(OPTIMIZE))) {
            return true;
        }
        final IExpression[] parameters = expression.getChildren();
        for (int i = 0; i < parameters.length; i++) {
            if (isOptimize(parameters[i])) {
                return true;
            }
        }
        return false;
    }

    public static void afterOptimizationModification(final IExpression expression) {
        expression.getProperty(AFTER_OPTIMIZATION_MODIFICATION);
        final IExpression[] parameters = expression.getChildren();
        for (int i = 0; i < parameters.length; i++) {
            afterOptimizationModification(parameters[i]);
        }
    }

    public static <T> Set<T> getOptimizationVariables(final IExpression expression) {
        final Set<T> set = ILockCollectionFactory.getInstance(false).newLinkedSet();
        getOptimizationVariables(expression, set);
        return set;
    }

    @SuppressWarnings("unchecked")
    public static <T> void getOptimizationVariables(final IExpression expression, final Set<T> optimizationVariables) {
        final Object value = expression.getProperty(OPTIMIZATION_VARIABLES);
        if (value != null) {
            if (value instanceof Object[]) {
                final Object[] arr = (Object[]) value;
                for (int i = 0; i < arr.length; i++) {
                    final T cValue = (T) arr[i];
                    optimizationVariables.add(cValue);
                }
            } else if (value instanceof Collection) {
                final Collection<T> col = (Collection<T>) value;
                optimizationVariables.addAll(col);
            } else {
                final T cValue = (T) value;
                optimizationVariables.add(cValue);
            }
        }
        final IExpression[] parameters = expression.getChildren();
        for (int i = 0; i < parameters.length; i++) {
            getOptimizationVariables(parameters[i], optimizationVariables);
        }
    }

}
