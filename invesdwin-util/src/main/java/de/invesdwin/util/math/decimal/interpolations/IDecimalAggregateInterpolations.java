package de.invesdwin.util.math.decimal.interpolations;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.interpolations.config.BSplineInterpolationConfig;
import de.invesdwin.util.math.decimal.interpolations.config.InterpolationConfig;
import de.invesdwin.util.math.decimal.interpolations.config.LoessInterpolationConfig;

public interface IDecimalAggregateInterpolations<E extends ADecimal<E>> {

    IDecimalAggregate<E> loess(LoessInterpolationConfig config);

    IDecimalAggregate<E> bSpline(BSplineInterpolationConfig config);

    IDecimalAggregate<E> cubicBSpline(InterpolationConfig config);

    /**
     * bezier is fast O(n) but cannot calculate value sizes larger than 1030. You might want to consider to fallback to
     * an equivalent variation of BSpline with degree n, which sadly is exponentially slower with O(2^n) and might thus
     * not even complete in your lifetime. So we are automatically reducing the points by an averaging algorithm as a
     * preprocessing filter to get down to a maximum of 1000 points.
     */
    IDecimalAggregate<E> bezierCurve(InterpolationConfig config);

}
